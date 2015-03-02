package com.qhe.pcontroldroid.activities;

import android.support.v4.app.Fragment;

import com.qhe.pcontroldroid.fragments.MainFragment;


public class MainActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

}
