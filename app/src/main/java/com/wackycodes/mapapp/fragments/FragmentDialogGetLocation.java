package com.wackycodes.mapapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wackycodes.map.fragment.FragmentDialogGetLocationLocation;
import com.wackycodes.mapapp.R;
import com.wackycodes.mapapp.databinding.FragmentDialogGetLocationBinding;

public class FragmentDialogGetLocation extends FragmentDialogGetLocationLocation implements OnMapReadyCallback {

    public FragmentDialogGetLocation( Context context ) {
        super( context );
    }

    private FragmentDialogGetLocationBinding locationBinding;
    // Just put this code above onCreateView method
    @Override
    public int getTheme() {
        return R.style.DialogTheme;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        locationBinding = FragmentDialogGetLocationBinding.inflate( inflater, container, false );


        // Obtain the SupportMapFragment and get notified when the mapapp is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.myLocationFragmentMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return locationBinding.getRoot();
    }

    private GoogleMap mMap;

    /**
     * Manipulates the mapapp once available.
     * This callback is triggered when the mapapp is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showDebugLog("Map is Ready!");
        String latLng = getLatLng();
        if ( latLng.isEmpty() || latLng.equalsIgnoreCase("0.0,0.0") ){
            // Lat Lng is not found Yet!
            return;
        }

        String [] latLngString = latLng.split(",");
        // Add a marker in Sydney and move the camera
        setMyLocationMarker( Double.parseDouble(latLngString[0]), Double.parseDouble(latLngString[1]), null );
    }
    private void setMyLocationMarker( double latitude, double longitude, @Nullable String addressLine ){
        if (addressLine!=null){
            locationBinding.textViewAddress.setText( addressLine );
        }
        if (mMap == null){
            return;
        }

        mMap.clear();
        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title( addressLine!=null? addressLine : "My Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation ));

        // Move the camera instantly to Sydney with a zoom of 15.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

        // Zoom in, animating the camera.
//        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation )      // Sets the center of the mapapp to Mountain View
                .zoom(17)                   // Sets the zoom
//                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onReceiveLatLng(double latitude, double longitude, @Nullable String addressLine) {
        setMyLocationMarker( latitude, longitude, addressLine );
    }



}