package com.example.tylerpfaff.chip;

import android.graphics.drawable.Drawable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by tylerpfaff on 8/23/16.
 */

public interface ChipotleService {

    @GET("barcode")
    Call<ResponseBody> getQRCode(@Header("Referrer") String referrer,
                                 @Query("token") String token,
                                 @Query("scale") int scale);

}
