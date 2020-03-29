package com.android.testdefsdknotificactionpush;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.ogangi.messangi.sdk.MessangiFirebaseMessagingService;

public class TheBestNameMeesangiFirebaseMessangiService extends MessangiFirebaseMessagingService {

    public static String CLASS_TAG=TheBestNameMeesangiFirebaseMessangiService.class.getSimpleName();
    public static String TAG="MessangiSDK";
    private String body ;
    private String title ;
    private String icon;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //super.onNewToken(s);//lo dejo para que haga el comportamiento del padre
        Log.e(TAG,CLASS_TAG+": new token push from app "+s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, CLASS_TAG + ": remote message ");


        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            icon = remoteMessage.getNotification().getIcon();
            Log.i(TAG, CLASS_TAG + "MENSAJE IN " + body);
            Log.i(TAG, CLASS_TAG + "TITULO IN " + title);
            Log.i(TAG, CLASS_TAG + "IMAGE IN " + icon);
        } else if (remoteMessage.getData().size() > 0) {
            try {
                body = remoteMessage.getData().get("body");
                title = remoteMessage.getData().get("title");
                icon = remoteMessage.getData().get("icon");
                Log.i(TAG, CLASS_TAG + "MENSAJE IN " + body);
                Log.i(TAG, CLASS_TAG + "TITULO IN " + title);
                Log.i(TAG, CLASS_TAG + "IMAGE IN " + icon);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());

            }
        }
    }
}
