package com.qhe.pcontroldroid.activities;

import android.support.v4.app.Fragment;

import com.qhe.pcontroldroid.fragments.SystemControlFragment;

/**
 * Created by sunshine on 15-3-2.
 */
public class SystemComtrolActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SystemControlFragment();
    }
}
