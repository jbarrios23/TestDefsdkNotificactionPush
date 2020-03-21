package com.ogangi.messangi.sdk;

import android.content.Context;

public class ApiUtils {

   public static Messangi messangi;
    /**
     * Method for get client retrofit
     * @param context :Instance context
     */
   public static EndPoint getSendMessageFCM(Context context){
        messangi=Messangi.getInst(context);
        String url= SdkUtils.getMessangi_host();
        String token=SdkUtils.getMessangi_token();
        return RetrofitClient.getClient(url,token,context).create(EndPoint.class);
   }

    public static EndPoint getSendMessageFCMAlt(Context context){
        messangi=Messangi.getInst(context);
        String url=SdkUtils.getMessangi_host();
        return RetrofitClient.getClientAlt(url).create(EndPoint.class);
    }


}


