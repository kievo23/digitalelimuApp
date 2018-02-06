package com.digitalelimu.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalelimu.app.models.Application;
import com.digitalelimu.app.models.Term;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LessonActivity extends AppCompatActivity {
    private static final String TAG = "My DEBUG TAG";
    View bookView;
    String phone = "";
    String accesstoken = "";
    String bookid = "";
    PopupWindow mPopupWindow;
    ImageButton iBtn;
    String bookname;
    RelativeLayout mRelativeLayout;
    ProgressDialog dialog;
    BookAdapter mAdapter;
    String term,week = "";
    ArrayList<Term> myArray = new ArrayList<Term>();
    RecyclerView recyclerView;
    Typeface tfr,tfb,tfm;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdapter = new BookAdapter(this, myArray);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        tfr = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
        tfb = Typeface.createFromAsset(getAssets(),"Raleway-Bold.ttf");
        tfm = Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedData = getSharedPreferences("userInfo", MODE_PRIVATE);
        phone = sharedData.getString("phone","");
        accesstoken = sharedData.getString("accesstoken","");
        bookid = getIntent().getStringExtra("bookid");
        term = getIntent().getStringExtra("term");
        bookname = getIntent().getStringExtra("bookname");
        week = getIntent().getStringExtra("week");
        setTitle(bookname+ " (Lessons)");

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mRelativeLayout = (RelativeLayout) findViewById(R.id.content_week);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewweek);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Application.domain)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Auth service = retrofit.create(Auth.class);
        Call<ArrayList<Term>> call = service.authLesson(phone,accesstoken,bookid,term,week);
        call.enqueue(new Callback<ArrayList<Term>>() {
            @Override
            public void onResponse(Call<ArrayList<Term>> call, Response<ArrayList<Term>> response) {
                //Toast.makeText(LessonActivity.this, "Return success", Toast.LENGTH_SHORT).show();
                if (response.body() != null && !response.body().isEmpty()){
                    for (Term term:response.body()) {
                        myArray.add(term);
                        mAdapter.notifyDataSetChanged();
                    }
                    dialog.hide();
                }else{
                    Toast.makeText(LessonActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                    dialog.hide();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Term>> call, Throwable t) {

            }
        });
        recyclerView.setAdapter(mAdapter);
    }

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
                Intent intent = new Intent(LessonActivity.this, SubscribeActivity.class);
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
    class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> implements View.OnClickListener {
        private Context mcontext;
        private ArrayList<Term> subtopics;

        public BookAdapter(Context context, ArrayList<Term> subtopics) {
            this.mcontext = context;
            this.subtopics = subtopics;
        }

        @Override
        public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflator = LayoutInflater.from(context);

            bookView = inflator.inflate(R.layout.activity_list_lesson,parent,false);
            BookAdapter.ViewHolder adapter = new BookAdapter.ViewHolder(bookView);
            return adapter;
        }

        @Override
        public void onBindViewHolder(BookAdapter.ViewHolder holder, final int position) {
            final Term topics = subtopics.get(position);
            holder.bookName.setText("Lesson " +subtopics.get(position).getLesson());
            holder.bookName.setTypeface(tfm);
            holder.audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(subtopics.get(position).getAudio() != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(Application.domain+"uploads/audio/"+subtopics.get(position).getAudio()));
                        startActivity(intent);
                    }else {
                        Toast.makeText(mcontext, "No Audio", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(subtopics.get(position).getVideo() != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(Application.domain+"uploads/video/"+subtopics.get(position).getVideo()));
                        startActivity(intent);
                    }else {
                        Toast.makeText(mcontext, "No Video", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.paper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LessonActivity.this, ContentActivity.class);
                    intent.putExtra("bookid",topics.getBookId());
                    intent.putExtra("term",topics.getTerm());
                    intent.putExtra("week",topics.getWeek());
                    intent.putExtra("lesson",topics.getLesson());
                    intent.putExtra("bookname",bookname);
                    startActivity(intent);
                }
            });
            bookView.setTag(R.id.idBook, subtopics.get(position).getId());

            bookView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    //Check if subscribed
                    dialog.show();
                    Intent intent = new Intent(LessonActivity.this, ContentActivity.class);
                    intent.putExtra("bookid",topics.getBookId());
                    intent.putExtra("term",topics.getTerm());
                    intent.putExtra("week",topics.getWeek());
                    intent.putExtra("lesson",topics.getLesson());
                    intent.putExtra("bookname",bookname);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return subtopics.size();
            //return 0;
        }

        @Override
        public void onClick(View view) {

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView bookName;
            public ImageButton paper;
            public ImageButton audio;
            public ImageButton video;
            public ViewHolder(View itemView) {
                super(itemView);
                bookName = (TextView) itemView.findViewById(R.id.bookname);
                paper = (ImageButton) itemView.findViewById(R.id.paper);
                audio = (ImageButton) itemView.findViewById(R.id.audio);
                video = (ImageButton) itemView.findViewById(R.id.video);
            }
        }
    }
}

