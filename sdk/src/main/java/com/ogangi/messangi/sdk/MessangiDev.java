package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * class MessangiDev is used for handle Device paramenter in SDK and service
 */

public class MessangiDev implements Serializable {
    private String id;
    private String pushToken;
    protected String userId;
    private String type;
    private String language;
    private String model;
    private String os;
    private String sdkVersion;
    private List<String> tags = null;
    private String createdAt;
    private String updatedAt;
    private String timestamp;
    private String transaction;

    /**
     * Method that make Update of Device using the service Put
     @param context
     @serialData :MessangiDev
     */

    public void save(final Context context){

        final Messangi messangi=Messangi.getInst(context);
        // EndPoint endPoint= ApiUtils.getSendMessageFCM(context);
        messangi.utils.showInfoLog(this," Id "+id+" pushToken "+pushToken+" "+tags.toString());
        //JsonObject gsonObject = new JsonObject();
        JSONObject requestUpdatebody=requestJsonBodyForUpdate(pushToken);
        //JsonParser jsonParser=new JsonParser();
        //gsonObject=(JsonObject) jsonParser.parse(requestUpdatebody.toString());
//        endPoint.putDeviceParameter(id,gsonObject).enqueue(new Callback<MessangiDev>() {
//                @Override
//                public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {
//                    messangi.utils.showInfoLog(this,"Update Device good "+new Gson().toJson(response.body()));
//                    if(response.isSuccessful()){
//                        MessangiDev messangiDev=response.body();
//                        messangiStorageController.saveDevice(response.body());
//                        sendEventToActivity(messangiDev,context);
//                    }else{
//                        int code=response.code();
//                        messangi.utils.showErrorLog(this,"Code update error "+code);
//                        sendEventToActivity(null,context);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<MessangiDev> call, Throwable t) {
//                    messangi.utils.showErrorLog(this,"onFailure put service "+t.getMessage());
//                    sendEventToActivity(null,context);
//                }
//            });


        new HTTPReqTaskPut(id,requestUpdatebody,context).execute();
    }

    /**
     * Method for get Device registered from service
     @param context: instance context
     @param forsecallservice : allows effective device search in three ways: by instance, by shared variable or by service.
     */
    public void requestUserByDevice(final Context context, boolean forsecallservice){
        final Messangi messangi=Messangi.getInst(context);
        final MessangiStorageController messangiStorageController =Messangi.getInst().messangiStorageController;
        if(!forsecallservice && messangi.messangiUserDevice!=null){
            messangi.utils.showInfoLog(this,"User From RAM ");
            sendEventToActivity(messangi.messangiUserDevice,context);
        }else {
            if (!forsecallservice && messangiStorageController.isRegisterUserByDevice()) {
                Map<String, String> resultMap=messangiStorageController.getUserByDeviceOneByOne();
                messangi.messangiUserDevice=MessangiUserDevice.parseData(resultMap) ;
                sendEventToActivity(messangi.messangiUserDevice,context);
                messangi.utils.showInfoLog(this,"User From Local storage ");
            } else {

                    messangi.utils.showInfoLog(this, "User From Service ");
//                    EndPoint endPoint = ApiUtils.getSendMessageFCM(context);
//                    endPoint.getUserByDevice(id).enqueue(new Callback<Map<String, String>>() {
//                        @Override
//                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
//                            if (response.isSuccessful()) {
//
//                                Map<String, String> responseBody = response.body();
//                                MessangiUserDevice messangiUserDevice;
//                                messangiUserDevice = MessangiUserDevice.parseData(responseBody);
//                                messangiUserDevice.id = userId;
//                                messangiStorageController.saveUserByDevice(messangiUserDevice);
//                                sendEventToActivity(messangiUserDevice, context);
//                                messangi.utils.showInfoLog(this,"Request user device successful");
//
//                            } else {
//
//                                int code = response.code();
//                                messangi.utils.showErrorLog(this, "code getUser by device " + code);
//                                sendEventToActivity(null, context);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
//                            messangi.utils.showErrorLog(this, "onFailure " + t.getMessage());
//                            sendEventToActivity(null, context);
//                        }
//                    });

                new HTTPReqTaskGetUser(id,messangi,context).execute();
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
        final MessangiStorageController messangiStorageController =Messangi.getInst().messangiStorageController;
        messangiStorageController.setNotificationManually(true);
        if(messangiStorageController.hasTokenRegiter()&& enable){
            pushToken= messangiStorageController.getToken();

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
     * * @param pushToken : push token parameter
     */

    private JSONObject requestJsonBodyForUpdate(String pushToken){

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
        Intent intent=new Intent("PassDataFromSdk");
        messangi.utils.showInfoLog(this,"Broadcasting message");
        intent.putExtra("message",something);

        if(something!=null){
         LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
         messangi.utils.showErrorLog(this,"Not Send Broadcast ");
        }
    }

    private class HTTPReqTaskPut extends AsyncTask<Void,Void,String> {

        private JSONObject jsonObject;
        private String Id;
        private Messangi messangi;
        private String server_response;
        private Context context;
        private MessangiDev messangiDev;

        public HTTPReqTaskPut(String id, JSONObject gsonObject, Context context) {
            this.jsonObject=gsonObject;
            this.Id=id;
            this.context=context;
            this.messangi=Messangi.getInst(this.context);

        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessangiSdkUtils.getMessangi_token();
                JSONObject postData = jsonObject;
                messangi.utils.showErrorLog(this,"JSON data for update "+postData.toString());
                String provUrl= MessangiSdkUtils.getMessangi_host()+"/v1/devices/"+Id;
                messangi.utils.showErrorLog(this,"Url "+provUrl);
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    sendEventToActivity(null,context);

                    throw new IOException("Invalid response from server: " + code);
                }


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messangi.readStream(urlConnection.getInputStream());
                    messangi.utils.showErrorLog(this,"update data good"+ server_response);
                }


            } catch (Exception e) {
                e.printStackTrace();
                sendEventToActivity(null,context);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try{
                if(!response.equals("")) {
                    JSONObject resp=new JSONObject(response);
                    messangi.utils.showInfoLog(this, "response on put update device " + resp.toString());
                    messangiDev=messangi.utils.getMessangiDevFromJson(resp);
                    messangi.messangiStorageController.saveDeviceOneByOne(resp);
                    sendEventToActivity(messangiDev,context);

                }
            }catch (NullPointerException e){
                messangi.utils.showErrorLog(this,"device create!");
                sendEventToActivity(null,context);
            } catch (JSONException e) {
                e.printStackTrace();
                sendEventToActivity(null,context);
            }
        }
    }

    private class HTTPReqTaskGetUser extends AsyncTask<Void,Void,String> {

        public String deviceId;
        private String server_response;
        private Messangi messangi;
        private Context context;

        public HTTPReqTaskGetUser(String deviceId, Messangi messangi, Context context) {
            this.deviceId=deviceId;
            this.messangi=messangi;
            this.context=context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessangiSdkUtils.getMessangi_token();

                String param ="Bearer "+authToken;
                messangi.utils.showInfoLog(this,"Auth Token "+param);
                String provUrl= MessangiSdkUtils.getMessangi_host()+"/v1/users?device="+deviceId;
                messangi.utils.showErrorLog(this,"Url "+provUrl);
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messangi.readStream(urlConnection.getInputStream());
                    messangi.utils.showErrorLog(this,"response get User in"+ server_response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                messangi.utils.showErrorLog(this,"Service get User error "+e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            try{
                if(!response.equals("")) {
                    messangi.utils.showInfoLog(this, "response on Get User " + response);
                    JSONObject resp=new JSONObject(response);
                    messangi.utils.showErrorLog(this,"Json get user "+resp.toString());
                    Map<String, String> resultMap=toMap(resp);
                    messangi.messangiStorageController.saveUserByDeviceOneByOne(resultMap);
                    MessangiUserDevice messangiUserDevice;
                    messangiUserDevice = MessangiUserDevice.parseData(resultMap);
                    messangiUserDevice.id = userId;
                    //messangi.messangiStorageController.saveUserByDevice(messangiUserDevice);

                    sendEventToActivity(messangiUserDevice, context);
                    messangi.utils.showInfoLog(this,"Request user device successful");

                }
            }catch (NullPointerException e){
                messangi.utils.showErrorLog(this,"User not Get!");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public static Map<String, String> toMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            map.put(key, String.valueOf(value));
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @NonNull
    @Override
    public String toString() {
        return "device: "+id+" "+tags;
    }
}
