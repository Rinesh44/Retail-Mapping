package com.example.android.retailmapping;

import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.retailmapping.Interface.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddShop extends AppCompatActivity {
    private TextInputEditText shopName, ownerName, contactNo, address;
    Spinner district, brand, newBrand;
    Button addBrand, save;
    ArrayList<String> brandList, districtList;
    SpinnerAdapter brandAdapter, districtAdapter;
    public static String base_url = "http://pagodalabs.com.np/";
    public static String url = "http://pagodalabs.com.np/retailmapping/brand/api/brand";
    public static String post_url = "http://pagodalabs.com.np/retailmapping/shop/api/shop";
    Toolbar toolbar;
    ProgressDialog progressDialog;
    LinearLayout brandContainer;
    Call<ResponseBody> call;
    static ArrayList<String> allBrands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        Window window = AddShop.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(AddShop.this.getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Add Shop");

        shopName = (TextInputEditText) findViewById(R.id.TextName);
        ownerName = (TextInputEditText) findViewById(R.id.textOwner);
        contactNo = (TextInputEditText) findViewById(R.id.textContact);
        address = (TextInputEditText) findViewById(R.id.textAddress);

        district = (Spinner) findViewById(R.id.districtDrop);
        brand = (Spinner) findViewById(R.id.brandDrop);
        allBrands = new ArrayList<>();

        addBrand = (Button) findViewById(R.id.addBrand);
        save = (Button) findViewById(R.id.save);

        brandList = new ArrayList<>();
        districtList = new ArrayList<>();

        brandContainer = (LinearLayout) findViewById(R.id.brandContainer);

        addBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBrand = new Spinner(AddShop.this);
                newBrand.setBackground(getResources().getDrawable(android.R.drawable.btn_dropdown));
                newBrand.setAdapter(brandAdapter);
                brandContainer.addView(newBrand);
            }
        });

        if (InternetConnection.checkConnection(getApplicationContext())) {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    int brandCount = brandContainer.getChildCount();
                    String mOutletName = shopName.getText().toString().trim();
                    String mOwnerName = ownerName.getText().toString().trim();
                    String mContactNo = contactNo.getText().toString().trim();
                    String mAddress = address.getText().toString().trim();
                    String mDistrict = district.getSelectedItem().toString().trim();
                    String mBrand = brand.getSelectedItem().toString().trim();
                    String[] brandPart = mBrand.split("\\.");
                    String brandId = brandPart[0];
                    allBrands.add(brandId);

                    if (newBrand.getSelectedItem() != null) {
                        for (int i = 0; i < brandCount; i++) {
                            String mOtherBrands = ((Spinner) brandContainer.getChildAt(i)).getSelectedItem().toString();
                            String[] otherBrandParts = mOtherBrands.split("\\.");
                            String otherBrandId = otherBrandParts[0];
                            allBrands.add(otherBrandId);
                        }
                    }

                    String mUserName = LoginActivity.textInputEditTextUsername.getText().toString().trim();

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("shop_name", mOutletName);
                    params.put("owner_name", mOwnerName);
                    params.put("contact_no", mContactNo);
                    params.put("address", mAddress);
                    params.put("district", mDistrict);
                    params.put("brand_id", allBrands);
                    params.put("created_by", LoginActivity.id);

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
                                Log.e("addShopPostResponse", responseString);
                            }
                        }

                    });

                    Toast.makeText(AddShop.this, "Shop Added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddShop.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void run() throws IOException {
        progressDialog = new ProgressDialog((AddShop.this));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url) // that means base url + the left url in interface
                .client(okHttpClient)//adding okhttp3 object that we have created
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        call = retrofit.create(ApiInterface.class).getResponse(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);
                    if (myResponse != null) {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        JSONArray brandArray = jsonObject.getJSONArray("brand");
                        JSONArray districtArray = jsonObject.getJSONArray("district");
                        /**
                         * Check Length of Array...
                         */
                        int lenArray = brandArray.length();
                        if (lenArray > 0) {
                            for (int jIndex = 0; jIndex < lenArray; jIndex++) {

                                JSONObject innerObject = brandArray.getJSONObject(jIndex);
                                String name = innerObject.getString("brand");
                                String id = innerObject.getString("brand_id");
                                String brandName = id + ". " + name;
                                brandList.add(brandName);
                            }
                        }

                        int lenDistrict = districtArray.length();

                        if (lenDistrict > 0) {

                            for (int d = 0; d < lenDistrict; d++) {

                                JSONObject innerDistrictObject = districtArray.getJSONObject(d);
                                String districtName = innerDistrictObject.getString("district");

                                districtList.add(districtName);
                            }
                        }
                    } else
                        Toast.makeText(AddShop.this, "No Data found", Toast.LENGTH_SHORT).show();


                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                AddShop.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        brandAdapter = new SpinnerAdapter(AddShop.this, brandList);
                        brand.setAdapter(brandAdapter);

                        districtAdapter = new SpinnerAdapter(AddShop.this, districtList);
                        district.setAdapter(districtAdapter);

                    }

                });

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Furniture", t.toString());
                Toast.makeText(AddShop.this, R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

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

