package com.example.han.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;

public class mainPlay extends FragmentActivity {

    final static int PERMISSION_REQUEST_CODE = 1;

    boolean toggle = true;

    private Context context;
    private Activity activity;
    private GangNetwork gangNetwork;
    private ImageView bgImage;
    private TextView artist;
    private TextView musicTitle;
    private HashMap<String, String> music;
    private boolean isPlaying = false;
    public MediaPlayer mediaPlayer;

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
        setContentView(R.layout.activity_main_play);
        context = getApplicationContext();
        activity = mainPlay.this;
        gangNetwork = new GangNetwork(getPhone());
        musicTitle = (TextView)findViewById(R.id.title);
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

    private void nextMusic() {
        music = gangNetwork.getMusic();
        bgImage = (ImageView)findViewById(R.id.background_image);
        if(music.get("title").length() > 25) {
            musicTitle.setText(music.get("title").substring(0, 25) + "...");
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
