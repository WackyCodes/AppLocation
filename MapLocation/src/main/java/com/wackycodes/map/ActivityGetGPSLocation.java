package com.wackycodes.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.wackycodes.map.listener.Constants;
import com.wackycodes.map.listener.OnPermissionListener;
import com.wackycodes.map.util.GPSTracker;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ActivityGetGPSLocation extends BaseActivityLocation
        implements GPSTracker.OnGpsListener {
    private String latLng = "";
    private String addressLine = "";
    private double latitude = 0;
    private double longitude = 0;
    // GPSTracker is main helper class to do most of the things
    private GPSTracker gpsTracker;
    private boolean isGPSOn = false;

    // Location Service..
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public String getLatLng(){
        return latLng;
    }

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

    public abstract void onReceiveLatLng(double lat, double lng, @Nullable String addressLine );

    @Override
    protected void onStart() {
        super.onStart();
        // Assign the gpsTracker ..!
        gpsTracker = new GPSTracker(ActivityGetGPSLocation.this);
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
                gpsTracker.getLocation();
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
                gpsTracker.turnGPSOn(this);
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
            } else {
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
                    if (!ActivityGetGPSLocation.this.isDestroyed()) {
                        getLocation();
                    }
                }
            }, 3000);
        } else {
            gpsTracker.initGPSEnabled();
            // Show In-App System Dialog to enable GPS
            gpsTracker.turnGPSOn(this);
        }
    }

}