package com.digitalelimu.app;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.digitalelimu.app.models.Application;
import com.digitalelimu.app.models.Term;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TermActivity extends AppCompatActivity {
    private static final String TAG = "My DEBUG TAG";
    View bookView;
    String phone = "";
    String accesstoken = "";
    String bookid = "";
    PopupWindow mPopupWindow;
    ImageButton iBtn;
    String bookname;
    LinearLayout mRelativeLayout;
    ProgressDialog dialog;
    BookAdapter mAdapter;
    ArrayList<Term> myArray = new ArrayList<Term>();
    RecyclerView recyclerView;
    Typeface tfr,tfb,tfm;
    AdView mAdView;
    NativeExpressAdView adNative;
    Button pdf;
    RequestQueue qued;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdapter = new BookAdapter(this, myArray);

        //mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        //adNative = (NativeExpressAdView) findViewById(R.id.nativeAd);
        //AdRequest adRequest2 = new AdRequest.Builder().build();
        //adNative.loadAd(adRequest2);

        tfr = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
        tfb = Typeface.createFromAsset(getAssets(),"Raleway-Bold.ttf");
        tfm = Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedData = getSharedPreferences("userInfo", MODE_PRIVATE);
        phone = sharedData.getString("phone","");
        accesstoken = sharedData.getString("accesstoken","");
        bookid = getIntent().getStringExtra("bookid");
        bookname = getIntent().getStringExtra("bookname");
        setTitle(bookname+ " (Terms)");

        pdf = (Button) findViewById(R.id.pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                Intent intent = new Intent(TermActivity.this, PdfActivity.class);
                intent.putExtra("bookid",bookid);
                intent.putExtra("bookname",bookname);
                startActivity(intent);
            }
        });

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mRelativeLayout = (LinearLayout) findViewById(R.id.content_term);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewterm);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Application.domain)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Auth service = retrofit.create(Auth.class);

        Call<ArrayList<Term>> call = service.authTerm(phone,accesstoken,bookid);
        call.enqueue(new Callback<ArrayList<Term>>() {
            @Override
            public void onResponse(Call<ArrayList<Term>> call, Response<ArrayList<Term>> response) {
                //Toast.makeText(TermActivity.this, "Return success", Toast.LENGTH_SHORT).show();
                if (response.body() != null && !response.body().isEmpty()){
                    for (Term term:response.body()) {
                        myArray.add(term);
                        mAdapter.notifyDataSetChanged();
                    }
                    dialog.hide();
                }else{
                    Toast.makeText(TermActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                    dialog.hide();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Term>> call, Throwable t) {

            }
        });

        recyclerView.setAdapter(mAdapter);
        String url = Application.domain+"/api/getPdfs/"+phone+"/"+accesstoken+"/"+bookid;
        qued = Volley.newRequestQueue(this);

        final LinearLayout btnlayout = (LinearLayout)findViewById(R.id.btnLayout);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                //JSONObject topicObj = (JSONObject) response;
                    //Toast.makeText(TermActivity.this, response.getString("pdf").toString(), Toast.LENGTH_LONG).show();
                    String[] parts = response.getString("pdf").toString().split(",");
                    for (int i = 0;i < parts.length;i++){
                        //Toast.makeText(TermActivity.this, parts[i].toString(), Toast.LENGTH_SHORT).show();
                        Button btn = new Button(TermActivity.this);
                        btn.setId(i);
                        btn.setTag(parts[i].toString());
                        btn.setBackgroundColor(Color.parseColor("#DB125160"));
                        btn.setTextColor(Color.parseColor("#fdfdfd"));
                        btn.setText("Chapter "+i+" (" +parts[i].toString()+")");
                        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        btnlayout.addView(btn);
                        Button btn1 = new Button(TermActivity.this);
                        btn1 = ((Button) findViewById(i));
                        final Button finalBtn = btn1;
                        btn1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent intent = new Intent(TermActivity.this, ReadpdfActivity.class);
                                intent.putExtra("bookid",bookid);
                                intent.putExtra("bookname",bookname);
                                intent.putExtra("pdf",finalBtn.getTag().toString());
                                intent.putExtra("chapter",finalBtn.getId());
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.hide();
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Toast toast = Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        });


        qued.add(request);
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
            case R.id.menusearch:
                Intent intents = new Intent(TermActivity.this, SearchActivity.class);
                startActivity(intents);
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
            case R.id.subscriptions:
                Intent intent = new Intent(TermActivity.this, SubscribeActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("accesstoken", accesstoken);
                startActivity(intent);
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
        public TermActivity.BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflator = LayoutInflater.from(context);

            bookView = inflator.inflate(R.layout.activity_list_term,parent,false);
            TermActivity.BookAdapter.ViewHolder adapter = new TermActivity.BookAdapter.ViewHolder(bookView);
            return adapter;
        }

        @Override
        public void onBindViewHolder(TermActivity.BookAdapter.ViewHolder holder, int position) {
            final Term topics = subtopics.get(position);
            holder.bookName.setText("Term " +subtopics.get(position).getTerm());
            holder.bookName.setTypeface(tfm);
            holder.details.setText(subtopics.get(position).getWeeks()+" Weeks");
            bookView.setTag(R.id.idBook, subtopics.get(position).getId());

            bookView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    //Check if subscribed
                    dialog.show();
                    Intent intent = new Intent(TermActivity.this, WeekActivity.class);
                    intent.putExtra("bookid",topics.getBookId());
                    intent.putExtra("bookname",bookname);
                    intent.putExtra("term",topics.getTerm());
                    startActivity(intent);
                }
            });

            /**/
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
            public TextView details;
            public ViewHolder(View itemView) {
                super(itemView);
                bookName = (TextView) itemView.findViewById(R.id.bookname);
                details = (TextView) itemView.findViewById(R.id.details);
            }
        }
    }
}
