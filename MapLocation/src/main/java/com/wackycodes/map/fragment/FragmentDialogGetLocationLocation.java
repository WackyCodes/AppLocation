package com.wackycodes.map.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.wackycodes.map.listener.OnPermissionListener;
import com.wackycodes.map.util.GPSTracker;

import java.util.Timer;
import java.util.TimerTask;

public abstract class FragmentDialogGetLocationLocation extends BaseFragmentDialog  implements GPSTracker.OnLocationListener {
    public FragmentDialogGetLocationLocation(Context context) {
        super(context);
        this.context = context;
    }

    private Context context;

    private String latLng = "";
    private String addressLine = "";
    private double latitude = 0;
    private double longitude = 0;
    // GPSTracker is main helper class to do most of the things
    private GPSTracker gpsTracker;
    private boolean isGPSOn = false;
    private boolean isShowing = true;

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
    public void onStart() {
        super.onStart();
        // Assign the gpsTracker ..!
        gpsTracker = new GPSTracker( getCurrContext() );
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
        if (isGPSOn) {
            showDebugLog("getLocation : GPS On... " );

            requireActivity().runOnUiThread(() -> {
                gpsTracker.queryToLoadLocation();
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                latLng = String.valueOf(latitude) + "," + String.valueOf(longitude);
                addressLine = gpsTracker.getAddressLine( getCurrContext() );
                // TODO : Set addressLine in your TextView
                showDebugLog("LOCATION : " + "LAT_LNG : " + latLng + " \nADDRESS : " + addressLine);
                if (addressLine == null || latLng.equalsIgnoreCase("0.0,0.0")) {
                    // Call getFusedLocation is User Location Not found!
                    getFusedLocation();
                }else{
                    onReceiveLatLng( latitude, longitude, addressLine );
                }
            });

        } else {
            showDebugLog("getLocation : GPS is not On");
            gpsTracker.initGPSEnabled();

            // Show In-App System Dialog to enable GPS
            gpsTracker.turnGPSOn(this, registerForActivityResult );
        }
    }

    // Get User Location : FusedLocation Service
    //    @SuppressLint("MissingPermission")
    @SuppressLint("MissingPermission")
    private void getFusedLocation() {
        if ( !isLocationPermissionGranted() ){
            return;
        }

        if (fusedLocationClient == null)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient( getCurrContext() );

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
                            addressLine = gpsTracker.getAddressLine( getCurrContext(), location, location.getLatitude(), location.getLongitude());
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
    public void gpsStatus(boolean isGPSEnable) {
        isGPSOn = isGPSEnable;
        if (isGPSEnable) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if ( isShowing ) {
                        getLocation();
                    }
                }
            }, 3000);
        } else {
            gpsTracker.initGPSEnabled();
            // Show In-App System Dialog to enable GPS
            gpsTracker.turnGPSOn(this, registerForActivityResult);
        }
    }

    // Activity Launcher to Enable Location!
    private ActivityResultLauncher<IntentSenderRequest> registerForActivityResult =
            registerForActivityResult( new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                // Call gpsStatus until We didn't get GPS Enable
                if (result.getResultCode() == RESULT_OK) {
                    gpsStatus(true);
                } else if ( isForcedEnableGps ) {
                    gpsStatus(false);
                }
            });

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        this.isShowing = false;
    }

    private Context getCurrContext( ){
        return context != null ? context : requireContext();
    }


}
