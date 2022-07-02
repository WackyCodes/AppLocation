package com.wackycodes.map;
// Extend our BaseActivity to get benefit of common code ( OOPs )!

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.wackycodes.map.listener.Constants;
import com.wackycodes.map.util.GPSTracker;

import java.util.Timer;
import java.util.TimerTask;

public class ActivityLocation extends BaseActivity
        implements GPSTracker.OnGpsListener {

    private String latLng = "";
    // GPSTracker is main helper class to do most of the things
    private GPSTracker gpsTracker;
    private boolean isGPSOn = false;

    // Location Service..
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ...

        // Assign the gpsTracker ..!
        gpsTracker = new GPSTracker(ActivityLocation.this);
        if (isLocationPermissionGranted()) {
            // Call method to Get User Location  ..!
            getLocation();
        }
    }

    // Get User Location : Device Location !
    public void getLocation() {
        showErrorLog( "getLocation 1 " );
        runOnUiThread(() -> {
            if (isGPSOn) {
                showErrorLog( "getLocation 2 " + isGPSOn );
                gpsTracker.getLocation();
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                latLng = String.valueOf(latitude) + "," + String.valueOf(longitude);
                String addressLine = gpsTracker.getAddressLine(this);
                // TODO : Set addressLine in your TextView

                showErrorLog("LOCATION LAT_LNG : " + latLng + " \nADDRESS : " + addressLine);
                if (addressLine == null || latLng.equalsIgnoreCase("0.0,0.0")) {
                    // Call getFusedLocation is User Location Not found!
                    getFusedLocation();
                }

            } else {
//                new GpsUtils( this ).turnGPSOn( this );
                showErrorLog( "getLocation 3 " + isGPSOn );
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
            showErrorLog( "PERMISSION Return " );
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()){
                        showErrorLog( "getLastLocation Success!! ");
                        Location location = task.getResult();
                        if (location != null) {
                            // Logic to handle location object
                            latLng = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                            String addressLine = gpsTracker.getAddressLine(this, location, location.getLatitude(), location.getLongitude());
                            // TODO : Set addressLine in your TextView

                            showErrorLog("FUSED_LOC : LAT_LNG : " + latLng + " \nADDRESS : " + addressLine);
                        }else{
                            gpsStatus( isGPSOn );
                        }
                    }else{
                        showErrorLog( "getLastLocation Failed!! " + task.getException().getMessage() );
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GPS_REQUEST ) { // int GPS_REQUEST = 101;

            // Call gpsStatus until We didn't get GPS Enable
            if (resultCode == Activity.RESULT_OK){
                gpsStatus( true );
            }else{
                gpsStatus( false );
            }
        }
    }

    @Override
    public void gpsStatus(boolean isGPSEnable) {
        isGPSOn = isGPSEnable;
        if (isGPSEnable){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!ActivityLocation.this.isDestroyed()){
                        getLocation();
                    }
                }
            }, 3000);
        }else{
            gpsTracker.initGPSEnabled();
            // Show In-App System Dialog to enable GPS
            gpsTracker.turnGPSOn( this );
        }
    }



}