package com.ogangi.messangi.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * class MyFirebaseMessagingService let handle notification push using FirebaseMessagingService .
 *
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService  {


    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    public String body ;
    public String title ;
    public String icon;
    public String nameClass;
    public StorageController storageController;
    public Messangi messangi;
    public Activity activity;

    /**
     * In this method we receive the 'token' of the device.
     * We need it if we are going to communicate with the device directly.
     */

    @Override
    public void onNewToken(String s) {

        super.onNewToken(s);
        messangi = Messangi.getInst(this);
        messangi.utils.showDebugLog(this,"New Token "+s);
        sendTokenToBackend(s);




    }

    /**
     * Method sendTokenToBackend  allows to take the push token when it is
     * created and save it to update the created device
     * @param tokenPush
     */

    private void sendTokenToBackend(String tokenPush) {


        storageController=messangi.storageController;
        storageController.saveToken(tokenPush);

        if(!storageController.isNotificationManually()&& messangi.messangiDev!=null){
            messangi.messangiDev.setPushToken(tokenPush);
            messangi.messangiDev.save(this);
        }


    }


    /**
     * Method onMessageReceived  liste the push notification and let handle message
     * @param remoteMessage
     */
    @SuppressLint("PrivateApi")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // En este método recibimos el mensaje
        //verifiPermission();
        messangi = Messangi.getInst(this);
        nameClass= messangi.getNameclass();
        try {

            body = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();
            icon = remoteMessage.getNotification().getIcon();

            messangi.utils.showDebugLog(this,"MENSAJE IN " + body);
            messangi.utils.showDebugLog(this,"TITULO IN " + title);
            messangi.utils.showDebugLog(this,"IMAGE IN " + title);


        }catch (NullPointerException e){
            messangi.utils.showErrorLog(this,"error "+e.getMessage());

            body = remoteMessage.getData().get("message");
            title = remoteMessage.getData().get("title");
            icon = remoteMessage.getData().get("image");
            messangi.utils.showDebugLog(this,"MENSAJE  " + body);
            messangi.utils.showDebugLog(this,"TITULO  " + title);
            messangi.utils.showDebugLog(this,"IMAGE  " + icon);

        }
        Intent notificationIntent=null;

        try {
            //action defect
            notificationIntent = new Intent(this,Class.forName(nameClass));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }catch (NullPointerException e){
            e.printStackTrace();
            messangi.utils.showErrorLog(this,"error notificationIntent "+e.getMessage());
            notificationIntent = new Intent("android.intent.action.MAIN");
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(messangi.getIcon())  //a resource for your custom small icon
                .setContentTitle(title) //the "title" value you sent in your notification
                .setContentText(body) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}
