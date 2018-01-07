package com.example.android.retailmapping;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FieldTeamForm extends AppCompatActivity {
    TextInputEditText width, height, qty;
    Spinner brand,shopname;
    Button before, addDesc, save;
    TextView particulars;
    ImageView beforeImage;
    private final int requestCodeForBeforeImage = 10;
    private final int newRequestCodeForBeforeImage = 7;
    private final int requestCodeForAfterImage = 20;
    EditText textParticulars, newTextParticulars;
    ArrayList<String> shopList,brandList;
    SpinnerAdapter shopAdapter,brandAdapter;
    public static String post_url = "http://pagodalabs.com.np/retailmapping/field_detail/api/field_detail";
    public static View addDescription;
    android.support.v7.widget.Toolbar toolbar;
    static String encodedImage, newEncodedImage;
    LinearLayout container;
    ArrayList<String> arrayParticulars, arrayWidth, arrayHeight, arrayQty, arrayBrandId, arrayEncodedImage;
    private static final int REQUEST_STORAGE_ACCESS = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_team_form);

        Window window = FieldTeamForm.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(FieldTeamForm.this.getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Existing Shop");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_STORAGE_ACCESS);
        }


        width = (TextInputEditText)findViewById(R.id.editTextWidth);
        height = (TextInputEditText)findViewById(R.id.editTextHeight);
        qty = (TextInputEditText) findViewById(R.id.textqty);

        brand = (Spinner)findViewById(R.id.brandDrop);
        shopname = (Spinner)findViewById(R.id.shopDrop);

        brandList = new ArrayList<>();
        shopList = new ArrayList<>();

        arrayBrandId = new ArrayList<>();
        arrayHeight = new ArrayList<>();
        arrayParticulars = new ArrayList<>();
        arrayWidth = new ArrayList<>();
        arrayQty  = new ArrayList<>();
        arrayEncodedImage = new ArrayList<>();

        before = (Button)findViewById(R.id.beforeBtn);
        addDesc = (Button)findViewById(R.id.add);

        container = (LinearLayout) findViewById(R.id.container);

        save = (Button) findViewById(R.id.save);

        textParticulars = (EditText) findViewById(R.id.textParticulars);

        beforeImage = (ImageView) findViewById(R.id.captured);

        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureBeforeImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureBeforeImage,requestCodeForBeforeImage);
            }
        });

        addDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDescription = getLayoutInflater().inflate(R.layout.add_description,null, false);
                container.addView(addDescription);
                Spinner newBrand = (Spinner) addDescription.findViewById(R.id.brandDrop);
                newBrand.setAdapter(brandAdapter);

                Button newCaptureBtn = (Button) addDescription.findViewById(R.id.beforeBtn);
                newCaptureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent captureBeforeImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(captureBeforeImage,newRequestCodeForBeforeImage);
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    int containerCount = container.getChildCount();
                    Log.e("ContainerCount:", String.valueOf(containerCount));
                    String mOutletName = shopname.getSelectedItem().toString().trim();
                    String[] getShopId = mOutletName.split("\\.");
                    String mOutletId = getShopId[0];
                    String mParticulars = textParticulars.getText().toString().trim();
                    arrayParticulars.add(mParticulars);
                    String mWidth = width.getText().toString().trim();
                    arrayWidth.add(mWidth);
                    String mHeight = height.getText().toString().trim();
                    arrayHeight.add(mHeight);
                    String mQty = qty.getText().toString().trim();
                    arrayQty.add(mQty);
                    String mBrand = brand.getSelectedItem().toString().trim();
                    String[] getBrandId = mBrand.split("\\.");
                    String mBrandId = getBrandId[0];
                    arrayBrandId.add(mBrandId);
                    arrayEncodedImage.add(encodedImage);
                    //Log.e("imageCount:",String.valueOf(arrayEncodedImage.size()));
                    //String mCollectedBy = LoginActivity.getUserId;

                    if(container != null){
                        for(int i = 0; i <= containerCount -1; i++){
                            String newParticulars = ((EditText) container.getChildAt(i).findViewById(R.id.textParticulars)).getText().toString().trim();
                            arrayParticulars.add(newParticulars);
                            String newWidth = ((TextInputEditText) container.getChildAt(i).findViewById(R.id.editTextWidth)).getText().toString().trim();
                            arrayWidth.add(newWidth);
                            String newHeight = ((TextInputEditText) container.getChildAt(i).findViewById(R.id.editTextHeight)).getText().toString().trim();
                            arrayHeight.add(newHeight);
                            String newQty = ((TextInputEditText) container.getChildAt(i).findViewById(R.id.textqty)).getText().toString().trim();
                            arrayQty.add(newQty);
                            String newBrand = ((Spinner) container.getChildAt(i).findViewById(R.id.brandDrop)).getSelectedItem().toString().trim();
                            arrayBrandId.add(newBrand);
                            ImageView image = ((ImageView) container.getChildAt(i).findViewById(R.id.captured));
                            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();
                            newEncodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                            arrayEncodedImage.add(newEncodedImage);
                        }
                    }


                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("shop_id", mOutletId);
                    params.put("particulars", arrayParticulars);
                    params.put("width", arrayWidth);
                    params.put("height", arrayHeight);
                    params.put("qty", arrayQty);
                    params.put("brand_id", arrayBrandId);
                    params.put("before_visit", arrayEncodedImage);
                    params.put("collected_by", LoginActivity.id);

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
                                Log.e("FieldTeamResponse", responseString);
                            }
                        }

                    });

                    Toast.makeText(FieldTeamForm.this, "Done", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FieldTeamForm.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (InternetConnection.checkConnection(getApplicationContext())) {
            new FieldTeamForm.GetDataTask().execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if (requestCode == requestCodeForBeforeImage) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                beforeImage.setVisibility(View.VISIBLE);
                beforeImage.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            }

            if (requestCode == newRequestCodeForBeforeImage) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ImageView newBeforeImage = (ImageView) addDescription.findViewById(R.id.captured);
                newBeforeImage.setVisibility(View.VISIBLE);
                newBeforeImage.setImageBitmap(bitmap);

            }
        }

    }

    //Creating Get Data Task for Getting Data From Web

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            dialog = new ProgressDialog(FieldTeamForm.this);
            dialog.setTitle("Please Wait...");
            dialog.setMessage("Getting Information");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

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
                        /**
                         * Getting Array named "brand" From MAIN Json Object
                         */
                        JSONArray array = jsonObject.getJSONArray("brand");
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

                        int lenShop = shopArray.length();

                        if(lenShop > 0) {

                            for (int s = 0; s < lenShop; s++) {

                                JSONObject innerShopObject = shopArray.getJSONObject(s);
                                String name = innerShopObject.getString("shop_name");
                                String id = innerShopObject.getString("shop_id");
                                String shopName = id + ". " + name;
                                shopList.add(shopName);

                            }

                        }
                    }
                } else {
                    Toast.makeText(FieldTeamForm.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException je) {
                Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            brandAdapter = new SpinnerAdapter(FieldTeamForm.this, brandList);
            brand.setAdapter(brandAdapter);
            cancel(true);

            shopAdapter = new SpinnerAdapter(FieldTeamForm.this, shopList);
            shopname.setAdapter(shopAdapter);
            cancel(true);

        }
    }


}


