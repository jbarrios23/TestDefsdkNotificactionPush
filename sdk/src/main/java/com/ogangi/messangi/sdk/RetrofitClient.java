package com.ogangi.messangi.sdk;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    //This class will create a singleton of Retrofit

    private static Retrofit retrofit=null;
    private static String token;
    private static String CLASS_TAG=RetrofitClient.class.getSimpleName();

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    /**
     * Method get client Retrofit
     * @param context :Instance context
     * @param Url : Url of services.
     * @param token1: Token Auth from config file.
     */
    public static Retrofit getClient(String Url, String token1, Context context){
        Messangi messangi=Messangi.getInst(context);
        token=token1;
        messangi.utils.showErrorLog(CLASS_TAG,token);
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(Url)
                    .client(getHeader(token))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        messangi.utils.showDebugLog(CLASS_TAG,"Load Url "+ Url);

        return retrofit;
    }


    public static Retrofit getClientAlt(String Url){

        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(Url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        Log.d(CLASS_TAG, "Load Url "+ Url);
        return retrofit;
    }

    public static Retrofit putNull(){


        retrofit=null;

        return retrofit;
    }
    /**
     * Method get Header Retrofit
     * @param authorizationValue :receive token auth of config file
     * use: "--Authorization-- ","Bearer " + authorizationValue
     */
    public static OkHttpClient getHeader(final String authorizationValue ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Request request = null;
                                if (authorizationValue != null) {

                                    Log.d("--Authorization-- ","Bearer " + authorizationValue);

                                    Request original = chain.request();
                                    // Request customization: add request headers
                                    Request.Builder requestBuilder = original.newBuilder()
                                            .addHeader("Authorization","Bearer " +authorizationValue);

                                    request = requestBuilder.build();
                                }
                                return chain.proceed(request);
                            }
                        })
                .build();
        return okClient;

    }

}
