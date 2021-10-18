package com.example.behavior.task;

import android.net.Uri;

import com.example.behavior.MainHook;
import com.example.behavior.database.BehaviorLog;

import java.util.ArrayList;
import java.util.List;

public class ContactTask extends Task {
    private String pkg;
    private List<Uri> uriList = new ArrayList<>();
    private Thread thread;

    public ContactTask(final MainHook mainHook) {
        super(mainHook);
    }

    public void add(Uri uri, String pkg) {
        this.pkg = pkg;
        this.uriList.add(uri);
        if (thread == null || !thread.isAlive()) {
            thread = null;
            thread = new Thread(new DelayRunnable(mainHook));
            thread.start();
        }
    }

    private class DelayRunnable implements Runnable {
        private MainHook mainHook;

        public DelayRunnable(MainHook mainHook) {
            this.mainHook = mainHook;
        }

        @Override
        public void run() {
            int lastSize = uriList.size();
            long startTime = System.currentTimeMillis();
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (lastSize == uriList.size()) {
                    BehaviorLog log = new BehaviorLog();
                    log.setTime(startTime);
                    log.setPkg(pkg);
                    log.setType(BehaviorLog.Type.CONTACTS);

                    saveLog(log);

                    mainHook.log(pkg + " 读取联系人信息 contacts");
                    uriList.clear();
                    break;
                } else {
                    lastSize = uriList.size();
                }
            }
        }
    }
}
