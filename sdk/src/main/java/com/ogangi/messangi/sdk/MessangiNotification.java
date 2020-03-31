package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessangiNotification implements Serializable {


    private String title="";
    private String body="";
    private Map<String, String> data;
    private String icon="";
    private String id="";
    Messangi messangi=Messangi.getInst();
    public Context context;
    public String currentDate;
    public String currentTime;

    public MessangiNotification(RemoteMessage remoteMessage, Context context) {
        this.context=context;
        this.currentDate= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        this.currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        id=remoteMessage.getMessageId();
        if (remoteMessage.getData().size() > 0) {
            body = remoteMessage.getData().get("body");
            title=remoteMessage.getData().get("title");
            icon = remoteMessage.getData().get("icon");
            data=remoteMessage.getData();
            messangi.utils.showDebugLog(this,"DATA  " +remoteMessage.getData());
            messangi.utils.showDebugLog(this,"TITLE  " + title);
            messangi.utils.showDebugLog(this,"MESSAGE  " + body);
            messangi.utils.showDebugLog(this,"ICON  " + icon);
            messangi.utils.showDebugLog(this,"TIME:  " + currentTime+ "DATE: "+currentDate);
        }else if(remoteMessage.getNotification() != null){
            try{
                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();
                icon=remoteMessage.getNotification().getIcon();
                messangi.messangiStorageController.saveNotification(title,body);
                messangi.utils.showDebugLog(this,"TITLE  " + title);
                messangi.utils.showDebugLog(this,"MESSAGE  " + body);
                messangi.utils.showDebugLog(this,"ICON  " + icon);
                messangi.utils.showDebugLog(this,"TIME:  " + currentTime+ "DATE: "+currentDate);
            } catch (Exception e){
                messangi.utils.showErrorLog(this,"Exception: " + e.getMessage());

            }
        }
     messangi.setLastMessangiNotifiction(this);
     messangi.getMessangiNotifications().add(0,this);
     messangi.utils.showErrorLog(this,messangi.getMessangiNotifications().size());
     sendEventToActivity(this,this.context);


    }

    public MessangiNotification(Bundle extras, Context context) {
        this.context=context;
        this.currentDate= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        this.currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if(extras!=null){
            data=new HashMap<>();
            for(String key:extras.keySet()){
 //              Object value = getIntent().getExtras().get(key);
//                Log.e(TAG,CLASS_TAG+"Extras received at onCreate:  Key: " + key + " Value: " + value);
                messangi.utils.showDebugLog(this,"Extras received:  Key: " + key + " Value: " + extras.getString(key));
                data.put(key,extras.getString(key));

                if(key.equals("title")){
                title=extras.getString(key);
                }else if(key.equals("body")){
                body=extras.getString(key);
                }else if(key.equals("id")){
                id=extras.getString(key);
                }
            }
            messangi.setLastMessangiNotifiction(this);
            messangi.getMessangiNotifications().add(0,this);
            }


    }

    public MessangiNotification() {

    }


    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }


    /**
     * Method that send Parameter (Ej: messangiDev or MessangiUserDevice) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {
        Messangi messangi=Messangi.getInst();
        Intent intent=new Intent("PassDataFromoSdk");
        intent.putExtra("message",something);
        if(something!=null){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            messangi.utils.showInfoLog(this,"Not Send Broadcast ");
        }

    }

}
