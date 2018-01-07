package com.example.android.retailmapping;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class Profile extends AppCompatActivity {
    static ImageView userImage;
    ImageView background;
    private TextView userName, name, email, group;
    Toolbar toolbar;
    AppCompatButton changePassword;
    public static final String change_password_url = "http://pagodalabs.com.np/retailmapping/auth/api/passwordChange";
    String imageDir, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        background = (ImageView) findViewById(R.id.select);

        userImage = (ImageView) findViewById(R.id.userImage);
        userName = (TextView) findViewById(R.id.userName);
        name = (TextView) findViewById(R.id.valueName);
        email = (TextView) findViewById(R.id.valueEmail);
        group = (TextView) findViewById(R.id.valueGroup);
        changePassword = (AppCompatButton) findViewById(R.id.changePassword);

        imageDir = LoginActivity.username;
        userId = LoginActivity.id;

        File imageFile = new File(Environment.getExternalStorageDirectory().getPath() + "/RetailMapping/" + imageDir + ".jpg");
/*
        if(NavigationDrawer.bitmap != null){
            userImage.setImageBitmap(NavigationDrawer.bitmap);
        }else if (imageFile.exists()) {

            String filePath = imageFile.getPath();
            Bitmap loadBitmap = BitmapFactory.decodeFile(filePath);
            userImage.setImageBitmap(loadBitmap);
            Log.e("checkImage", "yes it exists");
        } else {

            if (NavigationDrawer.thumbnail != null)
                userImage.setImageBitmap(NavigationDrawer.thumbnail);
            else

                userImage.setImageDrawable(getResources().getDrawable(R.drawable.account));

        }
        */
        if (NavigationDrawer.bitmap != null)
            userImage.setImageBitmap(NavigationDrawer.bitmap);
        else
            userImage.setImageDrawable(getResources().getDrawable(R.drawable.account_light));
/*
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        User user = dbHelper.getUser(LoginActivity.getUserName);

        userName.setText(user.getName());
        name.setText(user.getName());
        email.setText(user.getEmail());
        group.setText(user.getGroup());
        */

        userName.setText(LoginActivity.username);
        name.setText(LoginActivity.username);
        email.setText(LoginActivity.email);
        if (LoginActivity.group.matches("5"))
            group.setText("Field Team");
        else if (LoginActivity.group.matches("2"))
            group.setText("Administrator");
        else if (LoginActivity.group.matches("3"))
            group.setText("Client");
        else
            group.setText("None");


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Profile.this);
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
                            Toast.makeText(Profile.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();

                        } else if (InternetConnection.checkConnection(getApplicationContext())) {

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            Map<String, Object> params = new HashMap<String, Object>();
                            //params.put("email", userName);
                            params.put("password", value2);
                            params.put("id", userId);

                            JSONObject parameter = new JSONObject(params);
                            Log.e("JSON:", parameter.toString());

                            OkHttpClient client = new OkHttpClient();

                            okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                            Request request = new Request.Builder()
                                    .url(change_password_url)
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
                            dialog.dismiss();


                        } else {
                            Toast.makeText(Profile.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }

                        /*else if ((databaseHelper.checkUser(userName)) && (value1.contentEquals(value2))) {
                            fixed = value1;
                            SQLiteDatabase db = databaseHelper.getWritableDatabase();
                            db.execSQL("UPDATE " + DatabaseHelper.TABLE_USER + " SET " + DatabaseHelper.COLUMN_USER_PASSWORD + " = '" + fixed + "' WHERE " + DatabaseHelper.COLUMN_USER_EMAIL + " = ?", new String[]{userName});
                            db.close();
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "New Password Set Successfully!", Toast.LENGTH_SHORT).show();

                        }
                        */
                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

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
