package com.android.testdefsdknotificactionpush;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.MessangiDev;
import com.ogangi.messangi.sdk.MessangiUserDevice;
import com.ogangi.messangi.sdk.SdkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public static String TAG="MessangiSDK";

    public Messangi messangi;
    public Button device,user,tags,save;
    public TextView imprime;
    public MessangiDev messangiDev;
    public MessangiUserDevice messangiUserDevice;
    public ListView lista_device,lista_user;

    public ArrayList<String> messangiDevArrayList;
    public ArrayAdapter<String> messangiDevArrayAdapter;
    public ArrayList<String> messangiUserDeviceArrayList;
    public ArrayAdapter<String> messangiUserDeviceArrayAdapter;
    public ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        lista_device=findViewById(R.id.lista_device);
        lista_user=findViewById(R.id.lista_user);

        device=findViewById(R.id.device);
        user=findViewById(R.id.user);
        tags=findViewById(R.id.tag);
        save=findViewById(R.id.save);
        progressBar=findViewById(R.id.progressBar);
        Switch simpleSwitch = findViewById(R.id.simpleSwitch);

        messangi=Messangi.getInst(this);
        messangiDevArrayList=new ArrayList<>();
        messangiUserDeviceArrayList=new ArrayList<>();
        messangiDevArrayAdapter=new ArrayAdapter<>(this,R.layout.item_device,R.id.Texview_value,messangiDevArrayList);
        messangiUserDeviceArrayAdapter=new ArrayAdapter<>(this,R.layout.item_device,R.id.Texview_value,messangiUserDeviceArrayList);


        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messangiDevArrayList.clear();
                messangiUserDeviceArrayList.clear();
                progressBar.setVisibility(View.VISIBLE);
                messangi.requestDevice(true);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertUser();
            }
        });

        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatAlert();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messangiDev.getTags().size()>0) {
                    progressBar.setVisibility(View.VISIBLE);
                    messangiDev.save(getApplicationContext());
                }else{
                    Toast.makeText(getApplicationContext(),"Nothing to save",Toast.LENGTH_LONG).show();
                }
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    Toast.makeText(getApplicationContext(),"Enable Notification Push",Toast.LENGTH_LONG).show();
                    messangiDev.setStatusNotificationPush(isChecked,getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                }else{

                    Toast.makeText(getApplicationContext(),"Disable Notification Push",Toast.LENGTH_LONG).show();
                    messangiDev.setStatusNotificationPush(isChecked,getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,CLASS_TAG+": register BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("PassDataFromoSdk"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG,CLASS_TAG+": onResume");
        messangiDevArrayList.clear();
        messangiUserDeviceArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        messangi.requestDevice(false);
    }

    private void createAlertUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout_user, null);
        builder.setView(customLayout);


        // add a button
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                EditText editText_key = customLayout.findViewById(R.id.editText_key);
                String key=editText_key.getText().toString();
                EditText editText_value = customLayout.findViewById(R.id.editText_value);
                String value=editText_value.getText().toString();
                messangiUserDevice.addProperties(key,value);
                createAlertUser();


            }
        });

        builder.setNegativeButton("Close And Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sendDialogDataToUser();

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();



    }

    private void sendDialogDataToUser() {

        Log.i(TAG,CLASS_TAG+": For update"+messangiUserDevice.getProperties());
        progressBar.setVisibility(View.VISIBLE);
        messangiUserDevice.save(getApplicationContext());

    }

    private void creatAlert() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        TextView vista=customLayout.findViewById(R.id.tag_selection);
        TextView clear=customLayout.findViewById(R.id.tag_clear);
        vista.setText("Select: "+messangiDev.getTags());
        clear.setText("Clear");
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messangiDev.clearTags();
                creatAlert();
                return false;
            }


        });
        // add a button
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                EditText editText = customLayout.findViewById(R.id.editText_tag);
                String tags=editText.getText().toString();
                messangiDev.addTagsToDevice(tags);
                creatAlert();



            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sendDialogDataToActivity();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void sendDialogDataToActivity() {

        Log.i(TAG,CLASS_TAG+": Tags selection final was "+messangiDev.getTags());
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Serializable message=intent.getSerializableExtra("message");
            SdkUtils sdkUtils=new SdkUtils();
            if ((message instanceof MessangiDev) && (message!=null)){
                messangiDevArrayList.clear();

                messangiDev=(MessangiDev) message;


                Log.i(TAG,CLASS_TAG+": Device:  "+sdkUtils.getGsonJsonFormat(messangiDev));
                messangiDevArrayList.add("Id: "           +messangiDev.getId());
                messangiDevArrayList.add("pushToken: "    +messangiDev.getPushToken());
                messangiDevArrayList.add("UserId: "       +messangiDev.getUserId());
                messangiDevArrayList.add("Type: "         +messangiDev.getType());
                messangiDevArrayList.add("Language: "     +messangiDev.getLanguage());
                messangiDevArrayList.add("Model: "        +messangiDev.getModel());
                messangiDevArrayList.add("Os: "           +messangiDev.getOs());
                messangiDevArrayList.add("SdkVersion: "   +messangiDev.getSdkVersion());
                messangiDevArrayList.add("Tags: "         +messangiDev.getTags());
                messangiDevArrayList.add("CreateAt: "     +messangiDev.getCreatedAt());
                messangiDevArrayList.add("UpdatedAt: "    +messangiDev.getUpdatedAt());
                messangiDevArrayList.add("Timestamp: "    +messangiDev.getTimestamp());
                messangiDevArrayList.add("Transaction: "  +messangiDev.getTransaction());


                lista_device.setAdapter(messangiDevArrayAdapter);
                messangiDev.requestUserByDevice(getApplicationContext(),false);


            }else if((message instanceof MessangiUserDevice) && (message!=null)){
                messangiUserDeviceArrayList.clear();
                messangiUserDevice=(MessangiUserDevice) message;
                Log.i(TAG,CLASS_TAG+" User:  "+sdkUtils.getGsonJsonFormat(messangiUserDevice));


                if(messangiUserDevice.getProperties().size()>0){
                    Map<String,String> result=messangiUserDevice.getProperties();
                    for (Map.Entry<String, String> entry : result.entrySet()) {

                        messangiUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
                    }


                }

                lista_user.setAdapter(messangiUserDeviceArrayAdapter);
            }else{
                Log.i(TAG,CLASS_TAG+": do nothing");
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }

            }
            if(progressBar.isShown()){
                progressBar.setVisibility(View.GONE);
            }

        }


    };

    @Override
    protected void onDestroy() {
        Log.i(TAG,CLASS_TAG+": unregister BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.e(CLASS_TAG,"PERMISSION_GRANTED");
//                    messangi.getPhone(activity);
//                } else {
//                    Toast.makeText(activity,"Permission Denied. ", Toast.LENGTH_LONG).show();
//
//                }
//                break;
//        }
//    }
}

