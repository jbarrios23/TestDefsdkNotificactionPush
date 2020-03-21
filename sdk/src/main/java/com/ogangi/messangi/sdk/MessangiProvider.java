package com.ogangi.messangi.sdk;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * MessangiProvider allows to initialize it automatically from the SDK itself
 */
public class MessangiProvider extends ContentProvider {
    public static String CLASS_TAG=MessangiProvider.class.getSimpleName();
    public Messangi messangi;
    @Override
    public boolean onCreate() {
        messangi=Messangi.getInst(getContext());
        messangi.utils.showInfoLog(this,"onCreate");
        if(!messangi.storageController.isRegisterDevice()){
            messangi.utils.showInfoLog(this,"Create Device ");
            messangi.createDeviceParameters();
        }

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
