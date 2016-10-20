package com.example.firewaves.chatapp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by atnm1 on 28/07/16.
 */
public class SendNotificationTask extends AsyncTask<Void, Void, Void> {

    public String topic;
    public String myTitle;
    public String myBody;
    public String myTag;


    public SendNotificationTask(String _title, String _body, String _tag, String _topic){
        myTitle = _title;
        myBody = _body;
        myTag = _tag;
        topic = _topic;
    }

    @Override
    protected Void doInBackground(Void...params) {
        // Firebase cloud messaging

        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpsURLConnection client = (HttpsURLConnection) url.openConnection();
            client.setDoInput(true);
            client.setDoOutput(true);
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-type", "application/json");
            client.setRequestProperty("Authorization", "key=AIzaSyAtLaAIS2KdfqtnBJAAKs0vh1yBzpgg0z8");
            client.connect();


            JSONObject notification = new JSONObject();
            notification.put("title", myTitle);
            notification.put("body", myBody);
            notification.put("sound", "default");
            notification.put("tag", myTag);

            JSONObject json = new JSONObject();
            json.put("to", "/topics" + topic);
            json.put("notification", notification);

            DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
            outputStream.writeBytes(json.toString());
            outputStream.flush();
            outputStream.close();


            // 1 0 0
            // 1 0 0
            // 0 0 1

            System.out.println(client.getResponseMessage());

        } catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }
}













