package com.qhe.pcontroldroid.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.qhe.pcontroldroid.R;

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

        }
    }


}
