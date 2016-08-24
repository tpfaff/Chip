package com.example.tylerpfaff.chip;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedInputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();

    final String referrer = "https://chipotle.com/chiptopia";
    final String prefsName = "CHIP_PREFS";
    int scale = 8;

    SharedPreferences sharedPreferences;

    WebView webView;
    ImageView qrView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8977189469228943~2428085784");

        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("7526E51362799004B14B46B7812549C3").build();
        mAdView.loadAd(adRequest);

        sharedPreferences = getSharedPreferences(prefsName,MODE_PRIVATE);


        qrView = (ImageView)findViewById(R.id.qr_image_view);
        webView = (WebView)findViewById(R.id.web_view);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        if(sharedPreferences.getString("token",null) == null){
            initWebView();
            getSupportActionBar().setTitle("Login");
        }else{
            webView.setVisibility(View.GONE);
            loadQRCode();
        }


    }

    private void initWebView(){

        CookieManager.getInstance().removeAllCookie();
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.d(TAG,"< "+request.getRequestHeaders());
                if(request.getUrl().getHost().equals("chiptopia-api.chipotle.com")){
                    if(request.getUrl().getPath().equals("/barcode")){
                        if(request.getUrl().getQueryParameterNames().contains("token") && request.getUrl().getQueryParameterNames().contains("scale")){
                            sharedPreferences.edit().putString("token",request.getUrl().getQueryParameter("token")).commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webView.setVisibility(View.GONE);
                                    loadQRCode();
                                }
                            });
                        }

                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        webView.loadUrl("https://chipotle.com/chiptopia");
    }

    private void loadQRCode(){

        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        if(webView.getVisibility() == View.GONE){
            progressBar.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://chiptopia-api.chipotle.com/")
                .build();

        ChipotleService service = retrofit.create(ChipotleService.class);

        String token = sharedPreferences.getString("token",null);

        Call<ResponseBody> call = service.getQRCode(referrer,token,scale);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG,"SUCCESS!");
                BitmapDrawable drawable = new BitmapDrawable(response.body().byteStream());
                qrView.setImageDrawable(drawable);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"FAIL!");
                Toast.makeText(MainActivity.this,t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
