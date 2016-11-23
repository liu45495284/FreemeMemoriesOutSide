package com.freeme.Address;

/**
 * ClassName: LoadTaskCallback
 * Description:
 * Author: connorlin
 * Date: Created on 2015-9-18.
 */
public abstract class AnalyzeCallback {

    public abstract void onSuccess(AddressItem[] data);

    public abstract void onFailure();
}
