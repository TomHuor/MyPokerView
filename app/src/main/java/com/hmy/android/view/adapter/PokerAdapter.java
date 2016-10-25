package com.hmy.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hmy.android.view.R;
import com.hmy.android.view.bean.CardMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shall on 2015-06-23.
 */
public class PokerAdapter extends BaseAdapter {
    private Context mContext;
    private List<CardMode> mData = new ArrayList<>();

    public PokerAdapter(Context mContext, List<CardMode> data) {
        this.mContext = mContext;
        this.mData = data;
    }

    public void resetData(List<CardMode> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        initView(holder, position);

        return convertView;
    }

    private void initView(ViewHolder holder, int position) {
        try {
            Glide.with(mContext)
                    .load(mData.get(position).getImages())
                    .into(holder.helloText);
            holder.cardName.setText(mData.get(position).getName());
            holder.cardYear.setText(String.valueOf(mData.get(position).getYear()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ViewHolder {
        protected ImageView helloText;
        protected TextView cardName;
        protected TextView cardYear;
        protected TextView cardImageNum;
        protected View itemSwipeLeftIndicator;
        protected View itemSwipeRightIndicator;

        ViewHolder(View rootView) {
            initView(rootView);
        }

        private void initView(View rootView) {
            helloText = (ImageView) rootView.findViewById(R.id.helloText);
            cardName = (TextView) rootView.findViewById(R.id.card_name);
            cardYear = (TextView) rootView.findViewById(R.id.card_year);
            cardImageNum = (TextView) rootView.findViewById(R.id.card_image_num);
            itemSwipeLeftIndicator = rootView.findViewById(R.id.item_swipe_left_indicator);
            itemSwipeRightIndicator = rootView.findViewById(R.id.item_swipe_right_indicator);
        }
    }
}
