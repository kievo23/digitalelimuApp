package com.digitalelimu.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.digitalelimu.app.models.Application;

import static java.lang.Integer.parseInt;

public class ContentActivity extends AppCompatActivity {
    String phone = "";
    String accesstoken = "";
    String bookid = "";
    String term,week,bookname,lesson = "";
    ProgressDialog dialog;
    Integer nextLesson,previousLesson;
    Typeface tfr,tfb,tfm;
    float initialX, initialY;

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
            case R.id.subscriptions:
                Intent intent = new Intent(ContentActivity.this, SubscribeActivity.class);
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
        setContentView(R.layout.activity_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tfr = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
        tfb = Typeface.createFromAsset(getAssets(),"Raleway-Bold.ttf");
        tfm = Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");

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
        term = getIntent().getStringExtra("term");
        bookname = getIntent().getStringExtra("bookname");
        week = getIntent().getStringExtra("week");
        lesson = getIntent().getStringExtra("lesson");
        nextLesson = Integer.parseInt(lesson)+1;
        previousLesson = Integer.parseInt(lesson)-1;
        if(previousLesson <= 0) {
            previousLesson = 0;
        }

        ImageButton fabLeft = (ImageButton) findViewById(R.id.left);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("bookid",bookid);
                intent.putExtra("term",term);
                intent.putExtra("week",week);
                intent.putExtra("lesson",previousLesson.toString());
                intent.putExtra("bookname",bookname);
                startActivity(intent);
            }
        });

        ImageButton fabRight = (ImageButton) findViewById(R.id.right);
        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("bookid",bookid);
                intent.putExtra("term",term);
                intent.putExtra("week",week);
                intent.putExtra("lesson",nextLesson.toString());
                intent.putExtra("bookname",bookname);
                finish();
                startActivity(getIntent());
            }
        });

        Button quit = (Button) findViewById(R.id.quit);
        quit.setTypeface(tfm);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        Button home = (Button) findViewById(R.id.home);
        home.setTypeface(tfm);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, WeekActivity.class);
                intent.putExtra("bookid",bookid);
                intent.putExtra("bookname",bookname);
                intent.putExtra("term",term);
                startActivity(intent);
            }
        });

        Button back = (Button) findViewById(R.id.back);
        back.setTypeface(tfm);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, LessonActivity.class);
                intent.putExtra("bookid",bookid);
                intent.putExtra("bookname",bookname);
                intent.putExtra("term",term);
                intent.putExtra("week",week);
                startActivity(intent);
            }
        });

        setTitle(bookname+ " (Lesson)");

        final WebView webView = (WebView) this.findViewById(R.id.webView);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(12);
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

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                //Log.d("EVENT", String.valueOf(action));
                //Toast.makeText(ContentActivity.this, String.valueOf(action), Toast.LENGTH_SHORT).show();
                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();

                    case MotionEvent.ACTION_UP:
                        float finalX = event.getX();
                        float finalY = event.getY();

                        if (initialX < finalX  && (finalX - initialX) > 100) {
                            //Toast.makeText(ContentActivity.this, "Left to Right swipe performed", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            intent.putExtra("bookid",bookid);
                            intent.putExtra("term",term);
                            intent.putExtra("week",week);
                            intent.putExtra("lesson",previousLesson.toString());
                            intent.putExtra("bookname",bookname);
                            startActivity(intent);
                            return false;
                        }

                        if (initialX > finalX && (initialX - finalX) > 100 ) {
                            //Toast.makeText(ContentActivity.this, "Right to Left swipe performed", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            intent.putExtra("bookid",bookid);
                            intent.putExtra("term",term);
                            intent.putExtra("week",week);
                            intent.putExtra("lesson",nextLesson.toString());
                            intent.putExtra("bookname",bookname);
                            finish();
                            startActivity(getIntent());
                            return false;
                        }

                        if (initialY < finalY && (finalY - initialY) > 100) {
                            //Toast.makeText(ContentActivity.this, "Up to Down swipe performed", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        if (initialY > finalY && (initialY - finalY) > 100) {
                            //Toast.makeText(ContentActivity.this, "Down to Up swipe performed", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        break;
/*
                    case MotionEvent.ACTION_CANCEL:
                        Toast.makeText(ContentActivity.this, "Action was CANCEL", Toast.LENGTH_SHORT).show();
                        break;

                    case MotionEvent.ACTION_OUTSIDE:
                        Toast.makeText(ContentActivity.this, "Movement occurred outside bounds of current screen element", Toast.LENGTH_SHORT).show();
                        break;
                        */
                }
                return false;
            };
        });

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://books.academicsage.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Auth service = retrofit.create(Auth.class);
        Call<Content> call = service.authContent(phone,accesstoken,bookid,term,week,lesson);
        call.enqueue(new Callback<Content>() {
            @Override
            public void onResponse(Call<Content> call, retrofit2.Response<Content> response) {
                Content content = response.body();
                webView.loadData(content.getDetails().toString(),"text/html",null);
                dialog.hide();
            }

            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                dialog.hide();
            }
        });*/
        //webView.loadUrl("https://www.google.com/");
        // make sure your pinch zoom is enabled
        webView.getSettings().setBuiltInZoomControls(true);

        // show the zoom controls
        webView.getSettings().setDisplayZoomControls(true);
        webView.loadUrl(Application.domain+"api/getContent/"+phone+"/"+accesstoken+"/"+bookid+"/"+term+"/"+week+"/"+lesson);
    }

}
