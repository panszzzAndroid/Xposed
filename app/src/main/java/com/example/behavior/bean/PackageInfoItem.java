package com.example.behavior.bean;

import android.graphics.drawable.Drawable;

import com.example.behavior.database.BehaviorLog;

import java.util.List;

public class PackageInfoItem {
    private String pkg;
    private String label;
    private Drawable icon;
    private List<BehaviorLog> logList;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public List<BehaviorLog> getLogList() {
        return logList;
    }

    public void setLogList(List<BehaviorLog> logList) {
        this.logList = logList;
    }
}
