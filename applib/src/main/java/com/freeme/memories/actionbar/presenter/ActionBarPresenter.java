package com.freeme.memories.actionbar.presenter;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.freeme.memories.actionbar.IActionBarHandle;


/**
 * ClassName: ActionBarPresenter
 * Description:
 * Author: connorlin
 * Date: Created on 202016/10/16.
 */
public class ActionBarPresenter {

    private IActionBarHandle mIActionBarHandle;

    public ActionBarPresenter(IActionBarHandle handle) {
        mIActionBarHandle = handle;
    }

    @BindingAdapter({"imageId"})
    public static void setImage(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    public void onBack(View view) {
        if (mIActionBarHandle != null) {
            mIActionBarHandle.onBack();
        }
    }

    public void onRightAction(View view) {
        if (mIActionBarHandle != null) {
            mIActionBarHandle.rightAction();
        }
    }
}
