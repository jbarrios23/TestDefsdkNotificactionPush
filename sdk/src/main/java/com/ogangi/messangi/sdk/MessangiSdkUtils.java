package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessangiSdkUtils {

    public static String CLASS_TAG= MessangiSdkUtils.class.getSimpleName();
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
            showErrorLog(MessangiSdkUtils.class,"Hasn't congifg file");
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
        MessangiSdkUtils.messangi_host = messangi_host;
    }

    /**
     * Method get messangi_token
     */
    public static String getMessangi_token() {
        return messangi_token;
    }

    public static void setMessangi_token(String messangi_token) {
        MessangiSdkUtils.messangi_token = messangi_token;
    }
//    /**
//     * Method get Gson format
//     * @param object
//     */
//    public String getGsonJsonFormat(Object object){
//        Object obj = null;
//        if(object instanceof MessangiDev){
//            obj= object;
//        }else if(object instanceof MessangiUserDevice){
//            obj= object;
//        }
//
//        return new Gson().toJson(obj);
//    }

    public MessangiDev getMessangiDevFromJson(JSONObject resp){
        MessangiDev messangiDev=new MessangiDev();
        try {
        if(resp.has("id")){

        messangiDev.setId(resp.getString("id"));

        }
        if(resp.has("pushToken")){
            messangiDev.setPushToken(resp.getString("pushToken"));
        }
        if(resp.has("userId")){
            messangiDev.setUserId(resp.getString("userId"));
        }
        if(resp.has("type")){
            messangiDev.setType(resp.getString("type"));
        }
        if(resp.has("language")){
            messangiDev.setLanguage(resp.getString("language"));
        }
        if(resp.has("model")){
            messangiDev.setModel(resp.getString("model"));
        }
        if(resp.has("os")){
            messangiDev.setOs(resp.getString("os"));
        }
        if(resp.has("sdkVersion")){
            messangiDev.setSdkVersion(resp.getString("sdkVersion"));
        }
        if(resp.has("tags")){
            List<String> prvTag=new ArrayList<>();
            JSONArray jsonArray=resp.getJSONArray("tags");
            for (int i = 0; i < jsonArray.length(); i++) {
                prvTag.add(jsonArray.getString(i));
            }
            messangiDev.setTags(prvTag);
            showInfoLog(this, "tags " + resp.getString("tags"));
        }
        if(resp.has("createdAt")){
            messangiDev.setCreatedAt(resp.getString("createdAt"));
        }
        if(resp.has("updatedAt")){
            messangiDev.setUpdatedAt(resp.getString("updatedAt"));
        }
        if(resp.has("timestamp")){
            messangiDev.setTimestamp(resp.getString("timestamp"));
        }
        if(resp.has("transaction")){
            messangiDev.setTransaction(resp.getString("transaction"));
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messangiDev;
    }

}
