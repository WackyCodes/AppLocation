package com.wackycodes.mapapp.listener;

import android.view.View;

import androidx.annotation.Nullable;

public interface RootListener {

    void showToast(@Nullable String message );
    void showSnackMessage(@Nullable View view, @Nullable String message );

    void showDialog( );
    void dismissDialog( );

}
