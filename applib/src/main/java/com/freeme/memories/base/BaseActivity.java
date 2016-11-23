package com.freeme.memories.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.freeme.memories.actionbar.IActionBarHandle;

/**
 * ClassName: BaseActivity
 * Description:
 * Author: connorlin
 * Date: Created on 2016/10/2.
 */
public class BaseActivity extends AppCompatActivity implements IHandle, IActionBarHandle {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpActionBar();
    }

    public void setUpActionBar() {

    }

    @Override
    public void onHandle(int type, Object object) {

    }

    @Override
    public void onBack() {

    }

    @Override
    public void rightAction() {

    }
}
