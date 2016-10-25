package com.hmy.android.view.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.hmy.android.view.R;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinosaurs might appear!
 */

public class PokerView extends AbsPokerView {
    private float mChildScaleOffset = 0.07f;
    private int mChildTopMargin = 0;
    private int mMaxVisiableCount = 3;
    private float mRotationDegrees = 15.f;
    private int mLastChildInStack = 0;

    private float mMoveScale = 0f;

    private Adapter mAdapter;
    private OnFlingListener mFlingListener;
    private AdapterDataSetObserver mDataSetObserver;
    private boolean mInLayout = false;
    private View mActivePoker = null;
    private OnItemClickListener mOnItemClickListener;
    private OnPokerTouchListener onPokerTouchListener;
    private PointF mLastTouchPoint;

    public PokerView(Context context) {
        super(context);
    }

    public PokerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PokerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PokerView, defStyle, 0);
        mMaxVisiableCount = a.getInt(R.styleable.PokerView_max_visible, mMaxVisiableCount);
        mRotationDegrees = a.getFloat(R.styleable.PokerView_rotation_degrees, mRotationDegrees);
        a.recycle();
    }


    /**
     * 设置 item之间的缩放比例差0.05:1~0.95~0.9
     *
     * @param scaleMargin
     */
    public void setChildScaleMargin(float scaleMargin) {
        this.mChildScaleOffset = scaleMargin;
    }


    /**
     * 设置 item之间的top差
     * 缩放之后item view比之上一层的item view 小 ，需要下移显示出层次
     *
     * @param topMargin
     */
    public void setChildTopMargin(int topMargin) {
        this.mChildTopMargin = topMargin;
    }

    public void setMaxVisiableCount(int mMaxVisiableCount) {
        this.mMaxVisiableCount = mMaxVisiableCount;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;

        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public void setOnFlingListener(OnFlingListener onFlingListener) {
        this.mFlingListener = onFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void onLeftExit() {
        getTopCardListener().selectLeft();
    }

    public void onRightExit() {
        getTopCardListener().selectRight();
    }

    @Override
    public View getSelectedView() {
        return mActivePoker;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }

        mInLayout = true;
        final int adapterCount = mAdapter.getCount();

        if (adapterCount == 0) {
            removeAllViewsInLayout();
        } else {
            View topCard = getChildAt(mLastChildInStack);

            if (mActivePoker != null && topCard != null && topCard == mActivePoker) {
                if (this.onPokerTouchListener.isTouching()) {
                    PointF lastPoint = this.onPokerTouchListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        this.mLastTouchPoint = lastPoint;
                        removeViewsInLayout(0, mLastChildInStack);
                        addViewsInLayout(1, adapterCount);
                    }
                }
            } else {
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                addViewsInLayout(0, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;

        if (adapterCount <= mMaxVisiableCount) mFlingListener.onAdapterEmpty(adapterCount);
    }


    private void addViewsInLayout(int startingIndex, int adapterCount) {
        while (startingIndex <= Math.min(adapterCount, mMaxVisiableCount)) {
            View newUnderChild = mAdapter.getView(startingIndex, null, this);

            if (newUnderChild.getVisibility() != GONE) {
                layoutChild(startingIndex, newUnderChild);
                mLastChildInStack = startingIndex;
            }
            startingIndex++;
        }
    }

    /**
     * 绘制子View
     *
     * @param index
     * @param child
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void layoutChild(int index, View child) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        addViewInLayout(child, 0, lp, true);

        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }
        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - w) / 2 + lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                int l = 0;
                childLeft = getPaddingLeft() + lp.leftMargin + l;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                int top = 0;
                childTop = getPaddingTop() + lp.topMargin + top;
                break;
        }

        child.layout(childLeft, childTop, childLeft + w, childTop + h);
        scaleChildView(child, index);
    }

    /**
     * 改变view 大小
     *
     * @param child
     * @param index
     */
    private void scaleChildView(View child, int index) {
        int curScale = index;

        if (index == mMaxVisiableCount && mMoveScale < 0.5f) {
            curScale = mMaxVisiableCount - 1;
            child.setVisibility(INVISIBLE);
        }else{
            child.setVisibility(VISIBLE);
        }

        child.offsetTopAndBottom((int) ((child.getHeight() / 2 * mChildScaleOffset + mChildTopMargin) * (curScale - mMoveScale)));
        child.setScaleX(1 - mChildScaleOffset * (curScale - mMoveScale));
        child.setScaleY(1 - mChildScaleOffset * (curScale - mMoveScale));
    }

    /**
     * Set the top view and add the fling listener
     */
    private void setTopView() {
        if (getChildCount() > 0) {

            mActivePoker = getChildAt(mLastChildInStack);
            if (mActivePoker != null) {

                onPokerTouchListener = new OnPokerTouchListener(mActivePoker, mAdapter.getItem(0),
                        mRotationDegrees, new OnPokerTouchListener.FlingListener() {

                    @Override
                    public void onCardExited() {
                        mActivePoker = null;
                        mMoveScale = 0f;
                        mFlingListener.onFirstPokerExit();
                    }

                    @Override
                    public void leftExit(Object dataObject) {
                        mMoveScale = 0f;
                        mFlingListener.onPokerLeftExit(dataObject);
                    }

                    @Override
                    public void rightExit(Object dataObject) {
                        mMoveScale = 0f;
                        mFlingListener.onPokerRightExit(dataObject);
                    }

                    @Override
                    public void onClick(Object dataObject) {
                        mMoveScale = 0f;
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClicked(0, dataObject);

                    }

                    @Override
                    public void onScroll(float scrollProgressPercent) {
                        mFlingListener.onScroll(scrollProgressPercent);
                    }

                    @Override
                    public void onMoveXY(float moveX, float moveY) {
                        float mX = (int) Math.abs(moveX);
                        float mY = (int) Math.abs(moveY);
                        if (mX > 50 || mY > 50) {
                            float m = Math.max(mX, mY);
                            mMoveScale = (m - 50f) / (mActivePoker.getWidth() * 5 / 14);
                            if (mMoveScale > 1f) {
                                mMoveScale = 1f;
                            }
                        } else {
                            mMoveScale = 0f;
                        }
                        requestLayout();
                    }
                });

                mActivePoker.setOnTouchListener(onPokerTouchListener);
            }
        }
    }

    private OnPokerTouchListener getTopCardListener() throws NullPointerException {
        if (onPokerTouchListener == null) {
            throw new NullPointerException();
        }
        return onPokerTouchListener;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            requestLayout();
        }

    }

    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public interface OnFlingListener {
        void onFirstPokerExit();

        void onPokerLeftExit(Object dataObject);

        void onPokerRightExit(Object dataObject);

        void onScroll(float scrollProgressPercent);

        void onAdapterEmpty(int leftCount);
    }

}
