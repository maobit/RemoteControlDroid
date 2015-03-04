package com.qhe.pcontroldroid.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qhe.pcontroldroid.R;
import com.qhe.pcontroldroid.models.ConnectionManager;
import com.qhe.pcontroldroid.models.RemoteFile;
import com.qhe.pcontroldroid.models.RemoteFileLab;
import com.qhe.pcontroldroid.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sunshine on 15-3-3.
 */
public class FileTransferFragment extends ListFragment {
    private ArrayList<RemoteFile> mFiles;
    private String mRequestDir;
    public static final String EXTRA_REQUEST_DIR = "com.qhe.pcontroldroid.fragments.FileTransferFragment.req_dir";
    private static final String TAG = "FileTransferFragment";
    private ProgressDialog mProgressDialog;

    private FragmentManager mFragmentManager;
    private RemoteFileAdapter adapter;

    private View mProgressBarContainer;
    private TextView mLoadingTextView;


    public static FileTransferFragment newInstance(String requestDir) {
        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST_DIR, requestDir);
        FileTransferFragment fragment = new FileTransferFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestDir = getArguments().getString(EXTRA_REQUEST_DIR);

        mFiles = new ArrayList<RemoteFile>();
        // get remote file list
        new FetchRemoteFileListTask().execute(mRequestDir);

        adapter = new RemoteFileAdapter(mFiles);
        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_transfer, container, false);

        mProgressBarContainer = view.findViewById(R.id.progressbarContainer);
        mLoadingTextView = (TextView) view.findViewById(R.id.tvMessage);

        ListView listView = (ListView) view.findViewById(android.R.id.list);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mFragmentManager = getActivity().getSupportFragmentManager();
        final RemoteFile file = ((RemoteFileAdapter)getListAdapter()).getItem(position);

        /**
         * 根据是否是文件夹进行相应的操作
         * 1、如果是文件，弹出一个对话框询问用户的操作：远程打开，传输到本地
         * 2、如果是文件夹就新开一个Fragment
         */

        if(file.getFileType().equals("folder")) {
            FileTransferFragment fragment = FileTransferFragment.newInstance(file.getAbsPath());
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(file.getAbsPath()).commit();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("文件选项").setMessage("文件： " + file.getFileName() + "\n" + "大小： " + FileUtils.formatFileSize(file.getFileSize()));
            builder.setNegativeButton("取消", null);
            builder.setNeutralButton("远程打开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new TransferFileTask().execute("OPEN%$%" + file.getAbsPath());
                }
            });
            builder.setPositiveButton("传输到本地", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /**
                     * 弹出一个进度条对话框，显示文件传输的进度
                     */
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setTitle("文件传输中...");
                    mProgressDialog.setIcon(R.drawable.icon_file);
                    mProgressDialog.setMessage("文件： " + file.getFileName() + "\n" + "大小： " + FileUtils.formatFileSize(file.getFileSize()));
                    mProgressDialog.setProgress(0);
                    mProgressDialog.setMax(100);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    new TransferFileTask().execute("TRANS%$%" + file.getAbsPath(), file.getFileSize() + "");
                }
            });
            builder.show();
        }
    }



    private class RemoteFileAdapter extends ArrayAdapter<RemoteFile> {
        public RemoteFileAdapter(ArrayList<RemoteFile> files) {
            super(getActivity(), 0, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_file, null);
            }
            RemoteFile file = getItem(position);

            ImageView fileIcon = (ImageView) convertView.findViewById(R.id.imgFileIcon);
            if(file.getFileType().equals("folder")) {
                fileIcon.setImageResource(R.drawable.icon_folder);
            }
            else {
                fileIcon.setImageResource(R.drawable.icon_file);
            }

            TextView fileName = (TextView) convertView.findViewById(R.id.tvFileName);
            fileName.setText(file.getFileName());

            TextView fileDetails = (TextView) convertView.findViewById(R.id.tvFileDetails);
            if(file.getFileType().equals("file")) {
                fileDetails.setText(FileUtils.formatFileSize(file.getFileSize()));
            } else {
                if(file.getFileContainItems() == 0)
                    fileDetails.setText("Empty folder");
                else
                    fileDetails.setText(file.getFileContainItems() + " items");
            }

            return convertView;
        }
    }


    /**
     * 获取计算机上的目录以及文件的信息
     */
    private class FetchRemoteFileListTask extends AsyncTask<String, Void, ArrayList<RemoteFile>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RemoteFile> doInBackground(String... params) {
            Socket socket;
            OutputStream os;
            InputStream in;
            BufferedReader reader;
            JSONArray array = null;
            StringBuilder jsonString = null;
            try {
                // send the directory name to server
                socket = new Socket(ConnectionManager.get(getActivity()).getServerIP(), 2016);
                os = socket.getOutputStream();
                os.write(params[0].getBytes());

                // read file list
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                jsonString = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                reader.close();

            } catch (Exception e) {
                Log.i(TAG, "Connection error, " + e);
            }

            if(jsonString != null) {
                try {
                    array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new RemoteFileLab(array).getFiles();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<RemoteFile> aVoid) {
            Object[] remoteFileObj = aVoid.toArray();
            Arrays.sort(remoteFileObj);

            for(int i=0; i<remoteFileObj.length; i++) {
                mFiles.add((RemoteFile)remoteFileObj[i]);
            }

            if(mFiles.size() == 0) {
                mProgressBarContainer.setVisibility(View.INVISIBLE);
                mLoadingTextView.setText("Empty folder");
            }

            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 对相应的点击事件进行处理
     * 1、如果用户点击的是“远程打开”，发送远程打开的命令
     * 2、如果用户点击的是“传输到本地”，发送传输文件的命令
     */
    private class TransferFileTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            File mSDCard = Environment.getExternalStorageDirectory();
            mSDCard =  new File(mSDCard, "/PControlDroid");
            OutputStream out = null;
            FileOutputStream fos = null;
            DataInputStream dis = null;
            byte[] buffer = new byte[1024];
            int length = -1;

            Socket socket = null;

            if(!mSDCard.exists()) {
                mSDCard.mkdir();
            }

            try {
                socket = new Socket(ConnectionManager.get(getActivity()).getServerIP(), 2016);
                out = socket.getOutputStream();
                out.write(params[0].getBytes());
                long count = 0;
                if(params[0].startsWith("TRANS")) {
                    long fileSize = Long.parseLong(params[1]);
                    dis = new DataInputStream(socket.getInputStream());
                    mSDCard = new File(mSDCard, params[0].substring(params[0].lastIndexOf('/')));
                    fos = new FileOutputStream(mSDCard);
                    while((length = dis.read(buffer, 0, buffer.length)) > 0) {
                        count += length;
                        fos.write(buffer, 0, length);
                        fos.flush();
                        publishProgress((int) ((count / (float) fileSize) * 100));
                    }
                }

            } catch (Exception e) {
                Log.i(TAG, "文件传输错误: " + e);
            } finally {
                try {
                    if (out != null)
                        out.close();
                    if(fos != null)
                        fos.close();
                    if(dis != null)
                        dis.close();
                    if(socket != null)
                        socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(mProgressDialog != null)
                mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mProgressDialog != null) {
                mProgressDialog.cancel();
            }
        }
    }
}
