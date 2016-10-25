package com.hmy.android.view.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 提示
 *
 * @author huomengyuan 3d4edcc1
 */
public class ToastUtil {
    private static ToastUtil mInstance;
    private Context mContext;

    public ToastUtil(Context context) {
        mContext = context;
    }

    public static ToastUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ToastUtil(context);
        }
        return mInstance;
    }

    public void showShort(@StringRes int message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showShort(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showLong(@StringRes int message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public void showLong(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
