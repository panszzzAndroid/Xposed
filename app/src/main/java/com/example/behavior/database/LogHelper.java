package com.example.behavior.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.behavior.utils.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志查询工具
 */
public class LogHelper {
    private Context context;

    public LogHelper(Context context) {
        this.context = context;
    }

    /**
     * 查询记录
     *
     * @param pkg 应用包名
     * @return 记录列表
     */
    public List<BehaviorLog> query(String pkg) {
        List<BehaviorLog> logList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            // pkg 过滤, time 降序
            Cursor cursor = contentResolver.query(Uri.parse("content://com.example.behavior/"), null, "pkg=?", new String[]{pkg}, "time desc");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ContentValues values = SqliteHelper.cursorToContents(cursor);
                    BehaviorLog behaviorLog = new BehaviorLog(values);
                    logList.add(behaviorLog);
                }
                cursor.close();
            }
        }
        return logList;
    }
}
