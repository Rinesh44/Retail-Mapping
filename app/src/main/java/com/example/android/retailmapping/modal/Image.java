package com.example.android.retailmapping.modal;

/**
 * Created by Shaakya on 11/24/2017.
 */

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by Lincoln on 04/04/16.
 */
public class Image implements Serializable{
    private byte[] medium;

    public Image() {
    }

    public Image(byte[] medium) {
        this.medium = medium;
    }

    public byte[] getMedium() {
        return medium;
    }

    public void setMedium(byte[] medium) {
        this.medium = medium;
    }

}
