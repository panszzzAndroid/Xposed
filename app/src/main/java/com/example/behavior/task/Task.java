package com.example.behavior.task;

import android.net.Uri;

import com.example.behavior.MainHook;
import com.example.behavior.database.BehaviorLog;

public class Task {
    protected MainHook mainHook;

    public Task(MainHook mainHook) {
        this.mainHook = mainHook;
    }

    /**
     * 保存日志记录
     */
    void saveLog(BehaviorLog log) {
        if (mainHook.getContext() == null) {
            return;
        }
        if (log == null) {
            return;
        }
        Uri uri = Uri.parse("content://com.example.behavior/");
        mainHook.getContext().getContentResolver().insert(uri, log.getValues());
    }
}
