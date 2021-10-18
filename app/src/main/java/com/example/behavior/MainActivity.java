package com.example.behavior;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.behavior.adapter.PackageAdapter;
import com.example.behavior.bean.PackageInfoItem;
import com.example.behavior.database.LogHelper;
import com.example.behavior.shell.Shell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查文件读写权限
        //this.checkPermission();
        //加载应用列表
        this.loadPackageList();

        
    }

    private void checkPermission() {
        int permissionCode = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCode != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
    private void loadPackageList() {
        Toast.makeText(this, "加载列表中", Toast.LENGTH_SHORT).show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    final List<PackageInfoItem> packageInfoList = new ArrayList<>();
                    PackageManager packageManager = getPackageManager();
                    List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
                    LogHelper logHelper = new LogHelper(getApplicationContext());
                    for (ApplicationInfo installedApplication : installedApplications) {
                        // 跳过系统应用
                        if ((installedApplication.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                            continue;
                        }
                        PackageInfoItem basePackageInfo = new PackageInfoItem();
                        basePackageInfo.setPkg(installedApplication.packageName);
                        basePackageInfo.setLabel(installedApplication.loadLabel(packageManager).toString());
                        basePackageInfo.setIcon(installedApplication.loadIcon(packageManager));
                        basePackageInfo.setLogList(logHelper.query(installedApplication.packageName));
                        packageInfoList.add(basePackageInfo);
                    }

                    // 次数排序
                    Collections.sort(packageInfoList, new Comparator<PackageInfoItem>() {
                        @Override
                        public int compare(PackageInfoItem o1, PackageInfoItem o2) {
                            int a1 = o1.getLogList().size();
                            int a2 = o2.getLogList().size();
                            return -1 * Integer.compare(a1, a2);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PackageAdapter adapter = new PackageAdapter(getApplicationContext(), packageInfoList);
                            ListView list = findViewById(R.id.list);
                            list.setAdapter(adapter);
                            // 点击,查看记录
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    PackageAdapter adapter1 = (PackageAdapter) parent.getAdapter();
                                    PackageInfoItem packageInfo = adapter1.getItem(position);
                                    Intent intent = new Intent(getApplicationContext(), BehaviorActivity.class);
                                    intent.putExtra("pkg", packageInfo.getPkg());
                                    intent.putExtra("name", packageInfo.getLabel());
                                    startActivity(intent);
                                }
                            });

                            // 长按,重启应用
                            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    PackageAdapter adapter1 = (PackageAdapter) parent.getAdapter();
                                    PackageInfoItem packageInfo = adapter1.getItem(position);
                                    restartPackage(getApplicationContext(), packageInfo.getPkg());
                                    Toast.makeText(MainActivity.this, "重启 - " + packageInfo.getLabel(), Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 重启程序
     */
    public void restartPackage(final Context context, final String packageName) {
        if (context == null) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                Shell.SU.run("am force-stop " + packageName);
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }.start();
    }
}