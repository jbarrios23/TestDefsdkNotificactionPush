package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;

import com.google.gson.Gson;

public class SdkUtils {

    public static String CLASS_TAG=SdkUtils.class.getSimpleName();
    public static String TAG="MessangiSDK";
    static int icon;
    public static String nameClass;
    private static String messangi_host;
    private static String messangi_token;
    private static boolean analytics_allowed;
    private static boolean location_allowed;
    private static boolean logging_allowed;

    /**
     * Method init Resourses system frmo config file
     * @param context
     */
    public void initResourcesConfigFile(Context context){

        try {

            int key_logging_allowed = context.getResources()
                    .getIdentifier("logging_allowed", "bool", context.getPackageName());
            logging_allowed = context.getResources().getBoolean(key_logging_allowed);
            showInfoLog(this, logging_allowed);
            int key_messagi_host = context.getResources()
                    .getIdentifier("messangi_host", "string", context.getPackageName());
            messangi_host = context.getString(key_messagi_host);
            showInfoLog(this, messangi_host);
            int key_messangi_app_token = context.getResources()
                    .getIdentifier("messangi_app_token", "string", context.getPackageName());
            messangi_token = context.getString(key_messangi_app_token);
            showInfoLog(this, messangi_token);
            int key_analytics_allowed = context.getResources()
                    .getIdentifier("analytics_allowed", "bool", context.getPackageName());
            analytics_allowed = context.getResources().getBoolean(key_analytics_allowed);
            showInfoLog(this, analytics_allowed);
            int key_location_allowed = context.getResources()
                    .getIdentifier("location_allowed", "bool", context.getPackageName());
             location_allowed = context.getResources().getBoolean(key_location_allowed);
            showInfoLog(this, location_allowed);

        }catch (Resources.NotFoundException e){
            showErrorLog(SdkUtils.class,"Hasn't congifg file");
        }
    }

    /**
     * Method Show Error log
     @param instance: intance for Tag.
     @param message : message to show
     */
    public  void showErrorLog(Object instance,Object message){
        if(logging_allowed) {
            Log.e(TAG,instance.getClass().getSimpleName()+": "+ message);
        }
    }

    /**
     * Method Show Error log
     @param instance: intance for Tag.
     @param message : message to show
     */
    public  void showDebugLog(Object instance,Object message){
        if(logging_allowed){
            Log.d(TAG,instance.getClass().getSimpleName()+": "+ message);
        }

    }
    /**
     * Method Show Error log
     @param instance: intance for Tag.
     @param message : message to show
     */
    public  void showInfoLog(Object instance,Object message){
        if(logging_allowed) {
            Log.i(TAG,instance.getClass().getSimpleName()+": "+ message);
        }
    }

    /**
     * Method get messangi_host
     */

    public static String getMessangi_host() {
        return messangi_host;
    }

    public static void setMessangi_host(String messangi_host) {
        SdkUtils.messangi_host = messangi_host;
    }

    /**
     * Method get messangi_token
     */
    public static String getMessangi_token() {
        return messangi_token;
    }

    public static void setMessangi_token(String messangi_token) {
        SdkUtils.messangi_token = messangi_token;
    }
    /**
     * Method get Gson format
     * @param object 
     */
    public String getGsonJsonFormat(Object object){
        Object obj = null;
        if(object instanceof MessangiDev){
            obj= object;
        }else if(object instanceof MessangiUserDevice){
            obj= object;
        }

        return new Gson().toJson(obj);
    }



}
