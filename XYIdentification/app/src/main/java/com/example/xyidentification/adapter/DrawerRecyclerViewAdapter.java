package com.example.xyidentification.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xyidentification.R;
import com.example.xyidentification.utils.ListItem;

import java.util.List;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */
public class DrawerRecyclerViewAdapter extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.ViewHolder> {
    private List<ListItem> mlist;

    public DrawerRecyclerViewAdapter(List<ListItem> mlist) {
        this.mlist = mlist;
    }

    private OnItemListener onitem;
    public interface OnItemListener{
        void onItemListener(int position);
    }

    public void OnItemListener(OnItemListener onItemListener){
        this.onitem = onItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_drawerlayout,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        ListItem item = mlist.get(position);
        viewHolder.img.setImageResource(item.getImage());
        viewHolder.tv.setText(item.getItemString());
        viewHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onitem.onItemListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView tv;
        private final RelativeLayout lay_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.recycler_img);
            tv = itemView.findViewById(R.id.recycler_tv);
            lay_item = itemView.findViewById(R.id.lay_item);
        }
    }
}
