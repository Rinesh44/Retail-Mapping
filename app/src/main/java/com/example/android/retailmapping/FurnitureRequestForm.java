package com.example.android.retailmapping;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
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
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FurnitureRequestForm extends AppCompatActivity {
    Spinner outletName, furniture, brand;
    TextInputEditText height, width, qty, doo;
    EditText remarks;
    Button order;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    ArrayList<String> shopList, brandList, furnitureList;
    public static String url = "http://pagodalabs.com.np/retailmapping/brand/api/brand";
    public static String post_url = "http://pagodalabs.com.np/retailmapping/furniture_order/api/furniture_order";
    public static String base_url = "http://pagodalabs.com.np/";
    SpinnerAdapter shopAdapter, brandAdapter, furnitureAdapter;
    Call<ResponseBody> call;
    private Calendar calendar;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_request_form);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = FurnitureRequestForm.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color

            window.setStatusBarColor(FurnitureRequestForm.this.getResources().getColor(R.color.colorPrimaryDark));
        }

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Add Furniture Request Details");

        outletName = (Spinner) findViewById(R.id.shopDrop);
        furniture = (Spinner) findViewById(R.id.furnitureDrop);
        brand = (Spinner) findViewById(R.id.brandDrop);

        shopList = new ArrayList<>();
        brandList = new ArrayList<>();
        furnitureList = new ArrayList<>();

        height = (TextInputEditText) findViewById(R.id.editTextHeight);
        width = (TextInputEditText) findViewById(R.id.editTextWidth);
        qty = (TextInputEditText) findViewById(R.id.textqty);
        doo = (TextInputEditText) findViewById(R.id.textDate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        remarks = (EditText) findViewById(R.id.remarksText);

        order = (Button) findViewById(R.id.order);

        doo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
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

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    String mOutletName = outletName.getSelectedItem().toString().trim();
                    String[] getShopId = mOutletName.split("\\.");
                    String mOutletId = getShopId[0];
                    String mFurniture = furniture.getSelectedItem().toString().trim();
                    String[] getFurnitureId = mFurniture.split("\\.");
                    String mFurnitureId = getFurnitureId[0];
                    String mFurnitureName = getFurnitureId[1];
                    String mWidth = width.getText().toString().trim();
                    String mHeight = height.getText().toString().trim();
                    String mQuantity = qty.getText().toString().trim();
                    String mBrand = brand.getSelectedItem().toString().trim();
                    String[] getBrandId = mBrand.split("\\.");
                    String mBrandId = getBrandId[0];
                    String mDate = doo.getText().toString().trim();
                    String mRemarks = remarks.getText().toString().trim();
                    String mUserName = LoginActivity.textInputEditTextUsername.getText().toString().trim();

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("shop_id", mOutletId);
                    params.put("furniture", mFurnitureName);
                    params.put("width", mWidth);
                    params.put("height", mHeight);
                    params.put("qty", mQuantity);
                    params.put("brand_id", mBrandId);
                    params.put("date_of_order", mDate);
                    params.put("remarks", mRemarks);
                    params.put("order_by", LoginActivity.id);

                    JSONObject parameter = new JSONObject(params);
                    Log.e("JSON:", parameter.toString());

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

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
                                Log.e("furniturePostResponse", responseString);
                            }
                        }

                    });
                    finish();
                    Toast.makeText(FurnitureRequestForm.this, "Request Posted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(FurnitureRequestForm.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(FurnitureRequestForm.this);
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
                    Log.e("getResponse", myResponse);
                    if (myResponse != null) {
                        JSONObject jsonObject = new JSONObject(myResponse);

                        JSONArray array = jsonObject.getJSONArray("brand");
                        JSONArray furnitureArray = jsonObject.getJSONArray("furniture");
                        JSONArray shopArray = jsonObject.getJSONArray("shop");

                        /**
                         * Check Length of Array...
                         */

                        int lenArray = array.length();
                        if (lenArray > 0) {
                            for (int jIndex = 0; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String name = innerObject.getString("brand");
                                String id = innerObject.getString("brand_id");
                                String brandName = id + ". " + name;
                                brandList.add(brandName);
                            }
                        }

                        int lenFurniture = furnitureArray.length();

                        if (lenFurniture > 0) {

                            for (int f = 0; f < lenFurniture; f++) {

                                JSONObject innerFurnitureObject = furnitureArray.getJSONObject(f);
                                String name = innerFurnitureObject.getString("furniture");
                                String id = innerFurnitureObject.getString("furniture_id");
                                String furnitureName = id + ". " + name;
                                furnitureList.add(furnitureName);
                                Log.e("furniture:", furnitureName);
                            }

                        }

                        int lenShop = shopArray.length();

                        if (lenShop > 0) {

                            for (int s = 0; s < lenShop; s++) {
                                JSONObject innerShopObject = shopArray.getJSONObject(s);
                                String name = innerShopObject.getString("shop_name");
                                String id = innerShopObject.getString("shop_id");
                                String shopName = id + ". " + name;
                                shopList.add(shopName);
                            }
                        }

                    } else
                        Toast.makeText(FurnitureRequestForm.this, "No data found", Toast.LENGTH_SHORT).show();


                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                FurnitureRequestForm.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        brandAdapter = new SpinnerAdapter(FurnitureRequestForm.this, brandList);
                        brand.setAdapter(brandAdapter);

                        furnitureAdapter = new SpinnerAdapter(FurnitureRequestForm.this, furnitureList);
                        furniture.setAdapter(furnitureAdapter);

                        shopAdapter = new SpinnerAdapter(FurnitureRequestForm.this, shopList);
                        outletName.setAdapter(shopAdapter);

                        Intent i = getIntent();
                        String selected_shop = i.getStringExtra("shop_name");
                        setSpinText(outletName, selected_shop);
                    }

                });

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FurnitureRequestForm", t.toString());
                Toast.makeText(FurnitureRequestForm.this, R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    dateListener, year, month, day);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg3, arg2 + 1, arg1);
                }
            };

    private void showDate(int day, int month, int year) {

        doo.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    public void setSpinText(Spinner spin, String text) {
        for (int i = 0; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }

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
