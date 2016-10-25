package com.hmy.android.view.util;

import android.content.Context;
import android.view.View;

/**
 * 尺寸管理
 *
 * @author huomengyuan 3d4edcc1
 * @time 2016/1/18
 **/
public final class DimenUtil {
    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;

    private static DimenUtil mDimen;

    private Context mContext;

    private DimenUtil(Context context) {
        mContext = context;
    }

    public static DimenUtil getInstance(Context context) {
        if (mDimen == null) {
            mDimen = new DimenUtil(context);
        }
        return mDimen;
    }

    /**
     * 获取屏幕密度
     *
     * @return
     */
    public float getDensity() {
        if (density <= 0F) {
            density = mContext.getResources().getDisplayMetrics().density;
        }
        return density;
    }

    /**
     * px to dp
     *
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        return (int) (dpValue * getDensity() + 0.5F);
    }

    /**
     * dp to px
     *
     * @return
     */
    public int px2dip(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5F);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public int getScreenWidth() {
        if (widthPixels <= 0) {
            widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public int getScreenHeight() {
        if (heightPixels <= 0) {
            heightPixels = mContext.getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }

    /**
     * 获取控件的宽度
     *
     * @param view
     * @return
     */
    public int getViewMeasuredWidth(View view) {
        calcViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 获取控件的高度
     *
     * @param view
     * @return
     */
    public int getViewMeasuredHeight(View view) {
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 测量控件的尺寸
     *
     * @param view
     */
    public static void calcViewMeasure(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }

    public int getXInWindow(View view) {
        int[] location = getLocation(view);
        return location[0];
    }

    public int getYInWindow(View view) {
        int[] location = getLocation(view);
        return location[1];
    }

    private int[] getLocation(View view) {
        int[] location = new int[2];
//        view.getLocationOnScreen(location);
        view.getLocationInWindow(location);
        return location;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
