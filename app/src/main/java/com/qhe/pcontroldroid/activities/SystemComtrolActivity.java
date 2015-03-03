package com.qhe.pcontroldroid.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

import com.qhe.pcontroldroid.fragments.SystemControlFragment;

/**
 * Created by sunshine on 15-3-2.
 */
public class SystemComtrolActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new SystemControlFragment();
    }

}
