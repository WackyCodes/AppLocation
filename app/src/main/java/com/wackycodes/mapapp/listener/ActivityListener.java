package com.wackycodes.mapapp.listener;

import androidx.annotation.Nullable;

public interface ActivityListener extends RootListener{
    void showErrorLog(@Nullable String log );
}
