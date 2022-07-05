package com.wackycodes.map.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.wackycodes.map.listener.OnPermissionListener;


public class BaseFragmentDialog extends DialogFragment implements OnPermissionListener {
    private Context context;

    public BaseFragmentDialog(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void showDebugLog(String logMsg) {
        Log.d("LOG", " " + logMsg);
    }

    /*
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
     */

    private ActivityResultLauncher<String[]> getRequestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (
                        (result.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) && Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION)))
                                &&
                                (result.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION) && Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION)))
                )  {
                    // permissionGranted!
                    onPermissionGranted(OnPermissionListener.PERMISSION_CODE_LOCATION);
                } else {
                    onPermissionGranted(OnPermissionListener.PERMISSION_CODE_LOCATION, false);
                }
            });


    public boolean isLocationPermissionGranted() {
        try {
            return ContextCompat.checkSelfPermission(context != null ? context : getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context != null ? context : getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Note : You can Extent this activity and Override  onPermissionGranted() there to get benefit directly
     */
    @Override
    public void onPermissionGranted(int permissionCode) {
        onPermissionGranted(permissionCode, true);
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

    @Override
    public void requestForPermission(int permissionCode) {
        switch (permissionCode) {
            case PERMISSION_CODE_LOCATION:
                getRequestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
//                requestPermissionLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION );
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
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
        Uri uri = Uri.fromParts("package", (context != null ? context : getContext()).getPackageName(), null);
        intent.setData(uri);
        this.startActivity(intent);
    }


}
