package com.digitalelimu.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.digitalelimu.app.models.Application;

public class PdfActivity extends AppCompatActivity {
    String phone = "";
    String accesstoken = "";
    ProgressDialog dialog;
    String bookid = "";
    String term,week,bookname,lesson = "";

    @Override
    protected void onResume() {
        super.onResume();
        dialog.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menusearch:
                Intent intents = new Intent(PdfActivity.this, SearchActivity.class);
                startActivity(intents);
                break;
            case R.id.subscriptions:
                Intent intent = new Intent(PdfActivity.this, SubscribeActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("accesstoken", accesstoken);
                startActivity(intent);
                break;
            case R.id.logout:
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.remove("phone");
                edit.remove("accesstoken");
                edit.commit();
                finish();
                startActivity(getIntent());
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_pdf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        SharedPreferences sharedData = getSharedPreferences("userInfo", MODE_PRIVATE);
        phone = sharedData.getString("phone","");
        accesstoken = sharedData.getString("accesstoken","");
        bookid = getIntent().getStringExtra("bookid");
        bookname = getIntent().getStringExtra("bookname");

        setTitle(bookname+" PDF");

        final WebView webView = (WebView) this.findViewById(R.id.webViewPdf);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(12);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(getApplicationContext(),"Long Click Disabled",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        webView.setLongClickable(false);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.show();
            }

            public void onPageFinished(WebView webview, String url) {
                dialog.hide();
            }
        });
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Application.domain)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Auth service = retrofit.create(Auth.class);*/

        // make sure your pinch zoom is enabled
        webView.getSettings().setBuiltInZoomControls(true);

        // show the zoom controls
        webView.getSettings().setDisplayZoomControls(true);
        webView.loadUrl(Application.domain+"api/pdf/"+phone+"/"+accesstoken+"/"+bookid);
    }
}
