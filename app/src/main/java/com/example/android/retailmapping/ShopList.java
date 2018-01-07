package com.example.android.retailmapping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Shaakya on 9/4/2017.
 */

public class ShopList extends Fragment {
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

    public ShopList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //fragment needs a view.so defining it and finding the layout as a container
        View rootView = inflater.inflate(R.layout.shop_list, container, false);
        shopList = new ArrayList<>();

        adapter = new SimpleAdapter(getActivity(), shopList,
                R.layout.list_item_shop, new String[]{"shop_name", "district"}, new int[]{R.id.name, R.id.district});
        adapter.notifyDataSetChanged();

        listView = (ListView) rootView.findViewById(R.id.list_shop);
        search = (EditText) rootView.findViewById(R.id.inputSearch);

        mEmptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        if (InternetConnection.checkConnection(getActivity())) {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }


    void run() throws IOException{
        progressDialog = new ProgressDialog(getActivity());
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

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try{

                    final String myResponse = response.body().string();
                    Log.e("getResponse:",myResponse);
                    if(myResponse != null) {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        JSONArray data = jsonObject.getJSONArray("shop");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String shop_name = c.getString("shop_name");
                            String district = c.getString("district");
                            String address = c.getString("address");
                            String contact_no = c.getString("contact_no");
                            String created_by = c.getString("username");
                            String owner_name = c.getString("owner_name");
                            String shop_id = c.getString("shop_id");
                            String brand = c.getString("brand");

                            Log.e("shoplist:", shop_name);
                            // tmp hash map for single data
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("shop_name", shop_name);
                            hashMap.put("district", district);
                            hashMap.put("address", address);
                            hashMap.put("contact_no", contact_no);
                            hashMap.put("created_by", created_by);
                            hashMap.put("owner_name", owner_name);
                            hashMap.put("brand", brand);
                            hashMap.put("shop_id", shop_id);
                            // adding contact to contact list
                            shopList.add(hashMap);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else
                        Toast.makeText(getActivity(), "No Data found", Toast.LENGTH_SHORT).show();


                }catch (JSONException | IOException | NullPointerException e){
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                getActivity().runOnUiThread(new Runnable() {
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

                                final String shop_name = (String) obj.get("shop_name");
                                final String district = (String) obj.get("district");
                                final String address = (String) obj.get("address");
                                final String contact_no = (String) obj.get("contact_no");
                                final String created_by = (String) obj.get("created_by");
                                final String owner_name = (String) obj.get("owner_name");
                                final String brand = (String) obj.get("brand");
                                final String shop_id = (String) obj.get("shop_id");

                                Intent in = new Intent(getActivity(), ShopDetails.class);
                                in.putExtra("shop_name", shop_name);
                                in.putExtra("district", district);
                                in.putExtra("address", address);
                                in.putExtra("contact_no", contact_no);
                                in.putExtra("created_by", created_by);
                                in.putExtra("owner_name", owner_name);
                                in.putExtra("brand", brand);
                                in.putExtra("shop_id", shop_id);
                                startActivity(in);
                            }
                        });

                        search.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                // When user changed the Text
                                ShopList.this.adapter.getFilter().filter(cs);
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
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

}


