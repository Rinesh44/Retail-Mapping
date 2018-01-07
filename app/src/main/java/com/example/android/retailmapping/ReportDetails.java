package com.example.android.retailmapping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportDetails extends AppCompatActivity {
    TextView particulars, brand, height, width, qty, username;
    ImageView beforeVisit, afterVisit;
    Toolbar toolbar;
    String valueFieldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Report Details");

        particulars = (TextView) findViewById(R.id.textParticulars);
        height = (TextView) findViewById(R.id.textHeight);
        width = (TextView) findViewById(R.id.textWidth);
        qty = (TextView) findViewById(R.id.textQty);
        brand = (TextView) findViewById(R.id.textBrand);
        //username = (TextView) findViewById(R.id.textCreatedBy);

        beforeVisit = (ImageView) findViewById(R.id.imageBeforeVisit);
        afterVisit = (ImageView) findViewById(R.id.imageAfterVisit);

        Intent i = getIntent();

        String valueParticulars = i.getStringExtra("particulars");
        particulars.setText(valueParticulars);

        String valueHeight = i.getStringExtra("height");
        height.setText(valueHeight);

        String valueWidth = i.getStringExtra("width");
        width.setText(valueWidth);

        String valueQty = i.getStringExtra("qty");
        qty.setText(valueQty);

       // String valueUsername = i.getStringExtra("username");
        //username.setText(valueUsername);

        String valueBrand = i.getStringExtra("brand");
        brand.setText(valueBrand);

        valueFieldId = i.getStringExtra("fieldenquiry_id");

        String img1 = i.getStringExtra("before_visit");

        byte[] decodedString = Base64.decode(img1, Base64.DEFAULT);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if (bitmap != null) {
            beforeVisit.setImageBitmap(bitmap);
        } else
            beforeVisit.setVisibility(View.GONE);


        String img2 = i.getStringExtra("after_visit");

        byte[] decodedString1 = Base64.decode(img2, Base64.DEFAULT);
        final Bitmap bitmap1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
        if (bitmap1 != null) {
            afterVisit.setImageBitmap(bitmap1);
        } else
            afterVisit.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return false;
    }

}
