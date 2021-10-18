package com.example.behavior;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.behavior.adapter.BehaviorAdapter;
import com.example.behavior.bean.BehaviorItem;
import com.example.behavior.database.BehaviorLog;
import com.example.behavior.database.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 单个应用记录列表
 */
public class BehaviorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_behavior);
        // 加载日志记录
        loadLogList();

        // 设置标题
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra("name"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 刷新日志记录
        this.loadLogList();
    }

    private void loadLogList() {
        String pkg = getIntent().getStringExtra("pkg");

        LogHelper logHelper = new LogHelper(getApplicationContext());
        List<BehaviorLog> logArrayList = logHelper.query(pkg);
        final List<BehaviorItem> packageInfoList = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        int countOfDCIM = 0, countOfContacts = 0, countOfLbs = 0, contOfSMS = 0;

        for (int i = 0; i < logArrayList.size(); i++) {
            BehaviorLog behaviorLog = logArrayList.get(i);
            BehaviorItem behaviorItem = new BehaviorItem();
            behaviorItem.setPkg(behaviorLog.getPkg());
            behaviorItem.setTime(behaviorLog.getTime());
            behaviorItem.setType(behaviorLog.getType());
            switch (behaviorItem.getType()) {
                case BehaviorLog.Type.DCIM:
                    countOfDCIM++;
                    break;
                case BehaviorLog.Type.CONTACTS:
                    countOfContacts++;
                    break;
                case BehaviorLog.Type.LBS:
                    countOfLbs++;
                    break;
                case BehaviorLog.Type.SMS:
                    contOfSMS++;
                    break;
            }
            try {
                behaviorItem.setIcon(packageManager.getApplicationIcon(behaviorLog.getPkg()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            packageInfoList.add(behaviorItem);
        }

        //设置副标题
        setSubTitle(countOfDCIM, countOfContacts, countOfLbs, contOfSMS);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BehaviorAdapter adapter = new BehaviorAdapter(getApplicationContext(), packageInfoList);
                ListView list = findViewById(R.id.list);
                list.setAdapter(adapter);
            }
        });
    }

    private void setSubTitle(final int countOfDCIM, final int countOfContacts, final int countOfLbs, final int contOfSMS) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String title = String.format(Locale.CHINA, "相册：%d, 通讯录：%d, 位置：%d, 短信：%d", countOfDCIM, countOfContacts, countOfLbs, contOfSMS);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setSubtitle(title);
                } else {
                    Toast.makeText(BehaviorActivity.this, title, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
