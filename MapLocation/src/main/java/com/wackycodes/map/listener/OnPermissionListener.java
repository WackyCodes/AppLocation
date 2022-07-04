package com.wackycodes.map.listener;

public interface OnPermissionListener {
    int PERMISSION_CODE_LOCATION = 101;

    void onPermissionGranted( int PERMISSION_CODE );
    void onPermissionGranted( int PERMISSION_CODE, boolean isGrant );

    boolean isLocationPermissionGranted();

    void requestForPermission( int permissionCode );
    void requestForcePermission( int permissionCode );

    interface OnPermissionDialogListener {
        void showPermissionDialog( int permissionCode, boolean isForcePermission );
        void acceptLaterAction( int permissionCode );
    }

}

