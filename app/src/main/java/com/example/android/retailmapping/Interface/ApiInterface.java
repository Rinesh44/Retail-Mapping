package com.example.android.retailmapping.Interface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Shaakya on 11/1/2017.
 */

public interface ApiInterface {

    @GET
    Call<ResponseBody> getResponse(@Url String url);
}
