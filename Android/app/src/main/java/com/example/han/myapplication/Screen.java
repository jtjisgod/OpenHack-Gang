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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class Screen extends FragmentActivity {

    final int PERMISSION_REQUEST_CODE = 1;
    private static boolean toggle = true;
    private Context context;
    private Activity activity;
    private GangNetwork gangNetwork;
    private ImageView bgImage;
    private TextView artist;
    private TextView musicTitle;
    private HashMap<String, String> music;
    private boolean isPlaying = false;
    public static MediaPlayer mediaPlayer;

    class MyThread extends Thread {
        @Override
        public void run() {
            while(toggle) {
                ProgressBar progress = (ProgressBar)findViewById(R.id.progress);
                progress.setProgress(mediaPlayer.getCurrentPosition());
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new playerMain(), "First");
        viewPagerAdapter.addFragments(new playerDeleted(), "Two");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        context = getApplicationContext();
        gangNetwork = new GangNetwork(getPhone());
        musicTitle = (TextView)findViewById(R.id.artist);
        artist = (TextView)findViewById(R.id.artist);
        nextMusic();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            toggle = false;
            nextMusic();
            }
        });
    }

    /**
     * Created by aupadhyay on 1/5/17.
     */

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabTitles = new ArrayList<>();

        public void addFragments(Fragment fragment, String tabTitle)
        {
            this.fragments.add(fragment);
            this.tabTitles.add(tabTitle);
        }

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }

    }


    private void nextMusic() {
        music = gangNetwork.getMusic();
        bgImage = (ImageView)findViewById(R.id.background_image);
        if(music.get("title").length() > 25) {
            String title = music.get("title").substring(0, 25) + "...";
            musicTitle.setText(title);
        } else {
            musicTitle.setText(music.get("title"));
        }
        artist.setText("- " + music.get("artist"));
        Picasso.get().load(music.get("bgImage")).into(bgImage);
        play(music.get("mp3Url"));
        toggle = true;
        ProgressBar progress = (ProgressBar)findViewById(R.id.progress);
        progress.setMax(mediaPlayer.getDuration());
        progress.setProgress(0);
        new MyThread().start();
    }

    private String getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "01011111111";
        }
        return phoneMgr.getLine1Number().split("\\+")[1];
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission)){
            Toast.makeText(context, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this.activity, new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    public void ctrl(View v) {
        if(toggle == true) {
            toggle = false;
            ((ImageView)v).setImageResource(R.drawable.play);
            mediaPlayer.pause();
        } else {
            toggle = true;
            ((ImageView)v).setImageResource(R.drawable.stop);
            mediaPlayer.start();
            new MyThread().start();
        }
    }

    public void menu(View v) {
        ((ImageView)v).setVisibility(View.INVISIBLE);
        gangNetwork.like(Integer.parseInt(music.get("idx")));
    }

    public void like(View v) {
        ((ImageView)v).setVisibility(View.INVISIBLE);
        gangNetwork.like(Integer.parseInt(music.get("idx")));
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
//            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
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