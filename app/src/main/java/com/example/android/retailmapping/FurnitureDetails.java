package com.example.android.retailmapping;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class FurnitureDetails extends AppCompatActivity {
    TextView furniture, brand, height, width, order_by, qty, remarks, shop;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_details);

        Window window = FurnitureDetails.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(FurnitureDetails.this.getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Details");

        furniture = (TextView) findViewById(R.id.textFurniture);
        brand = (TextView) findViewById(R.id.textBrand);
        height = (TextView) findViewById(R.id.textHeight);
        width = (TextView) findViewById(R.id.textWidth);
        order_by = (TextView) findViewById(R.id.textOrderBy);
        qty = (TextView) findViewById(R.id.textQty);
        remarks = (TextView) findViewById(R.id.textRemarks);
        shop = (TextView) findViewById(R.id.textShopName);

        Intent i = getIntent();

        String valueFurniture = i.getStringExtra("furniture");
        furniture.setText(valueFurniture);

        String valueBrand = i.getStringExtra("brand");
        brand.setText(valueBrand);

        String valueHeight = i.getStringExtra("height");
        height.setText(valueHeight);

        String valueWidth = i.getStringExtra("width");
        width.setText(valueWidth);

        String valueOrderBy = i.getStringExtra("username");
        order_by.setText(valueOrderBy);

        String valueQty = i.getStringExtra("qty");
        qty.setText(valueQty);

        String valueRemarks = i.getStringExtra("remarks");
        remarks.setText(valueRemarks);

        String valueShop = i.getStringExtra("shop_name");
        shop.setText(valueShop);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return false;
    }
}
