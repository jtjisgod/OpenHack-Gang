package com.example.han.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        musicImage.add((ImageView)findViewById(R.id.lp1));
        musicImage.add((ImageView)findViewById(R.id.lp2));
        musicImage.add((ImageView)findViewById(R.id.lp3));
        musicImage.add((ImageView)findViewById(R.id.lp4));
        musicImage.add((ImageView)findViewById(R.id.lp5));
        musicImage.add((ImageView)findViewById(R.id.lp6));


        musicImage.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImages();
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
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}


