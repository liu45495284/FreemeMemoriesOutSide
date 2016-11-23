package com.freeme.memories.actionbar.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.freeme.memories.BR;
import com.freeme.memories.actionbar.presenter.ActionBarPresenter;

/**
 * ClassName: ActionBarConfig
 * Description:
 * Author: connorlin
 * Date: Created on 202016/10/16.
 */
public class ActionBarConfig extends BaseObservable {

    // 左箭头
    private boolean showHomeAsUp = false;
    // 是否显示右侧菜单
    private boolean showRightMenu = false;
    // 右边菜单是否文字
    private boolean isRightText = true;
    // 右侧菜单图片资源ID
    private int rightImageId;

    private String title;
    // 右侧菜单文字
    private String rightMenuText;

    private ActionBarPresenter mActionBarPresenter;

    @Bindable
    public ActionBarPresenter getActionBarPresenter() {
        return mActionBarPresenter;
    }

    public void setActionBarPresenter(ActionBarPresenter actionBarPresenter) {
        mActionBarPresenter = actionBarPresenter;
        notifyPropertyChanged(BR.actionBarPresenter);
    }

    @Bindable
    public boolean isShowRightMenu() {
        return showRightMenu;
    }

    public void setShowRightMenu(boolean showRightMenu) {
        this.showRightMenu = showRightMenu;
        notifyPropertyChanged(BR.showRightMenu);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public int getRightImageId() {
        return rightImageId;
    }

    public void setRightImageId(int rightImageId) {
        this.rightImageId = rightImageId;
        notifyPropertyChanged(BR.rightImageId);
    }

    @Bindable
    public boolean isShowHomeAsUp() {
        return showHomeAsUp;
    }

    public void setShowHomeAsUp(boolean showHomeAsUp) {
        this.showHomeAsUp = showHomeAsUp;
        notifyPropertyChanged(BR.showHomeAsUp);
    }

    @Bindable
    public boolean isRightText() {
        return isRightText;
    }

    public void setRightText(boolean rightText) {
        isRightText = rightText;
        notifyPropertyChanged(BR.rightText);
    }

    @Bindable
    public String getRightMenuText() {
        return rightMenuText;
    }

    public void setRightMenuText(String rightMenuText) {
        this.rightMenuText = rightMenuText;
        notifyPropertyChanged(BR.rightMenuText);
    }
}
