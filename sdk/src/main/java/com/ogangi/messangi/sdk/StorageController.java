package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

/**
 * class StorageController is a singletone to save in sharepreference data user and data device
 */
public class StorageController {

    //private static StorageController mInstance;
    private static Context contexto;
    private SharedPreferences mSharedPreferences;
    private Messangi messangi;

    public StorageController(Context context,Messangi messangi){

        this.contexto=context;
        this.messangi=messangi;
        this.mSharedPreferences = contexto.getApplicationContext().getSharedPreferences("StorageCallback", 0);

    }
    /**
     * Method save token
     * @param token: token push of Firebase
     */
    public void saveToken(String token){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putString("Token",token);
        datosuser.apply();
        messangi.utils.showInfoLog(this,"Token Push Saved");

    }
    /**
     * Method hasTokenRegiter lets Know if token is registered in local storage
     *
     */
    public boolean hasTokenRegiter(){
        boolean hasToken = false;
        String token=mSharedPreferences.getString("Token","");
        if(token.length()>0){
            hasToken=true;

        }
        messangi.utils.showInfoLog(this,"Token Push from storage controller");
        return hasToken;
    }
    /**
     * Method get token registered in local storage
     *
     */
    public String getToken(){

        String token=mSharedPreferences.getString("Token","");

        messangi.utils.showInfoLog(this,"get token push"+token);
        return token;
    }

    public void deleteToken(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

    /**
     * Method save Device registered in local storage
     *
     */

    public void saveDevice(MessangiDev value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTags = gson.toJson(value);
        datosuser.putString("MessangiDev",jsonTags);
        datosuser.apply();
        messangi.utils.showInfoLog(this,"Device Saved in Storage Controller ");

    }
    /**
     * Method isRegisterDevice lets Know if Device is registered in local storage
     *
     */
    public boolean isRegisterDevice(){
        boolean hasToken = false;
        String token=mSharedPreferences.getString("MessangiDev","");
        if(token.length()>0){
            hasToken=true;

        }
        messangi.utils.showInfoLog(this,"From Local Storage isRegisterDevice "+hasToken);
        return hasToken;
    }
    /**
     * Method get Device registered in local storage
     *
     */
    public MessangiDev getDevice(){

        Gson gson = new Gson();
        String values=mSharedPreferences.getString("MessangiDev","");
        MessangiDev messangiDev=gson.fromJson(values,MessangiDev.class);

        messangi.utils.showInfoLog(this,"get Device from Local Storage ");
        return messangiDev;
    }

    public void deleteDeviceTags(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

    /**
     * Method save User By Device registered in local storage
     *
     */
    public void saveUserByDevice(MessangiUserDevice value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTags = gson.toJson(value);
        datosuser.putString("MessangiUserDevice",jsonTags);
        datosuser.apply();
        messangi.utils.showInfoLog(this,"User Saved in Storage Controller ");

    }
    /**
     * Method isRegisterUserByDevice lets Know if Device is registered in local storage
     *
     */
    public boolean isRegisterUserByDevice(){
        boolean hasToken = false;
        String token=mSharedPreferences.getString("MessangiUserDevice","");
        if(token.length()>0){
            hasToken=true;

        }
        messangi.utils.showInfoLog(this,"From Local Storage isRegisterUser "+hasToken);
        return hasToken;
    }

    /**
     * Method get User registered in local storage
     *
     */

    public MessangiUserDevice getUserByDevice(){

        Gson gson = new Gson();
        String values=mSharedPreferences.getString("MessangiUserDevice","");
        MessangiUserDevice messangiUserDevice=gson.fromJson(values,MessangiUserDevice.class);
        messangi.utils.showInfoLog(this,"get User from Local Storage ");
        return messangiUserDevice;
    }

    public void deleteUserByDevice(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }
    /**
     * Method setNotificationManually let enable Notification Manually
     * @param enable : enable
     */
    public void setNotificationManually(boolean enable){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putBoolean("DisableForUser",enable);
        datosuser.apply();
        messangi.utils.showDebugLog(this,"Set disable for user "+enable);

    }


    public boolean isNotificationManually(){

     return mSharedPreferences.getBoolean("DisableForUser",false);
    }

}
