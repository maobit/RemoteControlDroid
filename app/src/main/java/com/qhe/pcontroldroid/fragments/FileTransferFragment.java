package com.qhe.pcontroldroid.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
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

    private FragmentManager mFragmentManager;
    private RemoteFileAdapter adapter;


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

        getActivity().setTitle("文件传输");

        mRequestDir = getArguments().getString(EXTRA_REQUEST_DIR);
//        Toast.makeText(getActivity(), "Dir: " + mRequestDir, Toast.LENGTH_SHORT).show();


        mFiles = new ArrayList<RemoteFile>();
        // get remote file list
        new FetchRemoteFileListTask().execute(mRequestDir);

        adapter = new RemoteFileAdapter(mFiles);
        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_transfer, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mFragmentManager = getActivity().getSupportFragmentManager();
        RemoteFile file = ((RemoteFileAdapter)getListAdapter()).getItem(position);
//        Toast.makeText(getActivity(), file.getAbsPath(), Toast.LENGTH_SHORT).show();
        FileTransferFragment fragment =  FileTransferFragment.newInstance(file.getAbsPath());
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(file.getAbsPath()).commit();
//        mFragmentManager.beginTransaction().hide(this).commit();
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
                    RemoteFile remoteFile;
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
//            for(int i=0; i<aVoid.size(); i++) {
//                mFiles.add(aVoid.get(i));
//            }

            Object[] remoteFileObj = aVoid.toArray();
            Arrays.sort(remoteFileObj);

            for(int i=0; i<remoteFileObj.length; i++) {
                mFiles.add((RemoteFile)remoteFileObj[i]);
            }

            adapter.notifyDataSetChanged();
        }
    }
}
