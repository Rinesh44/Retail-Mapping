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

public class FieldTeamActivity extends AppCompatActivity {
    Toolbar toolbar;
    private Boolean exit = false;
    private ProgressDialog progressDialog;
    private static final int REQUEST_PERMISSIONS = 998;
    private SimpleAdapter adapter;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    ArrayList<HashMap<String, String>> shopList;
    private ListView listView;
    private EditText search;
    private TextView mEmptyStateTextView;
    Call<ResponseBody> call;
    SwipeRefreshLayout refresh;
    public static String base_url = "http://pagodalabs.com.np/";
    public static String url = "http://pagodalabs.com.np/retailmapping/field_detail/api/field_detail/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_team);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(FieldTeamActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS);

        shopList = new ArrayList<>();

        NavigationDrawer navigationDrawerFragment = (NavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        adapter = new SimpleAdapter(FieldTeamActivity.this, shopList,
                R.layout.list_item, new String[]{"shop_name", "address", "created_at"}, new int[]{R.id.name, R.id.district, R.id.date});
        adapter.notifyDataSetChanged();

        listView = (ListView) findViewById(R.id.list_shop);
        search = (EditText) findViewById(R.id.inputSearch);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shopList.clear();
                try {
                    run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                refresh.setRefreshing(false);
            }
        });

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        if (InternetConnection.checkConnection(FieldTeamActivity.this)) {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(FieldTeamActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(FieldTeamActivity.this);
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

        call = retrofit.create(ApiInterface.class).getResponse(url + LoginActivity.id);

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
                        JSONArray data = jsonObject.getJSONArray("Field_Report");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String particulars = c.getString("particulars");
                            String brand = c.getString("brand");
                            String height = c.getString("height");
                            String width = c.getString("width");
                            String qty = c.getString("qty");
                            String before_visit = c.getString("before_visit");
                            String after_visit = c.getString("after_visit");
                            String username = c.getString("username");
                            String shop_name = c.getString("shop_name");
                            String owner_name = c.getString("owner_name");
                            String field_id = c.getString("fieldenquiry_id");
                            String address = c.getString("address");
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
                            hashMap.put("username", username);
                            hashMap.put("shop_name", shop_name);
                            hashMap.put("owner_name", owner_name);
                            hashMap.put("fieldenquiry_id", field_id);
                            hashMap.put("address", address);
                            hashMap.put("created_at", created_at);
                            // adding contact to contact list
                            shopList.add(hashMap);
                            adapter.notifyDataSetChanged();
                        }
                    } else
                        Toast.makeText(FieldTeamActivity.this, "No Data found", Toast.LENGTH_SHORT).show();


                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                FieldTeamActivity.this.runOnUiThread(new Runnable() {
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
                                final String username = (String) obj.get("username");
                                final String shop_name = (String) obj.get("shop_name");
                                final String owner_name = (String) obj.get("owner_name");
                                final String field_id = (String) obj.get("fieldenquiry_id");

                                Intent in = new Intent(FieldTeamActivity.this, FieldTeamDetails.class);
                                in.putExtra("particulars", particulars);
                                in.putExtra("brand", brand);
                                in.putExtra("height", height);
                                in.putExtra("width", width);
                                in.putExtra("qty", qty);
                                in.putExtra("before_visit", before_visit);
                                in.putExtra("after_visit", after_visit);
                                in.putExtra("username", username);
                                in.putExtra("shop_name", shop_name);
                                in.putExtra("owner_name", owner_name);
                                in.putExtra("fieldenquiry_id", field_id);
                                startActivity(in);
                            }
                        });

                        search.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                // When user changed the Text
                                adapter.notifyDataSetChanged();
                                FieldTeamActivity.this.adapter.getFilter().filter(cs);
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
                Log.e("ShopList", t.toString());
                Toast.makeText(FieldTeamActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (exit) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.field_team_overflow, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_shop: {
                Intent enter_add_shop = new Intent(FieldTeamActivity.this, AddShop.class);
                startActivity(enter_add_shop);
                break;
            }

            case R.id.existing: {
                Intent enter_existing = new Intent(FieldTeamActivity.this, FieldTeamForm.class);
                startActivity(enter_existing);
                break;
            }

            // case blocks for other MenuItems (if any)
        }
        return false;
    }
}
