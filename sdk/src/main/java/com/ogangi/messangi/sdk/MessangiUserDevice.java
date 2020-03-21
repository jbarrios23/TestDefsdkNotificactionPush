package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * class MessangiDev is used for handle Device paramenter in SDK and service
 */
public class MessangiUserDevice implements Serializable {


    private  ArrayList<MessangiDev> devices;

    @SerializedName("properties")
    @Expose
    private  final Map<String, String> properties = new HashMap<>();

    protected String id;
    /**
     * Method that make Update of User using the service Put
     @param context
     @serialData :MessangiUserDevice
     */
    public void save(final Context context){

        final Messangi messangi=Messangi.getInst(context);
        final StorageController storageController=Messangi.getInst().storageController;
        EndPoint endPoint= ApiUtils.getSendMessageFCM(context);
        String deviceId=messangi.messangiDev.getId();
        messangi.utils.showErrorLog(this,deviceId);
        JsonObject gsonObject;
        Map<String, String> provPro=properties;
        JSONObject requestUpdatebody=new JSONObject(provPro);
        messangi.utils.showDebugLog(this,"requestUpdatebody "+requestUpdatebody.toString());
        JsonParser jsonParser=new JsonParser();
        gsonObject=(JsonObject) jsonParser.parse(requestUpdatebody.toString());
        endPoint.putUserByDeviceParameter(deviceId,gsonObject).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.isSuccessful()){
                        JsonObject jsonObject=response.body();
                        JsonObject data=jsonObject.getAsJsonObject("subscriber").getAsJsonObject("data");
                        messangi.utils.showDebugLog(this,"User update successful "+jsonObject);
                        messangi.utils.showDebugLog(this,"data "+data);

                        Gson gson = new Gson();
                        //Mapping data for convert to MessangiUaserDevice
                        Map<String, String> retMap = gson.fromJson(
                                data, new TypeToken<HashMap<String, String>>() {}.getType()
                        );
                        MessangiUserDevice messangiUserDevice=parseData(retMap);
                        storageController.saveUserByDevice(messangiUserDevice);
                        sendEventToActivity(messangiUserDevice,context);

                    }else{
                        int code=response.code();
                        messangi.utils.showErrorLog(this,"Code Update user error "+code);
                        sendEventToActivity(null,context);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    sendEventToActivity(null,context);
                    messangi.utils.showErrorLog(this,"onFailure "+t.getMessage());

                }
            });


    }

    public String getId() {

        return id;
    }
    /**
     * Method for add properties to user
     */
    public void addProperties(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Method for get Properties of user
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Method for get Device of user
     */
    public ArrayList<MessangiDev> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<MessangiDev> devices) {
        this.devices = devices;
    }

    /**
     * Method that send Parameter (Ej: messangiDev or MessangiUserDevice) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {
        Messangi messangi=Messangi.getInst(context);
        Intent intent=new Intent("PassDataFromoSdk");
        messangi.utils.showDebugLog(this,"Broadcasting message");
        intent.putExtra("message",something);
        if(something!=null){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            messangi.utils.showErrorLog(this,"Dont Send Broadcast ");
        }
    }

    /**
     * Method for parse data and conver to MessangiUserDevice Object
     @param retMap: HasMap Object to convert

     */

    public static MessangiUserDevice parseData(Map<String, String> retMap){

        MessangiUserDevice messangiUserDevice = new MessangiUserDevice();

        for (Map.Entry<String, String> entry : retMap.entrySet()) {


            if (entry.getKey().equals("devices")) {
                Gson gson=new Gson();
                //get Array list of devices from retMap
                ArrayList<MessangiDev> devices = gson.fromJson(
                        (String) retMap.get("devices"), new TypeToken<ArrayList<MessangiDev>>() {}.getType()
                );
                messangiUserDevice.setDevices(devices);
            } else {
                messangiUserDevice.addProperties(entry.getKey(), entry.getValue());
            }

        }

        return messangiUserDevice;

    }
    /**
     * Method for get Email user
     */

    public String getEmail(){


        if(properties.containsKey("email")){
            return (String) properties.get("email");
        }

        return "";
    }

    /**
     * Method for get Phone user
     */
    public String getPhone(){

        if(properties.containsKey("phone")){
            return (String) properties.get("phone");
        }

        return "";
    }

    /**
     * Method for get External ID user
     */
    public String getExternalID(){


        if(properties.containsKey("externalID")){
            return (String) properties.get("externalID");
        }

        return "";
    }

    public void setEmail(String value){

        properties.put("email",value);

    }
    public void setPhone(String value){

        properties.put("phone",value);

    }

    public void setExternalID(String value){

    properties.put("externalID",value);

    }

    /**
     * Method for get some property of user
     */
    public String getProperty(String key){

        if(properties.containsKey(key)){
            return (String) properties.get(key);
        }

        return "";
    }


}
