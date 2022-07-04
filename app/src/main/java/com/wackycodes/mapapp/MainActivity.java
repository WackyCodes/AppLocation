package com.wackycodes.mapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.SupportMapFragment;
import com.wackycodes.mapapp.databinding.ActivityMainBinding;
import com.wackycodes.mapapp.databinding.ActivityShowLocationBinding;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( mainBinding.getRoot() );


        mainBinding.buttonMap.setOnClickListener( view -> {
            startActivity( new Intent( this, ActivityShowLocation.class ));
        });

    }



}