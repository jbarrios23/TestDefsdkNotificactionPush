package com.android.testdefsdknotificactionpush;



import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogangi.messangi.sdk.MessangiNotification;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    public static String CLASS_TAG=ListAdapter.class.getSimpleName();
    public static String TAG="MessangiSDK";

    public Context context;
    public List<MessangiNotification> messangiNotificationList;
    public LayoutInflater inflater;
    public ListAdapter(Context context, ArrayList<MessangiNotification> messangiNotificationArrayList) {
        this.context=context;
        inflater= LayoutInflater.from(this.context);
        this.messangiNotificationList=messangiNotificationArrayList;
    }

    public class ViewHolder {

        TextView data;
        TextView date;
    }

    @Override
    public int getCount() {
        return messangiNotificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return messangiNotificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView== null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_notification, null);

            holder.data =  convertView.findViewById(R.id.Texview_value);
            holder.date =  convertView.findViewById(R.id.texview_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data.setText(""+messangiNotificationList.get(position).getTitle()+"\n"
                +messangiNotificationList.get(position).getBody());

        holder.date.setText(""+messangiNotificationList.get(position).getCurrentDate()+"\n"
                +messangiNotificationList.get(position).getCurrentTime());


        return convertView;
    }
}
