package com.qhe.pcontroldroid.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.qhe.pcontroldroid.R;
import com.qhe.pcontroldroid.models.ConnectionManager;
import com.qhe.pcontroldroid.utils.CommandId;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ShareScreenActivity extends ActionBarActivity {
    private ImageView mImageView;
    private Bitmap mBitmap;

    private static final String TAG = "ShareScreenActivity";
    private FetchScreenShotsTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_screen);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        mImageView = (ImageView) findViewById(R.id.image_screen);

        task = new FetchScreenShotsTask();
        task.execute();

//        mImageView.setImageBitmap(mBitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.cancel(true);
    }

    private class FetchScreenShotsTask extends AsyncTask<Void, Bitmap, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            OutputStream out;
            InputStream in;
            ByteArrayOutputStream baos;
            ObjectInputStream inputStream;
            while(true) {
                try {
                    Socket socket = new Socket(ConnectionManager.get(ShareScreenActivity.this).getServerIP(), 2016);
                    out = socket.getOutputStream();
                    out.write((CommandId.SHARE_SCREEN + "").getBytes());

                    in = socket.getInputStream();

                    //                byte[] picLen = new byte[8];
                    //                in.read(picLen, 0, picLen.length);
                    //                int msgSize = Integer.parseInt(new String(picLen, 0, picLen.length));
                    //                Log.e(TAG, "Size: " + msgSize);
                    //                byte[] buffer = new byte[msgSize];
                    //                int length = 0;
                    //                while(length < msgSize) {
                    //                    int readSize = in.read(buffer, length, msgSize - length);
                    //                    if(readSize > 0) {
                    //                         length += readSize;
                    //                    } else {
                    //                        break;
                    //                    }
                    //                 }

                    baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length = -1;
                    while ((length = in.read(buffer)) > 0) {
                        baos.write(buffer, 0, length);
                        baos.flush();
                    }
                    baos.flush();
//                    try {
//                        Thread.sleep(2000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    //                DataInputStream reader = new DataInputStream(in);
                    //                int msgSymbol = reader.readInt();
                    //                if(msgSymbol == -1) {
                    //                    int msgSize = reader.readInt();
                    //                    byte[] buffer = new byte[msgSize];
                    //                    int length = 0;
                    //                    while(length < msgSize) {
                    //                        int readSize = reader.read(buffer, length, msgSize - length);
                    //                        if(readSize > 0) {
                    //                            length += readSize;
                    //                        } else {
                    //                            break;
                    //                        }
                    //                    }
                    //                    ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
                    //                    ZipInputStream zis = new ZipInputStream(bis);
                    //                    ZipEntry ze = zis.getNextEntry();
                    //                    byte[] temp = new byte[1024];
                    //
                    //                    length = 0;
                    //                    while((length = zis.read(temp, 0, 1024)) > 0) {
                    //                        baos.write(temp, 0, length);
                    //                    }
                    //                    baos.flush();
                    //                }

                    byte[] bytes = baos.toByteArray();
                    //                byte[] bytes = buffer;

                    mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    publishProgress(mBitmap);

//                    File pic = new File(Environment.getExternalStorageDirectory() + "/PControlDroid/test.jpg");
//                    FileOutputStream fos = new FileOutputStream(pic);
//                    fos.write(bytes);
//                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            mImageView.setImageBitmap(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mImageView.setImageBitmap(mBitmap);
        }
    }

}
