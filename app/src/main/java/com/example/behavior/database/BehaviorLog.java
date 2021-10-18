package com.example.behavior.database;


import android.content.ContentValues;

/**
 * 日志记录表
 */
public class BehaviorLog {

    //请求时间
    private long time;

    //应用包名
    private String pkg;

    //日志类型 0 相册 1 通讯录 2 位置 3 短信
    private int type;

    public BehaviorLog() {
    }

    public BehaviorLog(ContentValues values) {
        if (values.containsKey("time")) {
            setTime(values.getAsLong("time"));
        }

        if (values.containsKey("pkg")) {
            setPkg(values.getAsString("pkg"));
        }

        if (values.containsKey("type")) {
            setType(values.getAsInteger("type"));
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Type {
        public static final int DCIM = 0;
        public static final int CONTACTS = 1;
        public static final int LBS = 2;
        public static final int SMS = 3;
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put("time", getTime());
        values.put("pkg", getPkg());
        values.put("type", getType());
        return values;
    }
}