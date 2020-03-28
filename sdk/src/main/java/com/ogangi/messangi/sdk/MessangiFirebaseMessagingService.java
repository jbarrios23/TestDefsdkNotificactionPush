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
 * class MessangiFirebaseMessagingService let handle notification push using FirebaseMessagingService .
 *
 */
public class MessangiFirebaseMessagingService extends FirebaseMessagingService  {


    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    private String body ;
    private String title ;
    private String icon;
    private String nameClass;
    private MessangiStorageController messangiStorageController;
    private Messangi messangi;
    private Activity activity;

    /**
     * In this method we receive the 'token' of the device.
     * We need it if we are going to communicate with the device directly.
     */


    @Override
    public void onNewToken(String s) {

        super.onNewToken(s);
        messangi = Messangi.getInst(this);
        messangi.utils.showErrorLog(this,"New Token "+s);
        sendTokenToBackend(s);




    }

    /**
     * Method sendTokenToBackend  allows to take the push token when it is
     * created and save it to update the created device
     * @param tokenPush
     */

    private void sendTokenToBackend(String tokenPush) {


        messangiStorageController =messangi.messangiStorageController;
        messangiStorageController.saveToken(tokenPush);

        if(!messangiStorageController.isNotificationManually()&& messangi.messangiDev!=null){
            messangi.utils.showErrorLog(this,"save in");
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
        super.onMessageReceived(remoteMessage);
        // En este método recibimos el mensaje
        //verifiPermission();
        messangi = Messangi.getInst(this);
        nameClass= messangi.getNameclass();
        messangi.utils.showErrorLog(this,"Name class "+nameClass);

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            messangi.messangiStorageController.saveNotification(title,body);
            messangi.utils.showDebugLog(this,"MENSAJE  " + body);
            messangi.utils.showDebugLog(this,"TITULO  " + title);


        }
        if (remoteMessage.getData().size() > 0) {
            try{
                body = remoteMessage.getData().get("body");
                title=remoteMessage.getData().get("title");
                icon = remoteMessage.getData().get("image");
                messangi.messangiStorageController.saveNotification(title,body);
                messangi.utils.showDebugLog(this,"TITULO  " + title);
                messangi.utils.showDebugLog(this,"MENSAJE  " + body);
                messangi.utils.showDebugLog(this,"ICON  " + icon);
                messangi.utils.showDebugLog(this,"action  " + remoteMessage.getData().get("click_action"));

            } catch (Exception e){
                Log.e("this", "Exception: " + e.getMessage());
            }
        }

        Intent notificationIntent=null;

        try {
            //action defect
            messangi.utils.showErrorLog(this,"action defect "+messangi.identifier);
            notificationIntent = new Intent(this,Class.forName(nameClass));
            if(messangi.identifier==1) {
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(notificationIntent);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            messangi.utils.showErrorLog(this,"error notificationIntent "+e.getMessage());
            notificationIntent = new Intent("com.android.testdefsdknotificactionpush.action.MainActivity");

        }catch (NullPointerException e){
            e.printStackTrace();
            messangi.utils.showErrorLog(this,"error notificationIntent "+e.getMessage());
            notificationIntent = new Intent("com.android.testdefsdknotificactionpush.action.MainActivity");
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
