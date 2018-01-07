package com.example.android.retailmapping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Shaakya on 9/4/2017.
 */

public class FurnitureRequest extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private ListView listView;
    private EditText search;
    private SimpleAdapter adapter;
    private Toolbar toolbar;
    public static String base_url = "http://pagodalabs.com.np/";
    public static String url = "http://pagodalabs.com.np/retailmapping/shop/api/shop";
    ArrayList<HashMap<String, String>> shopList;
    Call<ResponseBody> call;
    private TextView mEmptyStateTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.furniture_request);

        Window window = FurnitureRequest.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(FurnitureRequest.this.getResources().getColor(R.color.colorPrimaryDark));
        shopList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Select Outlet");

        adapter = new SimpleAdapter(FurnitureRequest.this, shopList,
                R.layout.list_item_shop, new String[]{"shop_name", "district"}, new int[]{R.id.name, R.id.district});
        adapter.notifyDataSetChanged();

        listView = (ListView) findViewById(R.id.list_shop);
        search = (EditText) findViewById(R.id.inputSearch);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        if (InternetConnection.checkConnection(FurnitureRequest.this)) {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(FurnitureRequest.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(FurnitureRequest.this);
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
                        JSONArray data = jsonObject.getJSONArray("shop");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String shop_name = c.getString("shop_name");
                            String district = c.getString("district");
                            Log.e("shoplist:", shop_name);
                            // tmp hash map for single data
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("shop_name", shop_name);
                            hashMap.put("district", district);
                            // adding contact to contact list
                            shopList.add(hashMap);
                            adapter.notifyDataSetChanged();
                        }
                    } else
                        Toast.makeText(FurnitureRequest.this, "No Data found", Toast.LENGTH_SHORT).show();


                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                FurnitureRequest.this.runOnUiThread(new Runnable() {
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

                                String selected_shop = (String) obj.get("shop_name");

                                Intent in = new Intent(FurnitureRequest.this, FurnitureRequestForm.class);
                                in.putExtra("shop_name", selected_shop);
                                startActivity(in);
                            }
                        });

                        search.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                // When user changed the Text
                                FurnitureRequest.this.adapter.getFilter().filter(cs);
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

                    }

                });

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Material", t.toString());
                Toast.makeText(FurnitureRequest.this, R.string.error, Toast.LENGTH_SHORT).show();
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


