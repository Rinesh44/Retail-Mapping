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

public class ShopDetails extends AppCompatActivity {
    TextView shopName, ownerName, address, contactNumber, district, createdBy, brand;
    Toolbar toolbar;
    String valueShopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Shop Details");

        shopName = (TextView) findViewById(R.id.textShopName);
        ownerName = (TextView) findViewById(R.id.textOwnerName);
        address = (TextView) findViewById(R.id.textAddress);
        contactNumber = (TextView) findViewById(R.id.textContact);
        district = (TextView) findViewById(R.id.textDistrict);
        createdBy = (TextView) findViewById(R.id.textCreatedBy);
        brand = (TextView) findViewById(R.id.textBrand);

        Intent i = getIntent();

        String valueShop = i.getStringExtra("shop_name");
        shopName.setText(valueShop);

        String valueOwner = i.getStringExtra("owner_name");
        ownerName.setText(valueOwner);

        String valueAddress = i.getStringExtra("address");
        address.setText(valueAddress);

        String valueContactNo = i.getStringExtra("contact_no");
        contactNumber.setText(valueContactNo);

        String valueDistrict = i.getStringExtra("district");
        district.setText(valueDistrict);

        String valueCreatedBy = i.getStringExtra("created_by");
        createdBy.setText(valueCreatedBy);

        String valueBrand = i.getStringExtra("brand");
        brand.setText(valueBrand);

        valueShopId = i.getStringExtra("shop_id");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.in_shop:
                Intent enter_in_shop = new Intent(ShopDetails.this, InShop.class);
                enter_in_shop.putExtra("shop_id", valueShopId);
                startActivity(enter_in_shop);
                break;

            case R.id.on_shop:
                Intent enter_on_shop = new Intent(ShopDetails.this, OnShop.class);
                enter_on_shop.putExtra("shop_id", valueShopId);
                startActivity(enter_on_shop);
                break;

            case R.id.report:
                Intent enter_report = new Intent(ShopDetails.this, Report.class);
                enter_report.putExtra("shop_id", valueShopId);
                startActivity(enter_report);
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        return true;
    }
}
