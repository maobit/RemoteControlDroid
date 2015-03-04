package com.qhe.pcontroldroid.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.qhe.pcontroldroid.R;
import com.qhe.pcontroldroid.models.ConnectionManager;
import com.qhe.pcontroldroid.utils.CommandId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ShareScreenActivity extends ActionBarActivity {
    private ImageView mImageView;
    private Bitmap mBitmap;

    private static final String TAG = "ShareScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_screen);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        mImageView = (ImageView) findViewById(R.id.image_screen);

        new FetchScreenShotsTask().execute();
//        mImageView.setImageBitmap(mBitmap);
    }

    private class FetchScreenShotsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            OutputStream out;
            InputStream in;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectInputStream inputStream;
            try {
                Socket socket = new Socket(ConnectionManager.get(ShareScreenActivity.this).getServerIP(), 2016);
                out = socket.getOutputStream();
                out.write((CommandId.SHARE_SCREEN + "").getBytes());

                in = socket.getInputStream();

                byte[] buffer = new byte[1024];
                int length = -1;
                while((length = in.read(buffer)) > 0) {
                    baos.write(buffer, 0, length);
                    baos.flush();
                }
                baos.flush();
//                DataInputStream reader = new DataInputStream(in);
//                int msgSymbol = reader.read();
//                if(msgSymbol == -1) {
//                    int msgSize = reader.readInt();
//                    byte[] buffer = new byte[1024];
//                    int length = -1;
//                    while((length = reader.read(buffer)) > 0) {
//                        baos.write(buffer, 0, length);
//                        baos.flush();
//                    }
//                    baos.flush();
//                }

                byte[] bytes = baos.toByteArray();

                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                File pic = new File(Environment.getExternalStorageDirectory() + "/PControlDroid/test.jpg");
                FileOutputStream fos = new FileOutputStream(pic);
                fos.write(bytes);
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mImageView.setImageBitmap(mBitmap);
        }
    }

}
