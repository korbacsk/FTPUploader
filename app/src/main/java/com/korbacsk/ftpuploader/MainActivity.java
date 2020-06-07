package com.korbacsk.ftpuploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.korbacsk.ftpuploader.common.UICommon;
import com.korbacsk.ftpuploader.helper.Debug;
import com.korbacsk.ftpuploader.ui.main.MainFragment;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Debug.LogMessage("MainActivity - onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Debug.LogMessage("MainActivity - onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Debug.LogMessage("MainActivity - onPermissionsDenied");
        UICommon.showAlertDialog(
                this,
                getString(R.string.dialog__need_permission_title),
                getString(R.string.dialog__need_permission_message),
                getString(R.string.dialog__need_permission_button_ok));
    }
}
