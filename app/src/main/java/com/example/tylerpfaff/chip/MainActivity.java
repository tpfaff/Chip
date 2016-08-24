package com.example.tylerpfaff.chip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();

    String referrer = "https://chipotle.com/chiptopia-barcode?barcode=eyJtZW1iZXIiOiJUeWxlciBQZmFmZiIsImltYWdlIjoiaHR0cHM6Ly9jaGlwdG9waWEtYXBpLmNoaXBvdGxlLmNvbS9iYXJjb2RlP3Rva2VuPWJlNmEzMjRiOGZjOTllOTQ4YTAxMzkxMDgwZjgzZjFmZGE4ZjZhZTNmYjU5ZmM3ODFmMTg2ZTliNTkyZjE1OTk5YjUzMjkyMzExY2UyMjJkNDE0NzMwOTVkMjlkNjgyOTIyYjgwYzQ4YWFiODgyNzRkMmFkY2ZmMTBhMDY2NDVkJnNjYWxlPTgiLCJjb2RlIjoiODAyMTEgMTUzNzI1NTgwIn0=";
    String token = "be6a324b8fc99e948a01391080f83f1fda8f6ae3fb59fc781f186e9b592f15999b53292311ce222d41473095d29d682922b80c48aab88274d2adcff10a06645d";
    int scale = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView qrView = (ImageView)findViewById(R.id.qr_image_view);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://chiptopia-api.chipotle.com/")
                .build();

        ChipotleService service = retrofit.create(ChipotleService.class);

        Call<ResponseBody> call = service.getQRCode(referrer,token,scale);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG,"SUCCESS!");
                BitmapDrawable drawable = new BitmapDrawable(response.body().byteStream());
                qrView.setImageDrawable(drawable);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"FAIL!");
            }
        });
    }
}
