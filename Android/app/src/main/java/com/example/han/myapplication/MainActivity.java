package com.example.han.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MainActivity extends FragmentActivity {

    final static int PERMISSION_REQUEST_CODE = 1;

    private Context context;
    private Activity activity;
    private LinearLayout mRootLayout;
    public MediaPlayer mediaPlayer;
    ArrayList<ImageView> musicImage = new ArrayList<>();
    LinkedList<HashMap<String, String>> musicQueue = new LinkedList<>();
    GangNetwork gangNetwork;
    CircleTransform transForm = new CircleTransform();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        activity = MainActivity.this;
        gangNetwork = new GangNetwork(getPhone());

        musicImage.add((ImageView)findViewById(R.id.lp1));
        musicImage.add((ImageView)findViewById(R.id.lp2));
        musicImage.add((ImageView)findViewById(R.id.lp3));
        musicImage.add((ImageView)findViewById(R.id.lp4));
        musicImage.add((ImageView)findViewById(R.id.lp5));
        musicImage.add((ImageView)findViewById(R.id.lp6));

        musicQueueInit();
        showImages();
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        musicImage.get(0).setAnimation(animation);

//        gangNetwork.like(200);
//        gangNetwork.like(123);
//        gangNetwork.like(222);
//        ArrayList<HashMap<String, String>> maps = gangNetwork.likeList();
//        Log.i("MAP SIZE", "" + maps.size());
//        for(int i=0;i<maps.size();i++) {
//            Log.i("TEST", maps.get(i).get("mp3Url"));
//        }
//        String url = gangNetwork.getMusic().get("mp3Url");
//        Log.i("TEST", url);
//        play(url);

//        musicImage.get(0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadImages();
//            }
//        });

    }

    private void musicQueueInit() {
        for(int i=0;i<6;i++) {
            musicQueue.add(gangNetwork.getMusic());
        }
    }

    private void showImages() {

        for(int i=0;i<6;i++) {
            HashMap<String, String> tmp = musicQueue.poll();
            musicQueue.add(tmp);
            String image = "";
            try {
                image = tmp.get("bgImage");
            } catch(Exception e) {
                image = "";
            }
            Picasso.get().load(image).transform(transForm).into(musicImage.get(i));
        }
    }

    private String getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return phoneMgr.getLine1Number().split("\\+")[1];
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission)){
            Toast.makeText(context, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this.activity, new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    public void play(String Murl){
        Log.i("URL", Murl);
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


    public class CircleTransform implements Transformation {
        private final int BORDER_COLOR = Color.WHITE;
        private final int BORDER_RADIUS = 5;

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;

            // Prepare the background
            Paint paintBg = new Paint();
            paintBg.setColor(BORDER_COLOR);
            paintBg.setAntiAlias(true);

            // Draw the background circle
            canvas.drawCircle(r, r, r, paintBg);

            // Draw the image smaller than the background so a little border will be seen
            canvas.drawCircle(r, r, r - BORDER_RADIUS, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}