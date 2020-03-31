package com.android.testdefsdknotificactionpush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.MessangiDev;
import com.ogangi.messangi.sdk.MessangiNotification;
import com.ogangi.messangi.sdk.MessangiUserDevice;

import java.util.ArrayList;

public class ListNotification extends AppCompatActivity {
    public static String CLASS_TAG=ListNotification.class.getSimpleName();
    public static String TAG="MessangiSDK";

    public Messangi messangi;
    public Button back,clear;
    public ListView list_notification;

    public ArrayList<MessangiNotification> messangiNotificationArrayList;
    public ListAdapter messangiNotificationAdapter;
    public ProgressBar progressBar;

    MessangiNotification messangiNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_notification);

        messangi=Messangi.getInst();
        list_notification = findViewById(R.id.list_notification_push);
        back=findViewById(R.id.button_back);
        clear=findViewById(R.id.button_clear);
        messangiNotificationArrayList = messangi.getMessangiNotifications();
        if(messangiNotificationArrayList.size()>0) {
            messangiNotificationAdapter = new ListAdapter(getApplicationContext(),messangiNotificationArrayList);
            list_notification.setAdapter(messangiNotificationAdapter);
        }else{
            list_notification.setVisibility(View.GONE);
            TextView provImp=findViewById(R.id.textView_noti);
            provImp.setText("Hasn't  Notification");
        }

        list_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,CLASS_TAG+":Notification selected: "+messangiNotificationArrayList.get(position).getTitle());
                showAlertNotificaction(messangiNotificationArrayList.get(position));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messangiNotificationArrayList.clear();
                finish();
                startActivity(getIntent());

            }
        });


    }

    private void showAlertNotificaction(MessangiNotification messangiNotification) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
        builder.setView(customLayout);
        TextView title=customLayout.findViewById(R.id.title_noti);
        TextView body=customLayout.findViewById(R.id.body_noti);
        TextView data=customLayout.findViewById(R.id.data_noti);
        title.setText(""+ messangiNotification.getTitle());
        body.setText(""+ messangiNotification.getBody());
        if(messangiNotification.getData().size()>0){
            data.setText("data: "+ messangiNotification.getData());
        }else{
            data.setText("Hasn't data");
        }



        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
