package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.test.util.ContactUtils;
import com.example.test.util.MyContacts;
import com.example.test.util.SMSUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void write(View view) {
        Files.writeFile(new File("/sdcard/a.txt"), "123");
    }

    public void sms(View view) {
        boolean result = addPermissionByPermissionList(this, new String[]{Manifest.permission.READ_SMS}, 0);
        if (result) {
            String smsList = SMSUtil.read(getApplicationContext());
            Log.e("@@", smsList.substring(1, 50));
        }
    }

    public void gps(View view) {
        File dir = new File("/sdcard/aaa1");
        dir.mkdir();

        try {
            new File(dir,"test.txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void contact(View view) {
        String s = ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString();
        boolean result = addPermissionByPermissionList(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        if (result) {
            ArrayList<MyContacts> allContacts = ContactUtils.getAllContacts(getApplicationContext());
            Toast.makeText(this, String.valueOf(allContacts.size()), Toast.LENGTH_SHORT).show();
        }
    }

    public void photo(View view) {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).list();
    }

    /**
     * ????????????
     */
    public boolean addPermissionByPermissionList(Activity activity, String[] permissions, int request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android 6.0????????????????????????????????????????????????
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {  //???????????????App????????????
                Toast.makeText(this, "?????????", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                //??????????????????
                String[] permissionsNew = mPermissionList.toArray(new String[mPermissionList.size()]);//???List????????????
                ActivityCompat.requestPermissions(activity, permissionsNew, request); //??????????????????onRequestPermissionsResult????????????
                return false;
            }
        }
        return false;
    }

}
