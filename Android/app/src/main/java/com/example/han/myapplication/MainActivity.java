package com.example.han.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private LinearLayout mRootLayout;
    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();
        mActivity=MainActivity.this;

        mRootLayout=findViewById(R.id.root_layout);

        // URL 설정.
//        String url="http://bughunting.net:5000/mainPlayList";
        String url="http://bughunting.net:5000/crawlTag";
        String responseText="";

        // AsyncTask를 통해 HttpURLConnection 수행.

        NetworkTask networkTask=new NetworkTask();
        try {
            responseText=networkTask.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        TelephonyManager im_num=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String tel_num=im_num.getDeviceId();
            Toast.makeText(this,tel_num,Toast.LENGTH_SHORT).show();
            String[] music_infor=responseText.split(",");
            try {
                JSONArray category = new JSONArray(responseText);

                String music_url="http://api.soundcloud.com/tracks/" + category.get(4) + "/stream?client_id=unnFdubicpq7RVFFsQucZzduDPQTaCYy";
                play(music_url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }



        //new SendPost().execute();
/*
        Toast.makeText(this,"11",Toast.LENGTH_SHORT).show();

        TelephonyManager im_num=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"33",Toast.LENGTH_SHORT).show();
            return;
        }
        String tel_num=im_num.getDeviceId();
        Toast.makeText(this,tel_num,Toast.LENGTH_SHORT).show();

        responseText=responseText.replaceAll(" ", "");
        String[] music_infor=responseText.split(",");
        String music_url="http://api.soundcloud.com/tracks/" + music_infor[4] + "/stream?client_id=unnFdubicpq7RVFFsQucZzduDPQTaCYy";
        play(music_url);



    }

    public void play(String Murl){
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(Murl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private class SendPost extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... unused) {
            String content=executeClient();
            return content;
        }

        private String executeClient() {
            ArrayList<NameValuePair> post=new ArrayList<NameValuePair>();
            post.add(new BasicNameValuePair("id", "leejay"));
            post.add(new BasicNameValuePair("pw", "1234"));

            // 연결 HttpClient 객체 생성
            HttpClient client = new DefaultHttpClient();

            // 객체 연결 설정 부분, 연결 최대시간 등등
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            // Post객체 생성
            HttpPost httpPost = new HttpPost("http://bughunting.net:5000");

            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                httpPost.setEntity(entity);
                client.execute(httpPost);
                return EntityUtils.getContentCharSet(entity);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class NetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strUrl) {
            StringBuffer buffer=new StringBuffer();
            URL url=null;
            try {
                url=new URL(strUrl[0]);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp=new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader=new BufferedReader(tmp);
                    String str="";
                    String receiveMsg="";
                    while ((str=reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg=buffer.toString();
                    Log.i("receiveMsg : ", receiveMsg);
                    reader.close();
                } else {
                    Log.i("ERROR", "ERROR");
                }

            } catch (Exception e) {
            }

            return buffer.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}