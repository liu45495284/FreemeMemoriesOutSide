package com.freeme.memories.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * ClassName: BaseHandler
 * Description:
 * Author: connorlin
 * Date: Created on 2016-8-30.
 */
public class BaseHandler extends Handler {

    public BaseHandler(Context context) {
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }

    @Override
    public void dispatchMessage(Message msg) {
        super.dispatchMessage(msg);
    }
}
