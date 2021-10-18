package com.example.test.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SMSUtil {
    public static String read(Context context) {
        final String SMS_URI_INBOX = "content://sms/inbox";
        Uri uri = Uri.parse(SMS_URI_INBOX);
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type",};
        Cursor cur = context.getContentResolver().query(uri, projection, null, null, "date desc");
        StringBuilder smsBuilder = new StringBuilder();

        if (cur.moveToFirst()) {
            smsBuilder.append(SqliteHelper.dumpCursor(cur));
            if (!cur.isClosed()) {
                cur.close();
                cur = null;
            }
        } else {
            smsBuilder.append("no result!");
        }
        smsBuilder.append("getSmsInPhone has executed!");
        return smsBuilder.toString();
    }
}
