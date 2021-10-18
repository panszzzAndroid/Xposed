package com.example.behavior.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.behavior.R;
import com.example.behavior.bean.BehaviorItem;
import com.example.behavior.database.BehaviorLog;
import com.example.behavior.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class BehaviorAdapter extends BaseAdapter {
    private List<BehaviorItem> behaviorItems = new ArrayList<>();
    private Context context;

    public BehaviorAdapter(Context context, List<BehaviorItem> itemList) {
        this.context = context;
        this.behaviorItems = itemList;
    }

    @Override
    public int getCount() {
        return behaviorItems.size();
    }

    @Override
    public BehaviorItem getItem(int position) {
        return behaviorItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_log, null);
            holder.icon = convertView.findViewById(R.id.icon);
            holder.type = convertView.findViewById(R.id.type);
            holder.time = convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(getItem(position).getIcon());
        switch (getItem(position).getType()) {
            case BehaviorLog.Type.DCIM:
                holder.type.setText("读取相册");
                break;
            case BehaviorLog.Type.CONTACTS:
                holder.type.setText("读取通讯录");
                break;
            case BehaviorLog.Type.LBS:
                holder.type.setText("获取地理位置");
                break;
            case BehaviorLog.Type.SMS:
                holder.type.setText("读取短信");
                break;
        }
        holder.time.setText(Util.formatTime(getItem(position).getTime()));
        return convertView;
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView type;
        private TextView time;
    }
}
