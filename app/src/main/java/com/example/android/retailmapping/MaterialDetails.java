package com.example.android.retailmapping;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class MaterialDetails extends AppCompatActivity {
    TextView material, brand, height, width, order_by, qty, remarks, shop, design_new, design_old;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_details);

        Window window = MaterialDetails.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if(Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(MaterialDetails.this.getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Details");


        material = (TextView) findViewById(R.id.textMaterial);
        brand =  (TextView) findViewById(R.id.textBrand);
        height = (TextView) findViewById(R.id.textHeight);
        width = (TextView) findViewById(R.id.textWidth);
        order_by = (TextView) findViewById(R.id.textOrderBy);
        qty = (TextView) findViewById(R.id.textQty);
        remarks = (TextView) findViewById(R.id.textRemarks);
        shop = (TextView) findViewById(R.id.textShopName);
        design_new = (TextView) findViewById(R.id.textDesignNew);
        design_old = (TextView) findViewById(R.id.textDesignOld);

        Intent i = getIntent();

        String valueMaterial = i.getStringExtra("material");
        material.setText(valueMaterial);

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

        String valueDesignNew = i.getStringExtra("design_new");
        design_new.setText(valueDesignNew);

        String valueDesignOld = i.getStringExtra("design_old");
        design_old.setText(valueDesignOld);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
        }
        return false;
    }

}
