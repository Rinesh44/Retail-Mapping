package com.example.android.retailmapping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AfterImage extends AppCompatActivity {
Toolbar toolbar;
Button capture, add;
ImageView image;
static String encodedImage;
public static String post_url = "http://pagodalabs.com.np/retailmapping/field_detail/api/after_visit";
private final int requestCodeForAfterImage = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_image);

        Window window = AfterImage.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(AfterImage.this.getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Set After Image");

        capture = (Button) findViewById(R.id.capture);
        add = (Button) findViewById(R.id.add);

        image = (ImageView) findViewById(R.id.imageView);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureAfterImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureAfterImage,requestCodeForAfterImage);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())){

                    Intent i = getIntent();

                    String field_id_value = i.getStringExtra("fieldenquiry_id");
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("fieldenquiry_id", field_id_value);
                    params.put("after_visit", encodedImage);


                    JSONObject parameter = new JSONObject(params);
                    Log.e("JSON:", parameter.toString());

                    OkHttpClient client = new OkHttpClient();
                    okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url(post_url)
                            .post(body)
                            .addHeader("content-type", "application/json; charset=utf-8")
                            .build();

                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.e("response", call.request().body().toString());
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String responseString = response.body().string();
                                Log.e("AfterImageResponse", responseString);
                            }
                        }

                    });

                    Toast.makeText(AfterImage.this, "Image added", Toast.LENGTH_SHORT).show();
                    finish();
                }else
                    Toast.makeText(AfterImage.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if (requestCode == requestCodeForAfterImage) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return false;
    }
}
