package com.example.han.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class GangNetwork {

    private String phone;

    public GangNetwork(String phone) {
        this.phone = phone;
    }

    private HashMap<String, String> convertMap(String playList) {
        try {
            return convertMap(new JSONArray(playList));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private HashMap<String, String> convertMap(JSONArray jsonArray) {
        HashMap<String, String> map = new HashMap<>();
        try {
            map.put("idx", jsonArray.getString(0));
            map.put("genre", jsonArray.getString(1));
            map.put("artist", jsonArray.getString(2));
            map.put("playback", jsonArray.getString(3));
            map.put("track", jsonArray.getString(4));
            map.put("bgImage", jsonArray.getString(5));
            map.put("title", jsonArray.getString(6));
            map.put("mp3Url", "http://api.soundcloud.com/tracks/" + map.get("track") + "/stream?client_id=unnFdubicpq7RVFFsQucZzduDPQTaCYy");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    private String getJson(String url) {
        NetworkTask networkTask = new NetworkTask();
        try {
            return networkTask.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public HashMap<String, String> getMusic() {
        return getMusic("main");
    }

    // return Hashmap of a music
    public HashMap<String, String> getMusic(String tag) {
        String json = "";
        if("main".equals(tag)) {
            json = getJson("http://bughunting.net:5000/mainPlayList");
        } else {
            json = getJson("http://bughunting.net:5000/shuffle/" + tag);
        }
        return convertMap(json);
    }

    // Return Tags (Category)
    public String[] getTags() {
        String url = "http://bughunting.net:5000/crawlTag";
        String[] str = null;
        try {
            JSONArray jsonArray = new JSONArray(getJson(url));
            str = new String[jsonArray.length()];
            for(int i=0;i<jsonArray.length();i++) {
                str[i] = jsonArray.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    // Like & Dislike Toggler
    // return true = LIKE / false = DISLIKE
    public boolean like(int idx) {
        String response = getJson("http://bughunting.net:5000/like/" + idx + "?imei=" + this.phone);
        if(response == "like") {
            return true;
        }
        return false;
    }

    public ArrayList<HashMap<String, String>> likeList() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        String url = "http://bughunting.net:5000/likeList?imei=" + this.phone;
        try {
            JSONArray jsonArray = new JSONArray(getJson(url));
            for(int i=0;i<jsonArray.length();i++) {
                HashMap<String, String> hashMap = convertMap(jsonArray.getJSONArray(i));
                list.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
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
                e.printStackTrace();
            }

            return buffer.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
