package com.wackycodes.map.listener;

/*******************************************************************************
 * WackyCodes - Copyright (c) 2022.
 *
 *  This file created by Shailendra Lodhi  on  04/07/2022, 11:03 AM
 *  Check : https://linktr.ee/wackycodes
 *  ===========================================================
 *  File Name : OnPermissionListener.java
 *  Description :
 *  ======================   Updates History    ========================
 *  S.No. -|-  Updated By -|- Updated Date -|- Remarks
 *  1.    -    Shailendra    -   04/07/2022   -   File Created
 *
 ******************************************************************************/

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

