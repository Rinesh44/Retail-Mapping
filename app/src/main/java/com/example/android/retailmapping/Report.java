package com.example.android.retailmapping;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.retailmapping.Interface.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Report extends AppCompatActivity {
    Toolbar toolbar;
    private Boolean exit = false;
    private ProgressDialog progressDialog;
    private static final int REQUEST_PERMISSIONS = 998;
    private SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> shopList;
    private ListView listView;
    //private EditText search;
    private TextView mEmptyStateTextView;
    String valueShopId;
    Call<ResponseBody> call;
    public static String base_url = "http://pagodalabs.com.np/";
    public static String url = "http://pagodalabs.com.np/retailmapping/shop/api/report/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        shopList = new ArrayList<>();

        adapter = new SimpleAdapter(Report.this, shopList,
                R.layout.showdate, new String[]{"created_at"}, new int[]{R.id.date});
        adapter.notifyDataSetChanged();

        listView = (ListView) findViewById(R.id.list_shop);
        //search = (EditText) findViewById(R.id.inputSearch);

        Intent i = getIntent();
        valueShopId = i.getStringExtra("shop_id");


        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        if (InternetConnection.checkConnection(Report.this)) {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(Report.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(Report.this);
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

        call = retrofit.create(ApiInterface.class).getResponse(url + valueShopId);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    final String myResponse = response.body().string();
                    Log.e("getResponse:", myResponse);
                    if (myResponse != null) {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        JSONArray data = jsonObject.getJSONArray("report");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String particulars = c.getString("particulars");
                            String brand = c.getString("brand");
                            String height = c.getString("height");
                            String width = c.getString("width");
                            String qty = c.getString("qty");
                            String before_visit = c.getString("before_visit");
                            String after_visit = c.getString("after_visit");
                            String shop_name = c.getString("shop_name");
                            String owner_name = c.getString("owner_name");
                            String field_id = c.getString("fieldenquiry_id");
                            String created_at = c.getString("created_at");

                            // tmp hash map for single data
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("particulars", particulars);
                            hashMap.put("brand", brand);
                            hashMap.put("height", height);
                            hashMap.put("width", width);
                            hashMap.put("qty", qty);
                            hashMap.put("before_visit", before_visit);
                            hashMap.put("after_visit", after_visit);
                            hashMap.put("shop_name", shop_name);
                            hashMap.put("owner_name", owner_name);
                            hashMap.put("fieldenquiry_id", field_id);
                            hashMap.put("created_at", created_at);
                            // adding contact to contact list
                            shopList.add(hashMap);
                            adapter.notifyDataSetChanged();
                        }
                    } else
                        Toast.makeText(Report.this, "No Data found", Toast.LENGTH_SHORT).show();


                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                Report.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        mEmptyStateTextView.setText(R.string.no_data);

                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                HashMap<String, Object> obj = (HashMap<String, Object>) adapter.getItem(position);

                                final String particulars = (String) obj.get("particulars");
                                final String brand = (String) obj.get("brand");
                                final String height = (String) obj.get("height");
                                final String width = (String) obj.get("width");
                                final String qty = (String) obj.get("qty");
                                final String before_visit = (String) obj.get("before_visit");
                                final String after_visit = (String) obj.get("after_visit");
                                final String shop_name = (String) obj.get("shop_name");
                                final String owner_name = (String) obj.get("owner_name");
                                final String field_id = (String) obj.get("fieldenquiry_id");

                                Intent in = new Intent(Report.this, ReportDetails.class);
                                in.putExtra("particulars", particulars);
                                in.putExtra("brand", brand);
                                in.putExtra("height", height);
                                in.putExtra("width", width);
                                in.putExtra("qty", qty);
                                in.putExtra("before_visit", before_visit);
                                in.putExtra("after_visit", after_visit);
                                in.putExtra("shop_name", shop_name);
                                in.putExtra("owner_name", owner_name);
                                in.putExtra("fieldenquiry_id", field_id);
                                startActivity(in);
                            }
                        });
/*
                        search.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                // When user changed the Text
                                adapter.notifyDataSetChanged();
                                Report.this.adapter.getFilter().filter(cs);
                            }

                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                          int arg3) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub
                            }
                        });
                        */
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ShopList", t.toString());
                Toast.makeText(Report.this, R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }

            // case blocks for other MenuItems (if any)
        }
        return false;
    }
}
