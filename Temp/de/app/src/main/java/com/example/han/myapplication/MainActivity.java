package com.example.han.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<ImageView> musicImage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicImage.add((ImageView)findViewById(R.id.lp6));
        musicImage.add((ImageView)findViewById(R.id.lp5));
        musicImage.add((ImageView)findViewById(R.id.lp4));
        musicImage.add((ImageView)findViewById(R.id.lp3));
        musicImage.add((ImageView)findViewById(R.id.lp2));
        musicImage.add((ImageView)findViewById(R.id.lp1));


        musicImage.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicImage.get(0).setImageResource(R.mipmap.ic_ab2);

            }
        });

    }
    private void loadImages() {
        (new DownloadImageTask(musicImage.get(0))).execute("https://i2.wp.com/beebom.com/wp-content/uploads/2016/01/Reverse-Image-Search-Engines-Apps-And-Its-Uses-2016.jpg");
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {


            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Log.i("TEST", urls[0]);
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            Canvas canvas = new Canvas(mIcon11);
            Paint paint=new Paint();
            Rect rect=new Rect(0,0,mIcon11.getWidth(),mIcon11.getHeight());
            paint.setAntiAlias(true);
            canvas.drawCircle(mIcon11.getWidth()/2,mIcon11.getHeight()/2,mIcon11.getWidth()/2,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(mIcon11,rect,rect,paint);
            return mIcon11 ;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}


