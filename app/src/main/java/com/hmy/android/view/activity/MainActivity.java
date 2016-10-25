package com.hmy.android.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.hmy.android.view.R;
import com.hmy.android.view.adapter.PokerAdapter;
import com.hmy.android.view.bean.CardMode;
import com.hmy.android.view.util.ToastUtil;
import com.hmy.android.view.view.PokerView;

import java.util.List;

public class MainActivity extends Activity implements MainView {
    private PokerAdapter mAdapter;
    private PokerView vPokerView;

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(this);

        setContentView(R.layout.activity_main);
        vPokerView = (PokerView) findViewById(R.id.main_poker);

        findViewById(R.id.main_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vPokerView.onLeftExit();
            }
        });

        findViewById(R.id.main_right).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vPokerView.onRightExit();
            }
        });

        vPokerView.setOnFlingListener(new PokerView.OnFlingListener() {

            @Override
            public void onFirstPokerExit() {
                mPresenter.onFirstPokerExit();
            }

            @Override
            public void onPokerLeftExit(Object dataObject) {
                ToastUtil.getInstance(MainActivity.this).showShort("不喜欢");
            }

            @Override
            public void onPokerRightExit(Object dataObject) {
                ToastUtil.getInstance(MainActivity.this).showShort("喜欢");
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                try {
                    View view = vPokerView.getSelectedView();
                    view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                    view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdapterEmpty(int leftCount) {
                ToastUtil.getInstance(MainActivity.this).showShort("剩余数量：" + leftCount);
            }
        });

        vPokerView.setOnItemClickListener(new PokerView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                ToastUtil.getInstance(MainActivity.this).showShort("点击图片");
            }
        });


        mPresenter.init();
    }


    @Override
    public void setAdapter(List<CardMode> data) {
        if (mAdapter == null) {
            mAdapter = new PokerAdapter(this, data);
            vPokerView.setAdapter(mAdapter);
        } else {
            mAdapter.resetData(data);
        }
    }
}
