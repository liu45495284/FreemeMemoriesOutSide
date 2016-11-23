package com.freeme.Address;

import android.os.AsyncTask;

import com.freeme.memories.utils.LogUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ClassName: AnalyzeAddress
 * Description:
 * Author: connorlin
 * Date: Created on 2016-6-2.
 */
public class AnalyzeAddressTask extends AsyncTask<String, String, AddressItem[]>  {

    private AnalyzeCallback mAnalyzeCallback;

    public AnalyzeAddressTask(AnalyzeCallback callback) {
        mAnalyzeCallback = callback;
    }

    @Override
    protected AddressItem[] doInBackground(String... params) {
        String result = null;
        try {
            LogUtil.i("params[0] = " + params[0]);
            result = AnalyzeUrl.getJsonStringFromUrl(params[0]);
            //LogUtil.i("result = " + result);
        } catch (Exception e) {
            e.printStackTrace();
           LogUtil.i("doInBackground exception:" + e.toString());
            mAnalyzeCallback.onFailure();
        }

        return getDataFromResult(result);
    }

    @Override
    protected void onPostExecute(AddressItem[] entity) {
        super.onPostExecute(entity);

        if(entity != null && entity.length > 0) {
            mAnalyzeCallback.onSuccess(entity);
        } else {
            mAnalyzeCallback.onFailure();
        }
    }

    private AddressItem[] getDataFromResult(String result) {
        AddressItem[] mAddressItems = null;
        JSONObject jsonResult;
        try {
            Gson gson = new Gson();
            jsonResult = new JSONObject(result);
            JSONArray mArray = jsonResult.getJSONArray("results");

            JSONObject ac = mArray.getJSONObject(0);
            JSONArray lastArray = ac.getJSONArray("address_components");
            mAddressItems = gson.fromJson(lastArray.toString(), AddressItem[].class);
        } catch (Exception e) {
            e.printStackTrace();
            com.freeme.memories.utils.LogUtil.i("getDataFromResult ex " + e);
        }

        return mAddressItems;
    }
}
