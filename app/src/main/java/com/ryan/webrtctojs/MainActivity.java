package com.ryan.webrtctojs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 输入信令服务器地址和房间号
 */
public class MainActivity extends Activity {

    private EditText mEditService;
    private EditText mEditRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditService = findViewById(R.id.EditService);
        mEditRoom = findViewById(R.id.EditRoom);

        String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "申请权限", 0, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 点击加入房间
     * @param view
     */
    public void onJoinClick(View view) {
        String service = mEditService.getText().toString(); // 注意这里需要https
        String room = mEditRoom.getText().toString();

        if (TextUtils.isEmpty(service) || TextUtils.isEmpty(room)) {
            Toast.makeText(this, "请输入信令服务器地址或者房间名", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("ServerAdd", service);
        intent.putExtra("RoomName", room);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}
