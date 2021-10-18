package com.example.behavior.task;

import com.example.behavior.MainHook;
import com.example.behavior.database.BehaviorLog;

import java.util.ArrayList;
import java.util.List;

public class LbsTask extends Task{
    private String pkg;
    private List<Object> requestList = new ArrayList<>();
    private Thread thread;

    public LbsTask(final MainHook mainHook) {
        super(mainHook);
    }

    public void add(Object request, String pkg) {
        this.pkg = pkg;
        this.requestList.add(request);
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
            int lastSize = requestList.size();
            long startTime = System.currentTimeMillis();
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (lastSize == requestList.size()) {
                    BehaviorLog log = new BehaviorLog();
                    log.setTime(startTime);
                    log.setPkg(pkg);
                    log.setType(BehaviorLog.Type.LBS);

                    saveLog(log);

                    mainHook.log(pkg + " 读取位置信息 " + requestList.size());
                    requestList.clear();
                    break;
                } else {
                    lastSize = requestList.size();
                }
            }
        }
    }
}
