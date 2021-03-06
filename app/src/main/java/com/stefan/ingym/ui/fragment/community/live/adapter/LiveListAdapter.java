package com.stefan.ingym.ui.fragment.community.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stefan.ingym.R;

import java.util.ArrayList;


/**
 * CoffeeSecret
 * Created by Simo on 2017/3/11.
 */
public class LiveListAdapter extends RecyclerView.Adapter<LiveListAdapter.LiveListViewHolder> {

    private Context context;
    private ArrayList<String> liveList;
    private LayoutInflater mLayoutInfalter;
    private OnLiveClickListener onLiveClickListener;

    public LiveListAdapter(Context context, ArrayList<String> liveList, OnLiveClickListener onLiveClickListener) {
        this.context = context;
        this.liveList = liveList;
        this.onLiveClickListener = onLiveClickListener;

        mLayoutInfalter = LayoutInflater.from(context);
    }

    @Override
    public LiveListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LiveListViewHolder(mLayoutInfalter.inflate(R.layout.item_live_list, null));
    }

    @Override
    public void onBindViewHolder(final LiveListViewHolder holder, int position) {
        holder.liveName.setText(liveList.get(position));
        if (position == 0) {
            // 设置在线人数
            holder.live_watch_number.setText(9778 + "");
            // 设置背景图片
            holder.liveSnapshot.setImageResource(R.mipmap.live_cover_1);
        } else if (position == 1){
            holder.live_watch_number.setText(3661 + "");
            holder.liveSnapshot.setImageResource(R.mipmap.live_cover_2);
        } else if (position == 2) {
            holder.live_watch_number.setText(2257 + "");
            holder.liveSnapshot.setImageResource(R.mipmap.live_cover_3);
        }
        holder.liveSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLiveClickListener.onLiveClickListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return liveList.size();
    }

    class LiveListViewHolder extends RecyclerView.ViewHolder {
        private ImageView liveSnapshot;
        private TextView liveName;
        private TextView live_watch_number;

        public LiveListViewHolder(View itemView) {
            super(itemView);
            liveSnapshot = (ImageView) itemView.findViewById(R.id.live_snapshot);
            liveName = (TextView) itemView.findViewById(R.id.live_name);
            live_watch_number = (TextView) itemView.findViewById(R.id.live_watch_number);
        }
    }

    public interface OnLiveClickListener {
        void onLiveClickListener(int position);
    }

}
