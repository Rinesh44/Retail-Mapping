package com.example.android.retailmapping;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.retailmapping.Interface.ApiInterface;
import com.example.android.retailmapping.adapter.GalleryAdapter;
import com.example.android.retailmapping.app.AppController;
import com.example.android.retailmapping.modal.Image;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class OnShop extends AppCompatActivity {
    Toolbar toolbar;
    String getShopId;
    private ArrayList<Image> images;
    private String TAG = InShop.class.getSimpleName();
    private ProgressDialog progressDialog;
    public static String post_url = "http://pagodalabs.com.np/retailmapping/comment/api/comment";
    public static String url = "http://pagodalabs.com.np/retailmapping/onshop/api/onshop/";
    public static String base_url = "http://pagodalabs.com.np/";
    ArrayList<HashMap<String, String>> shopImage;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private static final int PICK_IMAGE1 = 987;
    String fileName;
    private TextView mEmptyStateTextView;
    String encodedImage;
    private static final int REQUEST_STORAGE_ACCESS = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Call<ResponseBody> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_shop);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_STORAGE_ACCESS);
        }

        Window window = OnShop.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(OnShop.this.getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        Intent i = getIntent();
        getShopId = i.getStringExtra("shop_id");


        if (InternetConnection.checkConnection(OnShop.this)) {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(OnShop.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    void run() throws IOException {
        progressDialog = new ProgressDialog(OnShop.this);
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

        call = retrofit.create(ApiInterface.class).getResponse(url + getShopId);

        if (call.isExecuted())
            call = call.clone();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String myResponse = response.body().string();
                    Log.e("getOnShopResponse:", myResponse);
                    progressDialog.hide();

                    images.clear();
                    JSONObject jsonObject = new JSONObject(myResponse);
                    JSONArray data = jsonObject.getJSONArray("Onshop_Photo");

                    for (int i = 0; i < data.length(); i++) {

                        JSONObject object = data.getJSONObject(i);
                        Image image = new Image();

                        String shop_image = object.getString("onshop_image");

                        byte[] decodedString = Base64.decode(shop_image, Base64.DEFAULT);
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        image.setMedium(stream.toByteArray());
                        images.add(image);

                    }
                }catch (IOException | JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Furniture", t.toString());
                Toast.makeText(OnShop.this, R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                break;
            }

            case R.id.upload: {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE1);
                break;

            }

            case R.id.comment: {
                final Dialog comment = new Dialog(OnShop.this);
                comment.getWindow();
                comment.requestWindowFeature(Window.FEATURE_NO_TITLE);
                comment.setContentView(R.layout.comment);
                comment.show();

                Button ok = (Button) comment.findViewById(R.id.btn);
                final EditText commentText = (EditText) comment.findViewById(R.id.name);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (InternetConnection.checkConnection(getApplicationContext())) {
                            String getCommentText = commentText.getText().toString().trim();
                            String userId = LoginActivity.id;
                            String shopId = getShopId;

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("comment", getCommentText);
                            params.put("comment_by", userId);
                            params.put("shop_id", shopId);
                            params.put("category", "onshop");

                            JSONObject parameter = new JSONObject(params);
                            Log.e("JSON:", parameter.toString());

                            OkHttpClient client = new OkHttpClient();
                            okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                            okhttp3.Request request = new okhttp3.Request.Builder()
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
                                        Log.e("onShopCommentResponse", responseString);
                                    }
                                }

                            });

                            Toast.makeText(OnShop.this, "Comment Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(OnShop.this, "Unable to comment. No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;  //you can also calculate your inSampleSize
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];
        if (requestCode == PICK_IMAGE1) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    if (uri.toString().startsWith("file:")) {
                        fileName = uri.getPath();
                    } else { // uri.startsWith("content:")

                        Cursor c = getContentResolver().query(uri, null, null, null, null);

                        if (c != null && c.moveToFirst()) {

                            int id = c.getColumnIndex(MediaStore.Images.Media.DATA);
                            if (id != -1) {
                                fileName = c.getString(id);
                            }
                        }
                    }
                }


                Bitmap bm = BitmapFactory.decodeFile(fileName, options);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                bm.recycle();
            }

            if (InternetConnection.checkConnection(getApplicationContext())) {

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("onshop_image", encodedImage);
                params.put("shop_id", getShopId);

                JSONObject parameter = new JSONObject(params);
                Log.e("JSON:", parameter.toString());

                OkHttpClient client = new OkHttpClient();
                okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, parameter.toString());
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url + getShopId)
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
                            Log.e("onShopImageResponse", responseString);
                        }
                    }

                });

                Toast.makeText(OnShop.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(OnShop.this, "Unable to upload. No internet connection", Toast.LENGTH_SHORT).show();
            }


        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LoginActivity.checkClient)
        getMenuInflater().inflate(R.menu.client_gallery_overflow, menu);
        else
        getMenuInflater().inflate(R.menu.gallery_overflow, menu);
        return true;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
