package com.wackycodes.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wackycodes.map.listener.OnPermissionListener;

public class BaseActivity extends AppCompatActivity
        implements OnPermissionListener {
    private static final int REQUEST_PERMISSION_STORAGE = 100;
    private static final int REQUEST_PERMISSION_LOCATION = 101;

    public void showErrorLog( String logMsg ){
        Log.e( "LOG", " " + logMsg );
    }

    /**
     * Note : You can Extent this activity and Override  onPermissionGranted() there to get benefit directly
     */
    @Override
    public void onPermissionGranted(int permissionCode) {
        switch (permissionCode) {
            // case OnPermissionListener.PERMISSION_CODE_STORAGE:
            //      showFragmentDialog();
            //   break;
            case OnPermissionListener.PERMISSION_CODE_LOCATION:
                // getLocation();
                // TODO : Call the method or code after permission granted!
                break;
        }
    }

    //---------- Check User Permission -------------------------------------------------------------
    public boolean isLocationPermissionGranted() {
        try {
            if ( ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                        REQUEST_PERMISSION_LOCATION
                );
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ---------------- On Permission Result -------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //        if (requestCode == REQUEST_PERMISSION_STORAGE
        //                && grantResults[0] == PackageManager.PERMISSION_GRANTED
        //                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        //            onPermissionGranted(OnPermissionListener.PERMISSION_CODE_STORAGE);
        //        }
        //        else
        if (requestCode == REQUEST_PERMISSION_LOCATION
                & grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // permissionGranted!
            onPermissionGranted(OnPermissionListener.PERMISSION_CODE_LOCATION);
        } else {
            //  showToast("\"Permission denied.\"");
        }
    }

}