package com.example.android.retailmapping;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.retailmapping.helpers.InputValidation;
import com.example.android.retailmapping.sql.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private final AppCompatActivity activity = LoginActivity.this;
    private Button appCompatButtonLogin;
    private TextView textViewLinkRegister;
    private TextView title;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    public static TextInputEditText textInputEditTextUsername;
    private TextInputEditText textInputEditTextPassword;
    private DatabaseHelper databaseHelper;
    private LinearLayoutCompat root;
    private InputValidation inputValidation;
    private Boolean exit = false;
    static String mUsername;
    HashMap<String, String> brandHashMap;
    Collection<String> c;
    static ArrayList<HashMap> brands;
    static String username, group, email, id, image, parsedImage;
    private static final int REQUEST_PERMISSIONS = 777;
    static boolean checkClient = false;
    private static final String url = "http://pagodalabs.com.np/retailmapping/auth/api/login";

    public static String getUserName;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        initObjects();

    }

    private void initViews() {
        root = (LinearLayoutCompat) findViewById(R.id.rootview);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextUsername = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        //forgotpassword = (TextView) findViewById(R.id.forgot);
        textViewLinkRegister = (TextView) findViewById(R.id.textViewLinkRegister);
        appCompatButtonLogin = (Button) findViewById(R.id.appCompatButtonLogin);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
        //forgotpassword.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);
        brands = new ArrayList<>();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                // Navigate to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
/*
            case R.id.forgot:
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgotpassword);
                dialog.show();

                final EditText email = (EditText) dialog.findViewById(R.id.email);
                final EditText getpass = (EditText) dialog.findViewById(R.id.newpassword);
                final EditText confirm = (EditText) dialog.findViewById(R.id.confirm);

                Button ok = (Button) dialog.findViewById(R.id.ok);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String value1 = getpass.getText().toString().trim();
                        String value2 = confirm.getText().toString().trim();
                        String fixed;
                        String userName = email.getText().toString();
                        if (userName.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
                            Toast.makeText(getApplicationContext(), "Invalid EmailID!", Toast.LENGTH_SHORT).show();
                        } else if (!value1.contentEquals(value2)) {
                            Toast.makeText(getApplicationContext(), "Passwords Doesnt Match!", Toast.LENGTH_SHORT).show();
                        } else if (value1.isEmpty() || value2.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();

                        } else

                        if (InternetConnection.checkConnection(getApplicationContext())) {

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("email", userName);
                            params.put("password", value2);

                            JSONObject parameter = new JSONObject(params);
                            Log.e("JSON:", parameter.toString());

                            OkHttpClient client = new OkHttpClient();

                            okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                            Request request = new Request.Builder()
                                    .url(change_passsword_url)
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
                                        Log.e("changePasswordResponse", responseString);
                                    }
                                }

                            });
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }
/*
                        else if ((databaseHelper.checkUser(userName)) && (value1.contentEquals(value2))) {
                            fixed = value1;
                            SQLiteDatabase db = databaseHelper.getWritableDatabase();
                            db.execSQL("UPDATE " + DatabaseHelper.TABLE_USER + " SET " + DatabaseHelper.COLUMN_USER_PASSWORD + " = '" + fixed + "' WHERE " + DatabaseHelper.COLUMN_USER_EMAIL + " = ?", new String[]{userName});
                            db.close();
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "New Password Set Successfully!", Toast.LENGTH_SHORT).show();

                        }

                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                dialog.show();
*/
        }
    }

    private void verifyFromSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextUsername, textInputLayoutEmail, getString(R.string.error_message_name))) {
            return;
        }/*
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }*/
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }

        dismissKeyboard(LoginActivity.this);
        if (InternetConnection.checkConnection(getApplicationContext())) {
            Toast.makeText(activity, "Please Wait", Toast.LENGTH_SHORT).show();

            mUsername = textInputEditTextUsername.getText().toString().trim();
            String mPassword = textInputEditTextPassword.getText().toString().trim();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", mUsername);
            params.put("password", mPassword);

            JSONObject parameter = new JSONObject(params);
            Log.e("JSON:", parameter.toString());
            OkHttpClient client = new OkHttpClient();
            final RequestBody body = RequestBody.create(JSON, parameter.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("FailureResponse", call.request().body().toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        /*
                        try {
                            JSONObject jsonObj = new JSONObject(responseString);

                            // Getting JSON Array node
                            JSONArray data = jsonObj.getJSONArray("user_data");
                            JSONObject c = data.getJSONObject(0);
                            awcName = c.getString("awc");
                            checkAdmin = c.getString("id");
                            Log.e("finalAwc", awcName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        */

                        Log.e("postResponse:", responseString);
                        if (responseString.contains("Success")) {
                            try {

                                JSONObject jsonObj = new JSONObject(responseString);
                                JSONArray data = jsonObj.getJSONArray("user_data");
                                JSONObject c = data.getJSONObject(0);
                                username = c.getString("username");
                                group = c.getString("group");
                                email = c.getString("email");
                                id = c.getString("id");
                                image = c.getString("image");

                                parsedImage = image;

                                JSONArray brandDetails = jsonObj.getJSONArray("TotalBrand");
                                int totalBrands = brandDetails.length();
                                for(int i = 0; i <= totalBrands; i++){
                                    JSONObject b = brandDetails.getJSONObject(i);
                                    String brandName = b.getString("brand");
                                    String brandCount = b.getString("total");
                                    String brandColor = b.getString("color");
                                    HashMap values = new HashMap();
                                    values.put("name",brandName);
                                    values.put("count", Float.valueOf(brandCount));
                                    values.put("color", brandColor);
                                    brands.add(i,values);

                                }

                                /*
                                samsungCount =  c.getString("samsungCount");
                                huaweiCount = c.getString("huaweiCount");
                                sonyCount =  c.getString("sonyCount");
                                colorsCount = c.getString("colorsCount");
                                */

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (group.matches("5")) {
                                checkClient = true;
                                Intent field_team = new Intent(activity, FieldTeamActivity.class);
                                field_team.putExtra("userName", textInputEditTextUsername.toString());
                                getIntent().removeExtra("startTest");
                                startActivity(field_team);
                                finish();
                            } else if (group.matches("3")) {
                                checkClient = true;
                                Intent field_team = new Intent(activity, ClientActivity.class);
                                field_team.putExtra("userName", textInputEditTextUsername.toString());
                                getIntent().removeExtra("startTest");
                                startActivity(field_team);
                                finish();
                            } else {
                                Intent login = new Intent(activity, MainActivity.class);
                                login.putExtra("userName", textInputEditTextUsername.toString());
                                getIntent().removeExtra("startTest");
                                startActivity(login);
                                finish();
                            }

                            if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                                ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS);
                            }


                        } else
                            Snackbar.make(root, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                        // emptyInputEditText();
                    }
                }
            });

        } else {
            Toast.makeText(LoginActivity.this, "Sorry internet connection is required.", Toast.LENGTH_SHORT).show();
        }
/*
        if (databaseHelper.checkUser(textInputEditTextUsername.getText().toString().trim()
                , textInputEditTextPassword.getText().toString().trim())) {

            getUserName = textInputEditTextUsername.getText().toString().trim();
            //session.createLoginSession(textInputEditTextEmail.getText().toString().trim(), textInputEditTextPassword.getText().toString().trim());
            Intent accountsIntent = new Intent(activity, MainActivity.class);
            Toast.makeText(this, "Logged in as" + " " + textInputEditTextUsername.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            getIntent().removeExtra("startTest");
            startActivity(accountsIntent);
            finish();

        }
        */
    }

    private void emptyInputEditText() {
        textInputEditTextUsername.setText(null);
        textInputEditTextPassword.setText(null);
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

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }
}
