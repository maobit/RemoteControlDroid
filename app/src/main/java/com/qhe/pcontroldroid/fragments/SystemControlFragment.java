package com.qhe.pcontroldroid.fragments;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.qhe.pcontroldroid.R;
import com.qhe.pcontroldroid.models.ConnectionManager;
import com.qhe.pcontroldroid.utils.CommandId;

import java.io.OutputStream;
import java.net.Socket;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SystemControlFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SystemControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemControlFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // 定义各种按钮
    private Button mLockButton;
    private Button mLogoutButton;
    private Button mStandbyButton;
    private Button mSleepButton;
    private Button mRebootButton;
    private Button mShutdownButton;


    private int mCommandId;
    private Socket mSocket;
    private ConnectionManager mConnectionManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SystemControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SystemControlFragment newInstance(String param1, String param2) {
        SystemControlFragment fragment = new SystemControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SystemControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_system_control, container, false);

        mConnectionManager = ConnectionManager.get(getActivity());

        // 实例化按键，设置按键监听器
        ButtonListener buttonListener = new ButtonListener();

        mLockButton = (Button)view.findViewById(R.id.button_lock);
        mLockButton.setOnClickListener(buttonListener);

        mLogoutButton = (Button)view.findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(buttonListener);

        mStandbyButton = (Button)view.findViewById(R.id.button_standby);
        mStandbyButton.setOnClickListener(buttonListener);

        mSleepButton = (Button)view.findViewById(R.id.button_sleep);
        mSleepButton.setOnClickListener(buttonListener);

        mShutdownButton = (Button)view.findViewById(R.id.button_shutdown);
        mShutdownButton.setOnClickListener(buttonListener);

        mRebootButton = (Button)view.findViewById(R.id.button_reboot);
        mRebootButton.setOnClickListener(buttonListener);

        return view;
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 获取命令
            switch (v.getId()) {
                case R.id.button_lock:
                    mCommandId = CommandId.SYSTEM_LOCK;
                    break;
                case R.id.button_logout:
                    mCommandId = CommandId.SYSTEM_LOGOUT;
                    break;
                case R.id.button_reboot:
                    mCommandId = CommandId.SYSTEM_REBOOT;
                    break;
                case R.id.button_sleep:
                    mCommandId = CommandId.SYSTEM_SLEEP;
                    break;
                case R.id.button_standby:
                    mCommandId = CommandId.SYSTEM_STANDBY;
                    break;
                case R.id.button_shutdown:
                    mCommandId = CommandId.SYSTEM_SHUTDWON;
                    break;
                default:
                    break;
            }

            // 执行命令
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(CommandId.COMMAND_ARRAY[mCommandId]);
            builder.setMessage("确定执行 " + CommandId.COMMAND_ARRAY[mCommandId]);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new SendCommandTask().execute(mCommandId + "");
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
        }
    }

    private class SendCommandTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            OutputStream os = null;
            try {
                mSocket = new Socket(mConnectionManager.getServerIP(), 2016);
                os = mSocket.getOutputStream();
                os.write(params[0].getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(os != null)
                        os.close();
                    if(mSocket != null)
                        mSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
