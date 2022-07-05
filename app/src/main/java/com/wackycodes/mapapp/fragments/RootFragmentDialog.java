package com.wackycodes.mapapp.fragments;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wackycodes.mapapp.listener.RootListener;

public class RootFragmentDialog extends DialogFragment implements RootListener {

    public RootFragmentDialog() {
    }

    private ProgressDialog dialog;

    public void showDebugLog( @Nullable String message ){
        Log.d( "LOG", "" + message );
    }


    @Override
    public void showToast(@Nullable String message) {
        if (message != null && !message.isEmpty())
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSnackMessage(@Nullable View view, @Nullable String message) {

    }

    @Override
    public void showDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(getContext());
            dialog.setTitle("Please wait...!");
            dialog.setCancelable(false);
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
