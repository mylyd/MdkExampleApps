package com.example.mdkfastprintingapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mdkfastprintingapp.R;
import com.example.mdkfastprintingapp.bean.RecylerViewGetData;

import java.util.List;
/**
 * 类说明：显示模板RecyclerView 适配器
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/06 15:10
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<RecylerViewGetData> mlist;

    public RecyclerViewAdapter(List<RecylerViewGetData> mlist) {
        this.mlist = mlist;
    }

    public interface SwitchTheTemplateOclick{
        void onShowWord(int position);
    }

    private SwitchTheTemplateOclick switchTheTemplateOclick;

    public void getSwitchTheTemplate(SwitchTheTemplateOclick switchs){
        this.switchTheTemplateOclick = switchs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {
        RecylerViewGetData viewGetData = mlist.get(position);
        holder.imageViews.setImageResource(viewGetData.getImg_photo());
        holder.imageViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTheTemplateOclick.onShowWord(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViews;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViews = itemView.findViewById(R.id.item_img);
        }
    }
}
