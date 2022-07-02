package com.wackycodes.map;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wackycodes.map.listener.ActivityListener;

public class BaseActivity extends AppCompatActivity
        implements ActivityListener {

    @Override
    public void showErrorLog( String logMsg ){
        Log.e( "LOG", " " + logMsg );
    }


    @Override
    public void showToast(@Nullable String message) {

    }



}