package com.example.android.retailmapping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.retailmapping.Interface.ApiInterface;
import com.example.android.retailmapping.helpers.InputValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class CreateUser extends AppCompatActivity {
    Toolbar toolbar;
    private TextInputEditText password, confirmPassword, username, email;
    private TextInputLayout layoutUsername, layoutEmail, layoutPassword, layoutConfirmPassword;
    ProgressDialog progressDialog;
    private Spinner brand;
    ArrayList<String> brandList;
    private AppCompatRadioButton yes, no;
    private InputValidation inputValidation;
    private RadioGroup activeGroup;
    LinearLayout root;
    public static String base_url = "http://pagodalabs.com.np/";
    public static String url = "http://pagodalabs.com.np/retailmapping/brand/api/brand";
    public static String post_url = "http://pagodalabs.com.np/retailmapping/auth/api/client_register";
    AppCompatButton save, cancel;
    SpinnerAdapter brandAdapter;
    Call<ResponseBody> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = CreateUser.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color

            window.setStatusBarColor(CreateUser.this.getResources().getColor(R.color.colorPrimaryDark));
        }


        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Create User");

        inputValidation = new InputValidation(CreateUser.this);


        root = (LinearLayout) findViewById(R.id.rootview);

        layoutUsername = (TextInputLayout) findViewById(R.id.layoutUsername);
        layoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        layoutPassword = (TextInputLayout) findViewById(R.id.layoutPassword);
        layoutConfirmPassword = (TextInputLayout) findViewById(R.id.layoutConfirmPassword);


        username = (TextInputEditText) findViewById(R.id.textUsername);
        email = (TextInputEditText) findViewById(R.id.textEmail);
        password = (TextInputEditText) findViewById(R.id.textPassword);
        confirmPassword = (TextInputEditText) findViewById(R.id.textComfirmPassword);

        brand = (Spinner) findViewById(R.id.brandDrop);

        brandList = new ArrayList<>();

        activeGroup = (RadioGroup) findViewById(R.id.radioGroup);
        yes = (AppCompatRadioButton) findViewById(R.id.yes);
        no = (AppCompatRadioButton) findViewById(R.id.no);

        save = (AppCompatButton) findViewById(R.id.btnSave);
        cancel = (AppCompatButton) findViewById(R.id.btnCancel);

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
                    String checkActive;
                    String mUsername = username.getText().toString().trim();
                    String mEmail = email.getText().toString().trim();
                    String mPassword = password.getText().toString().trim();
                    String mConfirmPassword = confirmPassword.getText().toString().trim();
                    String mBrand = brand.getSelectedItem().toString().trim();
                    String[] getBrandId = mBrand.split("\\.");
                    String mBrandId = getBrandId[0];

                    String mActive = ((AppCompatRadioButton) findViewById(activeGroup.getCheckedRadioButtonId())).getText().toString();
                    if (mActive.matches("Yes"))
                        checkActive = "1";
                    else
                        checkActive = "0";


                    if (!inputValidation.isInputEditTextFilled(username, layoutUsername, getString(R.string.error_message_name))) {
                        return;
                    }
                    if (!inputValidation.isInputEditTextFilled(email, layoutEmail, getString(R.string.error_message_email))) {
                        return;
                    }
                    if (!inputValidation.isInputEditTextEmail(email, layoutEmail, getString(R.string.error_message_email))) {
                        return;
                    }
                    if (!inputValidation.isInputEditTextFilled(password, layoutPassword, getString(R.string.error_message_password))) {
                        return;
                    }
                    if (!inputValidation.isInputEditTextFilled(confirmPassword, layoutConfirmPassword, getString(R.string.error_message_password))) {
                        return;
                    }
                    if (mPassword.matches(mConfirmPassword)) {

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("username", mUsername);
                        params.put("email", mEmail);
                        params.put("password", mPassword);
                        params.put("brand_id", mBrandId);
                        params.put("active", checkActive);

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
                                    Log.e("CreateUserResponse", responseString);
                                }
                            }

                        });
                        finish();
                        Toast.makeText(CreateUser.this, "Client Created", Toast.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(root, getString(R.string.error_password_match), Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CreateUser.this, "Unable to save. No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    void run() throws IOException {
        progressDialog = new ProgressDialog(CreateUser.this);
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

                    } else
                        Toast.makeText(CreateUser.this, "No data found", Toast.LENGTH_SHORT).show();


                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                call.cancel();

                CreateUser.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        brandAdapter = new SpinnerAdapter(CreateUser.this, brandList);
                        brand.setAdapter(brandAdapter);

                    }

                });

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CreateUser", t.toString());
                Toast.makeText(CreateUser.this, R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });



    }
}
