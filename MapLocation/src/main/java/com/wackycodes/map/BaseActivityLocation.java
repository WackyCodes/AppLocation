package com.wackycodes.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wackycodes.map.listener.OnPermissionListener;

class BaseActivityLocation extends AppCompatActivity
        implements OnPermissionListener {
    private static final int REQUEST_PERMISSION_STORAGE = 100;
    private static final int REQUEST_PERMISSION_LOCATION = 101;

    public void showDebugLog(String logMsg ){
        Log.d( "LOG", " " + logMsg );
    }

    /**
     * Note : You can Extent this activity and Override  onPermissionGranted() there to get benefit directly
     */
    @Override
    public void onPermissionGranted(int permissionCode) {
        onPermissionGranted( permissionCode, true);
    }

    @Override
    public void onPermissionGranted(int permissionCode, boolean isGrant) {
        switch (permissionCode) {
            case OnPermissionListener.PERMISSION_CODE_LOCATION:
                // getLocation();
                // TODO : Call the method or code after permission granted!
                break;
        }
    }

    //---------- Check User Permission -------------------------------------------------------------
    public boolean isLocationPermissionGranted() {
        try {
            return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void requestForPermission(int permissionCode) {
        switch (permissionCode) {
            case PERMISSION_CODE_LOCATION:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_PERMISSION_LOCATION
                );
                break;
            default:
                break;
        }
    }

    @Override
    public void requestForcePermission(int permissionCode) {
        String permission = "";
        switch (permissionCode) {
            case PERMISSION_CODE_LOCATION:
                permission = Manifest.permission.ACCESS_COARSE_LOCATION;
                break;
            default:
                permission = null;
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permission != null) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                requestForPermission(permissionCode);
            } else {
                // We passed true.. since there is no matched!
                showDebugLog("Permission Passed! Code = " + permissionCode);
//                onPermissionGranted( permissionCode, true );
                requestPermissionSetting();
            }
        } else {
            // We passed true.. since there is no matched!
            showDebugLog("Permission Passed! App is <= M :: Permission Code = " + permissionCode);
            onPermissionGranted(permissionCode, true);
        }
    }

    private void requestPermissionSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        this.startActivity(intent);
    }

    // ---------------- On Permission Result -------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ){
            case REQUEST_PERMISSION_LOCATION:
                if ((grantResults.length >= 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        ||
                        (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // permissionGranted!
                    onPermissionGranted(OnPermissionListener.PERMISSION_CODE_LOCATION);
                } else {
                    onPermissionGranted(OnPermissionListener.PERMISSION_CODE_LOCATION, false);
                }
                break;
            default:
                onPermissionGranted(-1, false);
                break;

        }
    }


}