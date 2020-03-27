package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ogangi.messangi.sdk.MessangiDev.toMap;

/**
 * class MessangiDev is used for handle Device paramenter in SDK and service
 */
public class MessangiUserDevice implements Serializable {


    private  ArrayList<MessangiDev> devices;
    //private  ArrayList<String> devices;

//    @SerializedName("properties")
//    @Expose
    private  final Map<String, String> properties = new HashMap<>();

    protected String id;

    public MessangiUserDevice() {
        this.devices = new ArrayList<>();
        this.id="";
    }

    /**
     * Method that make Update of User using the service Put
     @param context
     @serialData :MessangiUserDevice
     */
    public void save(final Context context){

        final Messangi messangi=Messangi.getInst(context);
        final MessangiStorageController messangiStorageController =Messangi.getInst().messangiStorageController;

        String deviceId=messangi.messangiDev.getId();
        messangi.utils.showErrorLog(this,deviceId);
        //JsonObject gsonObject;
        Map<String, String> provPro=properties;
        JSONObject requestUpdatebody=new JSONObject(provPro);
        messangi.utils.showDebugLog(this,"requestUpdatebodyUser "+requestUpdatebody.toString());
      // JsonParser jsonParser=new JsonParser();
       // gsonObject=(JsonObject) jsonParser.parse(requestUpdatebody.toString());
//        endPoint.putUserByDeviceParameter(deviceId,gsonObject).enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    if(response.isSuccessful()){
//                        JsonObject jsonObject=response.body();
//                        JsonObject data=jsonObject.getAsJsonObject("subscriber").getAsJsonObject("data");
//                        messangi.utils.showDebugLog(this,"User update successful "+jsonObject);
//                        messangi.utils.showDebugLog(this,"data "+data);
//
//                        Gson gson = new Gson();
//                        //Mapping data for convert to MessangiUaserDevice
//                        Map<String, String> retMap = gson.fromJson(
//                                data, new TypeToken<HashMap<String, String>>() {}.getType()
//                        );
//                        MessangiUserDevice messangiUserDevice=parseData(retMap);
//                        messangiStorageController.saveUserByDevice(messangiUserDevice);
//                        sendEventToActivity(messangiUserDevice,context);
//
//                    }else{
//                        int code=response.code();
//                        messangi.utils.showErrorLog(this,"Code Update user error "+code);
//                        sendEventToActivity(null,context);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    sendEventToActivity(null,context);
//                    messangi.utils.showErrorLog(this,"onFailure "+t.getMessage());
//
//                }
//            });

                new HTTPReqTaskPutUserByDevice(deviceId,requestUpdatebody,context,messangi).execute();

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
                try {
                    Messangi messangi=Messangi.getInst();
                    JSONArray jsonArrayDevice=new JSONArray(retMap.get("devices"));
                    for(int i=0;i<jsonArrayDevice.length();i++){
                        JSONObject provDevice=jsonArrayDevice.getJSONObject(i);
                        MessangiDev messangiDev=messangi.utils.getMessangiDevFromJson(provDevice);
                        messangiUserDevice.devices.add(messangiDev);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


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


    private class HTTPReqTaskPutUserByDevice extends AsyncTask<Void,Void,String> {

        private String deviceId;
        private JSONObject jsonObject;
        private Context context;
        private String server_response;
        private Messangi messangi;


        public HTTPReqTaskPutUserByDevice(String deviceId, JSONObject gsonObject, Context context, Messangi messangi) {

            this.deviceId=deviceId;
            this.jsonObject=gsonObject;
            this.context=context;
            this.messangi=messangi;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {

                String authToken= MessangiSdkUtils.getMessangi_token();
                JSONObject postData = jsonObject;
                messangi.utils.showErrorLog(this,"JSON data for update "+postData.toString());
                String provUrl= MessangiSdkUtils.getMessangi_host()+"/v1/users?device="+deviceId;
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
                    messangi.utils.showErrorLog(this,"update data user good"+ server_response);
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
                    messangi.utils.showErrorLog(this,"Json update user "+resp.toString());
                    JSONObject data=resp.getJSONObject("subscriber").getJSONObject("data");
                    messangi.utils.showErrorLog(this,"Json update user "+data.toString());
                    Map<String, String> resultMap=toMap(data);
                    messangi.messangiStorageController.saveUserByDeviceOneByOne(resultMap);
                    MessangiUserDevice messangiUserDevice;
                    messangiUserDevice = MessangiUserDevice.parseData(resultMap);
                    sendEventToActivity(messangiUserDevice, context);

                    messangi.utils.showInfoLog(this,"Request update user device successful");

                }
            }catch (NullPointerException e){
                messangi.utils.showErrorLog(this,"User not update!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
