package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * class MessangiDev is used for handle Device paramenter in SDK and service
 */

public class MessangiDev implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("pushToken")
    @Expose
    private String pushToken;
    @SerializedName("userId")
    @Expose
    protected String userId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("os")
    @Expose
    private String os;
    @SerializedName("sdkVersion")
    @Expose
    private String sdkVersion;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("transaction")
    @Expose
    private String transaction;

    /**
     * Method that make Update of Device using the service Put
     @param context
     @serialData :MessangiDev
     */

    public void save(final Context context){

        final Messangi messangi=Messangi.getInst(context);
        final StorageController storageController=Messangi.getInst().storageController;
        EndPoint endPoint= ApiUtils.getSendMessageFCM(context);
        messangi.utils.showInfoLog(this," Id "+id+" pushToken "+pushToken+" "+tags.toString());
        JsonObject gsonObject = new JsonObject();
        JSONObject requestUpdatebody=requestJsonBodyForUpdate(pushToken,context);
        JsonParser jsonParser=new JsonParser();
        gsonObject=(JsonObject) jsonParser.parse(requestUpdatebody.toString());
        endPoint.putDeviceParameter(id,gsonObject).enqueue(new Callback<MessangiDev>() {
                @Override
                public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {
                    messangi.utils.showInfoLog(this,"Update Device good "+new Gson().toJson(response.body()));
                    if(response.isSuccessful()){
                        MessangiDev messangiDev=response.body();
                        storageController.saveDevice(response.body());
                        sendEventToActivity(messangiDev,context);
                    }else{
                        int code=response.code();
                        messangi.utils.showErrorLog(this,"Code update error "+code);
                        sendEventToActivity(null,context);
                    }
                }

                @Override
                public void onFailure(Call<MessangiDev> call, Throwable t) {
                    messangi.utils.showErrorLog(this,"onFailure put service "+t.getMessage());
                    sendEventToActivity(null,context);
                }
            });

    }

    /**
     * Method for get Device registered from service
     @param context: instance context
     @param forsecallservice : allows effective device search in three ways: by instance, by shared variable or by service.
     */
    public void requestUserByDevice(final Context context, boolean forsecallservice){
        final Messangi messangi=Messangi.getInst(context);
        final StorageController storageController=Messangi.getInst().storageController;
        if(!forsecallservice && messangi.messangiUserDevice!=null){
            messangi.utils.showInfoLog(this,"User From RAM ");
            sendEventToActivity(messangi.messangiUserDevice,context);
        }else {
            if (!forsecallservice && storageController.isRegisterUserByDevice()) {
                messangi.messangiUserDevice = storageController.getUserByDevice();
                sendEventToActivity(messangi.messangiUserDevice,context);
                messangi.utils.showInfoLog(this,"User From Local storage ");
            } else {

                    messangi.utils.showInfoLog(this, "User From Service ");
                    EndPoint endPoint = ApiUtils.getSendMessageFCM(context);
                    endPoint.getUserByDevice(id).enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            if (response.isSuccessful()) {

                                Map<String, String> responseBody = response.body();
                                MessangiUserDevice messangiUserDevice;
                                messangiUserDevice = MessangiUserDevice.parseData(responseBody);
                                messangiUserDevice.id = userId;
                                storageController.saveUserByDevice(messangiUserDevice);
                                sendEventToActivity(messangiUserDevice, context);
                                messangi.utils.showInfoLog(this,"Request user device successful");

                            } else {

                                int code = response.code();
                                messangi.utils.showErrorLog(this, "code getUser by device " + code);
                                sendEventToActivity(null, context);
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            messangi.utils.showErrorLog(this, "onFailure " + t.getMessage());
                            sendEventToActivity(null, context);
                        }
                    });
            }
        }

    }

    /**
     * Method for get status of enable notification push
     */

    public boolean isEnableNotificationPush(){
        return pushToken!=null && pushToken!="";
    }

    /**
     * Method for get Device registered from service
     @param context: instance context
     @param enable : boolean enable.
     */
    public void setStatusNotificationPush(boolean enable,Context context){
        final StorageController storageController=Messangi.getInst().storageController;
        storageController.setNotificationManually(true);
        if(storageController.hasTokenRegiter()&& enable){
            pushToken=storageController.getToken();

        }else{
            pushToken="";

        }
        save(context);
    }

    /**
     * Method for check Sdk Veriosn and Languaje, if one of these parameters changes
     * immediately it is updated in the database.
     @param context: instance context
     */

    public void checkSdkVersion(Context context) {
        final Messangi messangi=Messangi.getInst(context);
        String sdkVersionInt = BuildConfig.VERSION_NAME; // sdk version;
        if(getSdkVersion().equals("0") || !getSdkVersion().equals(sdkVersionInt)){
            setSdkVersion(sdkVersionInt);
            messangi.utils.showDebugLog(this,"Update sdk version ");
            try {
                Thread.sleep(1000);
                save(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            messangi.utils.showDebugLog(this,"Not update sdk version ");
        }
        String lenguaje= Locale.getDefault().getDisplayLanguage();
        //messangi.utils.showInfoLog(this,"DEVICE LENGUAJE "+lenguaje);
        if(getLanguage().equals("0") || !getLanguage().equals(lenguaje)){
            setLanguage(lenguaje);
            messangi.utils.showDebugLog(this,"Update lenguaje ");
            try {
                Thread.sleep(3000);
                save(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{
            messangi.utils.showDebugLog(this,"Not update lenguaje ");
        }

    }
    /**
     * Method for add nes Tags to Device, then you can do save and
     * immediately it is updated in the database.
     @param newTags: new Tags for add
     */

    public void addTagsToDevice(String newTags){

        if(tags!=null){
            tags.add(newTags);

        }else{
            tags=new ArrayList<>();
            tags.add(newTags);

        }

    }
    /**
     * Method for clear all Tags selected in local
     *
     */
    public void clearTags(){
        tags.clear();

    }


    /**
     * Method for get Id Device
     *
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method for get Push Token
     *
     */
    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    /**
     * Method for get User Id
     *
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Method for get Type of Device
     *
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    /**
     * Method for get Leanguaje of Device
     *
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    /**
     * Method for get Model od Device
     *
     */
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Method for get OS version of Device
     *
     */
    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Method for get Sdk version of Device
     *
     */
    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
    /**
     * Method for get Tags of Device
     *
     */
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    /**
     * Method for get JSON body for make Update Device
     * @param context : instance context
     * @param pushToken : push token parameter
     */

    private JSONObject requestJsonBodyForUpdate(String pushToken,Context context){

        JSONObject requestBody=new JSONObject();
        JSONArray jsonArray=new JSONArray(tags);

        try {
            if(!pushToken.equals("")) {
                requestBody.put("pushToken", pushToken);
                requestBody.put("type", type);
                requestBody.put("tags", jsonArray);
                requestBody.put("language", language);
                requestBody.put("model", model);
                requestBody.put("os", os);
                requestBody.put("sdkVersion", sdkVersion);
            }else{
                requestBody.put("pushToken", pushToken);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestBody;
    }

    /**
     * Method that send Parameter (Ej: messangiDev or MessangiUserDevice) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {
        Messangi messangi=Messangi.getInst(context);
        Intent intent=new Intent("PassDataFromoSdk");
        messangi.utils.showInfoLog(this,"Broadcasting message");
        intent.putExtra("message",something);
        if(something!=null){
         LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
         messangi.utils.showErrorLog(this,"Not Send Broadcast ");
        }
    }

}
