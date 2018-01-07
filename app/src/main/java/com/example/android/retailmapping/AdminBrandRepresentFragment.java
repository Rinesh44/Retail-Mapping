package com.example.android.retailmapping;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shaakya on 9/5/2017.
 */

public class AdminBrandRepresentFragment extends Fragment {
    private ProgressDialog progressDialog;
    private ListView listView;
    private SimpleAdapter adapter;
    private TextView brandName;
    ArrayList<HashMap<String, String>> brandList;

    public AdminBrandRepresentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //fragment needs a view.so defining it and finding the layout as a container
        View rootView = inflater.inflate(R.layout.fragment_admin_brand_represent, container, false);
        brandList = new ArrayList<>();

        adapter = new SimpleAdapter(getActivity(), brandList,
                R.layout.brand_list_item, new String[]{"brand_name", "color_code"}, new int[]{R.id.name, R.id.color});
        adapter.notifyDataSetChanged();

        listView = (ListView) rootView.findViewById(R.id.list_brand);


        if (InternetConnection.checkConnection(getActivity())) {
            new AdminBrandRepresentFragment.GetNames().execute();
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetNames extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            /**
             * Getting JSON Object from Web Using okHttp
             */
            JSONObject jsonObject = JSONParser.getDataFromWeb();
            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonObject != null) {
                    /**
                     * Check Length...
                     */
                    if (jsonObject.length() > 0) {
                        // Getting JSON Array node
                        JSONArray data = jsonObject.getJSONArray("brand");
                        // looping through All data
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String brand_name = c.getString("brand");
                            String color_code = c.getString("color");

                            // tmp hash map for single data
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("brand_name", brand_name);
                            hashMap.put("color_code",color_code);
                            // adding contact to contact list
                            brandList.add(hashMap);
                            adapter.notifyDataSetChanged();

                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                }
            } catch (final JSONException e) {
                Log.i(JSONParser.TAG, "" + e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            listView.setAdapter(adapter);

            for(int i = 0; i < listView.getAdapter().getCount();i++) {
            View view = (View)listView.getAdapter().getView(i, null, listView);
            brandName = (TextView) view.findViewById(R.id.name);
            Log.e("BrandText:", brandName.getText().toString());
            String getColor = brandList.get(i).get("color_code");
            Log.e("colorValue:", getColor);
            brandName.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            }

        }

    }

}


