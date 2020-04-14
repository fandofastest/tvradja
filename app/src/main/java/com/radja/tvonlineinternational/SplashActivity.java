package com.radja.tvonlineinternational;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.radja.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends AppCompatActivity {

    MyApplication App;
    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 2000;
    public static String statususer,banner,inter;
    public static String defaultimage="https://fando.xyz/tvku.jpg";
     InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        App = MyApplication.getInstance();

        getStatusapp(Constant.SERVER_URL+"getstatus.php");

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (!mIsBackButtonPressed) {
//                    if (App.getIsLogin()) {
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }
//
//        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();

    }


    private void getStatusapp(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
//                    JSONObject jsonObject=response.getJSONObject("status");
                       statususer = response.getString("status");
                       banner = response.getString("banner");
                       inter=response.getString("inter");

                    Button button= findViewById(R.id.buttonstart);
                    ProgressBar progressBar =findViewById(R.id.progressbar);
                    progressBar.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);

                    button.setOnClickListener(view -> showinter());













                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }


    public  void showinter() {

        Button button= findViewById(R.id.buttonstart);
        ProgressBar progressBar =findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                kehome();

                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                kehome();
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }

    public  void kehome(){
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
    }



}
