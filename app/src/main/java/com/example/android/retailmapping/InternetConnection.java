package com.example.android.retailmapping;

 import android.content.Context;
 import android.net.ConnectivityManager;

 /**
 * Created by Shaakya on 7/19/2017.
 */

public class InternetConnection {

    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(Context context) {
        return  ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}