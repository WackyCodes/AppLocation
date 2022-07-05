package com.wackycodes.mapapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.wackycodes.map.util.GPSTracker;
import com.wackycodes.mapapp.databinding.ActivityMainBinding;
import com.wackycodes.mapapp.fragments.FragmentDialogGetLocation;

import java.util.Timer;
import java.util.TimerTask;

/*******************************************************************************
 * WackyCodes - Copyright (c) 2022.
 *
 *  This file created by Shailendra Lodhi  on  04/07/2022, 11:03 AM
 *  Check : https://linktr.ee/wackycodes
 *  ===========================================================
 *  File Name : MainActivity.java
 *  Description :
 *  ======================   Updates History    ========================
 *  S.No. -|-  Updated By -|- Updated Date -|- Remarks
 *  1.    -    Shailendra    -   04/07/2022   -   File Created
 *
 ******************************************************************************/

public class MainActivity extends RootActivity implements GPSTracker.OnGpsListener {
    private static final String TAG = "MainActivity";


    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( mainBinding.getRoot() );

        mainBinding.buttonMap.setOnClickListener( view -> {
            startActivity( new Intent( this, ActivityShowLocation.class ));
        });

        mainBinding.buttonShowDialog.setOnClickListener( view -> {
            FragmentDialogGetLocation dialogGetLocation = new FragmentDialogGetLocation( this );
            dialogGetLocation.show(getSupportFragmentManager(), "FRAGMENT" );
        });

    }

    // GPSTracker is main helper class to do most of the things
    private GPSTracker gpsTracker;
    private boolean isGPSOn = false;
    /** Activity Launcher to Enable Location!
     * You can use it or get activity Result by using onActivityResult() */
    private ActivityResultLauncher<IntentSenderRequest> registerForActivityResult =
            registerForActivityResult( new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                // Call gpsStatus until We didn't get GPS Enable
                if (result.getResultCode() == RESULT_OK) {
                    gpsStatus(true );
                }else{
                    gpsStatus(false );
                }
            });

    @Override
    protected void onStart() {
        super.onStart();
        // Assign the gpsTracker ..!
        gpsTracker = new GPSTracker( MainActivity.this );
        if ( isLocationPermissionGranted() ) {
            /** Step 1 :- Use Either this
             * If You use Else part in the @gpsStatus method then
             * You can Skip the below code!
             * App System Dialog to enable GPS
             */
            gpsTracker.initGPSEnabled();
            gpsTracker.turnGPSOn(this, registerForActivityResult );

            /** Step 1 :- Or This !
             * If You use Else part in the @gpsStatus method then
             * You can directly call only this method!
             */
//            gpsStatus( isGPSOn );

        }else{
//            requestForPermission( PERMISSION_CODE_LOCATION );
        }
    }

    // Get User Location : Device Location !
    public void getCurrentLocation() {
        runOnUiThread(() -> {
            gpsTracker.queryToLoadLocation();

            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            String addressLine = gpsTracker.getAddressLine(this);

            if ( addressLine == null || ( latitude + "," + longitude ).equalsIgnoreCase("0.0,0.0")) {
                /** Call setOnLoadFusedLocation if User Location Not found
                 * If you wants to used Fused location, call it!
                 */
                gpsTracker.setOnLoadFusedLocation( this, this );
            }else {
                onLoadGPSLocation( latitude, longitude, addressLine );
            }
        });
    }

    @Override
    public void gpsStatus(boolean isGPSEnable) {
        isGPSOn = isGPSEnable;
        if ( isGPSEnable) {
            // Query To Fetch the current Location!
            getCurrentLocation();
        } else // if ( isForcedEnableGps )
        {
            gpsTracker.initGPSEnabled();
            // Show In-App System Dialog to enable GPS
            gpsTracker.turnGPSOn(this, registerForActivityResult );
        }
    }

    @Override
    public void onLoadGPSLocation(double latitude, double longitude, @Nullable String addressLine) {
        Log.e(TAG, "Address : " + addressLine + " || " + latitude + ", " + longitude );
        // Address is not Fetched ! @REPEAT..!! ( OPTIONAL )
        if ( addressLine == null || ( latitude + "," + longitude ).equalsIgnoreCase("0.0,0.0") ){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!MainActivity.this.isDestroyed()) {
                        getCurrentLocation();
                    }
                }
            }, 3000);
        }
        else{
            // TODO : Use the lat & lng
        }
    }


}