package com.wackycodes.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.wackycodes.map.listener.Constants;
import com.wackycodes.map.listener.OnPermissionListener;
import com.wackycodes.map.util.GPSTracker;

import java.util.Timer;
import java.util.TimerTask;

/*******************************************************************************
 * WackyCodes - Copyright (c) 2022.
 *
 *  This file created by Shailendra Lodhi  on  04/07/2022, 11:03 AM
 *  Check : https://linktr.ee/wackycodes
 *  ===========================================================
 *  File Name : ActivityGetLocationLocation.java
 *  Description :
 *  ======================   Updates History    ========================
 *  S.No. -|-  Updated By -|- Updated Date -|- Remarks
 *  1.    -    Shailendra    -   04/07/2022   -   File Created
 *
 ******************************************************************************/

public abstract class ActivityGetLocationLocation extends BaseActivityLocation implements GPSTracker.OnLocationListener {
    private String latLng = "";
    private String addressLine = "";
    private double latitude = 0;
    private double longitude = 0;
    // GPSTracker is main helper class to do most of the things
    private GPSTracker gpsTracker;
    private boolean isGPSOn = false;

    // Required Enable GPS ---
    private boolean isForcedEnableGps = true;

    // Location Service..
    private FusedLocationProviderClient fusedLocationClient;

    public void setForcedEnableGps(boolean forcedEnableGps) {
        isForcedEnableGps = forcedEnableGps;
    }

    public String getLatLng(){
        return latLng;
    }

    public abstract void onReceiveLatLng(double lat, double lng, @Nullable String addressLine );

    @Override
    public String getAddressLine(){
        return addressLine;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onLoadGPSLocation( double latitude, double longitude, @Nullable String addressLine  ){
        onReceiveLatLng( latitude, longitude, addressLine );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Assign the gpsTracker ..!
        gpsTracker = new GPSTracker(ActivityGetLocationLocation.this);
        if (isLocationPermissionGranted()) {
            // Call method to Get User Location  ..!
            getLocation();
        }else{
            requestForPermission( PERMISSION_CODE_LOCATION );
        }
    }

    // Get User Location : Device Location !
    public void getLocation() {
        showDebugLog("getLocation 1 ");
        runOnUiThread(() -> {
            if (isGPSOn) {
                showDebugLog("getLocation 2 " + isGPSOn);
                gpsTracker.queryToLoadLocation();
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                latLng = String.valueOf(latitude) + "," + String.valueOf(longitude);
                addressLine = gpsTracker.getAddressLine(this);
                // TODO : Set addressLine in your TextView
                showDebugLog("LOCATION : " + "LAT_LNG : " + latLng + " \nADDRESS : " + addressLine);
                if (addressLine == null || latLng.equalsIgnoreCase("0.0,0.0")) {
                    // Call getFusedLocation is User Location Not found!
                    getFusedLocation();
                }else{
                    onReceiveLatLng( latitude, longitude, addressLine );
                }

            } else {
//                new GpsUtils( this ).turnGPSOn( this );
                showDebugLog("getLocation 3 " + isGPSOn);
                gpsTracker.initGPSEnabled();

                // Show In-App System Dialog to enable GPS
                gpsTracker.turnGPSOn(this, null );
            }
        });
    }

    // Get User Location : FusedLocation Service
    //    @SuppressLint("MissingPermission")
    private void getFusedLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showDebugLog("PERMISSION Return ");
            return;
        }

        if (fusedLocationClient == null)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showDebugLog("getLastLocation Success!! ");
                        Location location = task.getResult();
                        if (location != null) {
                            // Logic to handle location object
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            latLng = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                            addressLine = gpsTracker.getAddressLine(this, location, location.getLatitude(), location.getLongitude());
                            // TODO : Set addressLine in your TextView
                            showDebugLog("FUSED_LOC : LAT_LNG : " + latLng + " \nADDRESS : " + addressLine);

                            onReceiveLatLng( location.getLatitude(), location.getLongitude(), addressLine );
                        } else {
                            gpsStatus(isGPSOn);
                        }
                    } else {
                        showDebugLog("getLastLocation Failed!! " + task.getException().getMessage());
                    }
                });
    }

    @Override
    public void onPermissionGranted(int permissionCode) {
//        super.onPermissionGranted(permissionCode);
        if (permissionCode == OnPermissionListener.PERMISSION_CODE_LOCATION) {
            // Call method to Get User Location  ..!
            getLocation();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GPS_REQUEST) { // int GPS_REQUEST = 101;
            // Call gpsStatus until We didn't get GPS Enable
            if (resultCode == RESULT_OK) {
                gpsStatus(true);
            } else if ( isForcedEnableGps ) {
                gpsStatus(false);
            }
        }
    }

    @Override
    public void gpsStatus(boolean isGPSEnable) {
        isGPSOn = isGPSEnable;
        if (isGPSEnable) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!ActivityGetLocationLocation.this.isDestroyed()) {
                        getLocation();
                    }
                }
            }, 3000);
        } else {
            gpsTracker.initGPSEnabled();
            // Show In-App System Dialog to enable GPS
            gpsTracker.turnGPSOn(this, null );
        }
    }

    // Activity Launcher to Enable Location!
    /** You can use it instead of onActivityResult */
    private ActivityResultLauncher<IntentSenderRequest> registerForActivityResult =
            registerForActivityResult( new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                // Call gpsStatus until We didn't get GPS Enable
                if (result.getResultCode() == RESULT_OK) {
                    gpsStatus(true);
                } else if ( isForcedEnableGps ) {
                    gpsStatus(false);
                }
            });



}