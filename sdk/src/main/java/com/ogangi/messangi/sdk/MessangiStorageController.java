package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * class MessangiStorageController is a singletone to save in sharepreference data user and data device
 */
public class MessangiStorageController {

    //private static MessangiStorageController mInstance;
    private static Context contexto;
    private SharedPreferences mSharedPreferences;
    private Messangi messangi;

    public MessangiStorageController(Context context, Messangi messangi){

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

    public void saveDeviceOneByOne(JSONObject value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        try {
            datosuser.putString("id", value.getString("id"));
            datosuser.putString("pushToken", value.getString("pushToken"));
            datosuser.putString("userId", value.getString("userId"));
            datosuser.putString("type", value.getString("type"));
            datosuser.putString("language", value.getString("language"));
            datosuser.putString("model", value.getString("model"));
            datosuser.putString("os", value.getString("os"));
            datosuser.putString("sdkVersion", value.getString("sdkVersion"));
            JSONArray jsonArray=value.getJSONArray("tags");
            List<String> prvTag=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                prvTag.add(jsonArray.getString(i));

            }
            Set<String> set=new HashSet<String>();
            set.addAll(prvTag);
            datosuser.putStringSet("tags",set);
            datosuser.putString("createdAt", value.getString("createdAt"));
            datosuser.putString("updatedAt", value.getString("updatedAt"));
            datosuser.putString("timestamp", value.getString("timestamp"));
            datosuser.putString("transaction", value.getString("transaction"));
            datosuser.apply();
        }catch (JSONException e){

        }
        messangi.utils.showInfoLog(this,"Device Saved in Storage Controller one by one ");

    }
    /**
     * Method isRegisterDevice lets Know if Device is registered in local storage
     *
     */
    public boolean isRegisterDeviceOneByOne(){
        boolean hasToken = false;
        String token=mSharedPreferences.getString("id","");
        if(token.length()>0){
            hasToken=true;

        }
        messangi.utils.showInfoLog(this,"From Local Storage isRegisterDevice one by one "+hasToken);
        return hasToken;
    }
    /**
     * Method get Device registered in local storage
     *
     */
    public MessangiDev getDeviceOneByOne(){

        //Gson gson = new Gson();
        String id=mSharedPreferences.getString("id","");
        String pushToken=mSharedPreferences.getString("pushToken","");
        String userId=mSharedPreferences.getString("userId","");
        String type=mSharedPreferences.getString("type","");
        String language=mSharedPreferences.getString("language","");
        String model=mSharedPreferences.getString("model","");
        String os=mSharedPreferences.getString("os","");
        String sdkVersion=mSharedPreferences.getString("sdkVersion","");
        //String tags=mSharedPreferences.getString("tags","");
        Set<String> set=mSharedPreferences.getStringSet("tags",null);

        String createdAt=mSharedPreferences.getString("createdAt","");
        String updatedAt=mSharedPreferences.getString("updatedAt","");
        String timestamp=mSharedPreferences.getString("timestamp","");
        String transaction=mSharedPreferences.getString("transaction","");
        //MessangiDev messangiDev=gson.fromJson(values,MessangiDev.class);
        MessangiDev messangiDev=new MessangiDev();
        messangiDev.setId(id);
        messangiDev.setPushToken(pushToken);
        messangiDev.setUserId(userId);
        messangiDev.setType(type);
        messangiDev.setLanguage(language);
        messangiDev.setModel(model);
        messangiDev.setOs(os);
        messangiDev.setSdkVersion(sdkVersion);
        List<String> provTags=new ArrayList<String>(set);
        messangiDev.setTags(provTags);
        messangiDev.setCreatedAt(createdAt);
        messangiDev.setUpdatedAt(updatedAt);
        messangiDev.setTimestamp(timestamp);
        messangiDev.setTransaction(transaction);

        messangi.utils.showInfoLog(this,"get Device from Local Storage one by one ");
        return messangiDev;
    }

    public void deleteDeviceTagsOneByOne(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

}
