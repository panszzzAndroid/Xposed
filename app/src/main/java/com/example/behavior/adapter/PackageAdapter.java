package com.example.behavior.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.behavior.bean.PackageInfoItem;
import com.example.behavior.R;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends BaseAdapter {
    private List<PackageInfoItem> packageInfoList = new ArrayList<>();
    private Context context;

    public PackageAdapter(Context context, List<PackageInfoItem> packageInfoList) {
        this.context = context;
        this.packageInfoList = packageInfoList;
    }

    @Override
    public int getCount() {
        return packageInfoList.size();
    }

    @Override
    public PackageInfoItem getItem(int position) {
        return packageInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_main, null);
            holder.icon = convertView.findViewById(R.id.icon);
            holder.label = convertView.findViewById(R.id.name);
            holder.pkg = convertView.findViewById(R.id.pkg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PackageInfoItem item = getItem(position);

        holder.icon.setImageDrawable(item.getIcon());
        holder.label.setText(item.getLabel() + "(" + item.getLogList().size() + ")");
        holder.pkg.setText(item.getPkg());

        return convertView;
    }

    private static class ViewHolder {
        private ImageView icon;
        private TextView label;
        private TextView pkg;
    }
}
