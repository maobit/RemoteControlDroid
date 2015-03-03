package com.qhe.pcontroldroid.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.qhe.pcontroldroid.fragments.MainFragment;


public class MainActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

}
