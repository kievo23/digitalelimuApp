package com.digitalelimu.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.digitalelimu.app.models.Application;
import com.digitalelimu.app.models.OAuthBook;
import com.digitalelimu.app.models.ReadBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookActivity extends AppCompatActivity {

    private static final String TAG = "My DEBUG TAG";
    BookAdapter mAdapter;
    ImageLoader mImageLoader;
    RecyclerView recyclerView;
    ArrayList<SubTopics> myArray = new ArrayList<SubTopics>();
    View bookView;
    String phone = "";
    String accessToken = "";
    String safAmount,walletamt,loadwalletamt,walletamtbal;
    PopupWindow mPopupWindow,imagePopup;
    LinearLayout mRelativeLayout;
    ProgressDialog dialog;
    Button contClose,airtel,stkContinue,stkpush,loadwalletbtn,classbtn,closeclassbtn;
    ImageView imageCover;
    Typeface tfr,tfb,tfm;
    WebView webView;
    AlertDialog dialogap;
    EditText amount,walletamount,loadwalletamout;
    String books;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menusearch:
                Intent intents = new Intent(BookActivity.this, SearchActivity.class);
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
                Intent intent = new Intent(BookActivity.this, SubscribeActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("accesstoken", accessToken);
                startActivity(intent);
                break;
            case R.id.login:
                Intent intentg = new Intent(BookActivity.this, RegisterActivity.class);
                startActivity(intentg);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void importImage(String urlPhoto, final ImageView imageView){
        ImageRequest imageRequest = new ImageRequest(urlPhoto,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        SingleTone.getInstance(getApplicationContext()).getRequestQue().add(imageRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final String urlId = intent.getStringExtra("CLASSID");
        String topic = intent.getStringExtra("CLASSNAME");
        getIntent().setAction("Already created");
        TextView username = (TextView) findViewById(R.id.username);
        AndroidNetworking.initialize(getApplicationContext());

        tfr = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
        tfb = Typeface.createFromAsset(getAssets(),"Raleway-Bold.ttf");
        tfm = Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");

        SharedPreferences sharedData = getSharedPreferences("userInfo", MODE_PRIVATE);
        phone = sharedData.getString("phone","");
        accessToken = sharedData.getString("accesstoken","");
        if(phone != ""){
            setTitle(topic);
            username.setText(sharedData.getString("phone",""));
        }else{
            setTitle(topic);
        }

        mRelativeLayout = (LinearLayout) findViewById(R.id.content_book);
        //mRelativeLayout.setBackgroundColor(262728);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //new StaggeredGridLayoutManager.LayoutParams();
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new BookAdapter(this, myArray);

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if(phone.equals("")){
            phone = "nophone";
        }

        String url = Application.domain+"api/getBooks/"+urlId+"/"+phone;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    walletamtbal = response.getString("walletbal").toString();
                    //Toast.makeText(BookActivity.this, walletamtbal, Toast.LENGTH_SHORT).show();
                    JSONArray jsonArrayBooks = new JSONArray(response.getString("books"));
                    for(int i = 0;i < jsonArrayBooks.length();i++){
                        try {
                            JSONObject topicObj = (JSONObject) jsonArrayBooks.get(i);
                            String id = topicObj.getString("id");
                            String sub_topic = topicObj.getString("name");
                            String photo = topicObj.getString("photo");
                            String description = topicObj.getString("description");
                            String lessons = topicObj.getString("lessons");

                            SubTopics newBook = new SubTopics(id,sub_topic,photo,description,lessons);
                            myArray.add(newBook);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    dialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d(TAG, error.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //qued.add(request);
        SingleTone.getInstance(BookActivity.this).getRequestQue().add(request);
        recyclerView.setAdapter(mAdapter);

        classbtn = (Button) findViewById(R.id.classbtn);
        classbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone == "nophone"){
                    //Toast.makeText(getApplicationContext(), view.getTag(R.id.idBook).toString(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BookActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder mbuilder = new AlertDialog.Builder(BookActivity.this);
                    View classpopupView = getLayoutInflater().inflate(R.layout.classpopup, null);
                    mbuilder.setView(classpopupView);
                    dialogap = mbuilder.create();
                    dialogap.show();

                    closeclassbtn = (Button) classpopupView.findViewById(R.id.close);
                    closeclassbtn.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                             dialogap.cancel();
                         }
                    });


                    TextView txt = (TextView) classpopupView.findViewById(R.id.howtopay);
                    TextView paybill = (TextView) classpopupView.findViewById(R.id.paybill);
                    txt.setTypeface(tfm);
                    paybill.setTypeface(tfm);
                    mPopupWindow = new PopupWindow(classpopupView, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);
                    contClose = (Button) classpopupView.findViewById(R.id.close);
                    contClose.setTypeface(tfm);
                    contClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogap.cancel();
                            dialog.hide();
                        }
                    });

                    stkpush = (Button) classpopupView.findViewById(R.id.csafPush);
                    amount = (EditText) classpopupView.findViewById(R.id.safamount);
                    stkpush.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            safAmount = amount.getText().toString();
                            if (safAmount.isEmpty()) {
                                Toast.makeText(BookActivity.this, "Price Should Not Be Empty", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(safAmount) < 25) {
                                Toast.makeText(BookActivity.this, "Price Should Not Be Less Than 25 Bob", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BookActivity.this, "Please wait a moment", Toast.LENGTH_LONG).show();
                                AndroidNetworking.post(Application.domain + "api/stkpushclass")
                                        .addBodyParameter("phone", "254" + phone.substring(phone.length() - 9, phone.length()))
                                        .addBodyParameter("classid", urlId)
                                        .addBodyParameter("amount", safAmount)
                                        .setTag("test")
                                        .setPriority(Priority.HIGH)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // do anything with response
                                                try {
                                                    String code = response.getString("CustomerMessage");
                                                    if ("Success. Request accepted for processing".equals(code)) {
                                                        Toast.makeText(BookActivity.this, code, Toast.LENGTH_SHORT).show();

                                                        Thread.sleep(10);
                                                        dialogap.cancel();
                                                        dialog.hide();
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "Kindly Try Again", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                // handle error
                                                Toast.makeText(BookActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });

                    stkContinue = (Button) classpopupView.findViewById(R.id.cfromwallet);
                    stkContinue.setText("Subscribe From Wallet (Bal:" + walletamtbal + " KES)");
                    walletamount = (EditText) classpopupView.findViewById(R.id.walletamount);
                    stkContinue.setTypeface(tfm);
                    stkContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Intent intentStk = getPackageManager().getLaunchIntentForPackage("com.android.stk");
                            //startActivity(intentStk);
                            walletamt = walletamount.getText().toString();
                            if (walletamt.isEmpty()) {
                                Toast.makeText(BookActivity.this, "Price Should Not Be Empty", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(walletamt) < 25) {
                                Toast.makeText(BookActivity.this, "Price Should Not Be Less Than 25 Bob", Toast.LENGTH_SHORT).show();
                            } else {
                                AndroidNetworking.get(Application.domain + "api/stkloadwalletpushclass/{phone}/{amount}/{classid}")
                                        .addPathParameter("phone", "0" + phone.substring(phone.length() - 9, phone.length()))
                                        .addPathParameter("amount", walletamt)
                                        .addPathParameter("classid", urlId)
                                        .setTag("test")
                                        .setPriority(Priority.HIGH)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // do anything with response
                                                try {
                                                    String code = response.getString("code");
                                                    if (("103").equals(code)) {
                                                        dialogap.cancel();
                                                        openLoadWalletDialog();
                                                    } else if (("100").equals(code)) {
                                                        Toast.makeText(BookActivity.this, "Subscribed Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                //Toast.makeText(BookActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                // handle error
                                                Toast.makeText(BookActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                dialogap.cancel();
                            }
                        }
                    });


                    airtel = (Button) classpopupView.findViewById(R.id.cairtelPay);
                    airtel.setTypeface(tfm);
                    airtel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Intent intentStk = getPackageManager().getLaunchIntentForPackage("com.android.stk");
                            //startActivity(intentStk);
                            //Toast.makeText(BookActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BookActivity.this, PesapalclassActivity.class);
                            intent.putExtra("phone", phone);
                            intent.putExtra("classid", urlId);
                            startActivity(intent);
                        }
                    });
                    TextView tv = (TextView) classpopupView.findViewById(R.id.tv);
                    tv.setTypeface(tfm);
                    //tv.setText("Account No:    " + topics.getId().toString() + ".elimu");



                };
            }
        });
    }















    protected void openLoadWalletDialog() {
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(BookActivity.this);
        final View loadwalletView = getLayoutInflater().inflate(R.layout.loadwallet, null);
        mbuilder.setView(loadwalletView);
        final AlertDialog dialogload = mbuilder.create();
        dialogload.show();

        loadwalletbtn = (Button) loadwalletView.findViewById(R.id.walletbtn);
        loadwalletamout = (EditText) loadwalletView.findViewById(R.id.loadamount);
        loadwalletbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadwalletamt = loadwalletamout.getText().toString();
                Toast.makeText(BookActivity.this, "Load Initiated", Toast.LENGTH_SHORT).show();
                AndroidNetworking.post(Application.domain+"api/walletstkpush")
                        .addBodyParameter("phone", "254"+phone.substring(phone.length()-9,phone.length()))
                        .addBodyParameter("amount", loadwalletamt)
                        .setTag("load wallet")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // do anything with response
                                //Toast.makeText(BookActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                try {
                                    String jsonres = response.getString("rst");
                                    JSONObject jsonobj = new JSONObject(jsonres);
                                    if ("Success. Request accepted for processing".equals(jsonobj.getString("CustomerMessage"))){
                                        Toast.makeText(BookActivity.this, jsonobj.getString("CustomerMessage"), Toast.LENGTH_SHORT).show();

                                        Thread.sleep(10);
                                        dialogload.cancel();
                                    }else {
                                        Toast.makeText(BookActivity.this, "Kindly Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dialogload.cancel();
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                                Toast.makeText(BookActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        if(phone != "nophone"){
            inflator.inflate(R.menu.main_menu,menu);
        }else{
            inflator.inflate(R.menu.not_logged,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }



    private class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> implements View.OnClickListener {
        private Context mcontext;
        private ArrayList<SubTopics> subtopics;

        public BookAdapter(Context context, ArrayList<SubTopics> subtopics) {
            this.mcontext = context;
            this.subtopics = subtopics;
        }

        @Override
        public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflator = LayoutInflater.from(context);

            bookView = inflator.inflate(R.layout.activity_list_book,parent,false);
            BookAdapter.ViewHolder adapter = new BookAdapter.ViewHolder(bookView);
            return adapter;

        }

        @Override
        public void onBindViewHolder(BookAdapter.ViewHolder holder, int position) {
            final SubTopics topics = subtopics.get(position);
            holder.bookName.setText(subtopics.get(position).getName());
            holder.bookName.setTypeface(tfb);
            holder.description.setText(subtopics.get(position).getDescription());
            holder.description.setTypeface(tfb);
            holder.lessons.setText(subtopics.get(position).getLesson()+" lessons");
            holder.lessons.setTypeface(tfr);
            if (topics.photo == "") {
                holder.imageView.setImageResource(R.mipmap.book_icon);
            }else {
                BookActivity.this.importImage(Application.domain+"uploads/"+topics.photo,holder.imageView);
            }
            bookView.setTag(R.id.idBook, subtopics.get(position).getId());
            bookView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    //Check if subscribed
                    dialog.show();
                    if(phone == "nophone"){
                        //Toast.makeText(getApplicationContext(), view.getTag(R.id.idBook).toString(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BookActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }else {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Application.domain)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        Auth service = retrofit.create(Auth.class);

                        Call<ReadBook> call = service.authBook(new OAuthBook(phone,accessToken,topics.getId()));
                        call.enqueue(new Callback<ReadBook>() {
                            @Override
                            public void onResponse(Call<ReadBook> call, retrofit2.Response<ReadBook> response) {

                                    dialog.hide();
                                    ReadBook term = response.body();


                                    if(("0001").equals(term.getClientId().toString())){
                                        Toast.makeText(BookActivity.this, term.getAmount(), Toast.LENGTH_SHORT).show();
                                    }else if (("0002").equals(term.getClientId().toString())){
                                        //SINCE THE CLIENT HAS NOT PAID, SHOW THEM HOW TO PAY
                                        //LayoutInflater inflatorp = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                        //final ViewGroup customView = (ViewGroup) inflatorp.inflate(R.layout.popup,null);

                                        AlertDialog.Builder mbuilder = new AlertDialog.Builder(BookActivity.this);
                                        View popupView = getLayoutInflater().inflate(R.layout.popup, null);
                                        mbuilder.setView(popupView);
                                        dialogap = mbuilder.create();
                                        dialogap.show();

                                        TextView txt = (TextView) popupView.findViewById(R.id.howtopay);
                                        TextView paybill = (TextView) popupView.findViewById(R.id.paybill);
                                        txt.setTypeface(tfm);
                                        paybill.setTypeface(tfm);
                                        mPopupWindow = new PopupWindow(popupView, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT,true);
                                        contClose = (Button) popupView.findViewById(R.id.close);
                                        contClose.setTypeface(tfm);
                                        contClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogap.cancel();
                                            }
                                        });

                                        stkContinue = (Button) popupView.findViewById(R.id.fromwallet);
                                        stkContinue.setText("Subscribe From Wallet (Bal:"+ term.getBalance()+" KES)");
                                        walletamount = (EditText) popupView.findViewById(R.id.walletamount);
                                        stkContinue.setTypeface(tfm);
                                        stkContinue.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //Intent intentStk = getPackageManager().getLaunchIntentForPackage("com.android.stk");
                                                //startActivity(intentStk);
                                                walletamt = walletamount.getText().toString();
                                                if(walletamt.isEmpty()){
                                                    Toast.makeText(BookActivity.this, "Price Should Not Be Empty", Toast.LENGTH_SHORT).show();
                                                }
                                                else if(Integer.parseInt(walletamt) < 5){
                                                    Toast.makeText(BookActivity.this, "Price Should Not Be Less Than 5 Bob", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    AndroidNetworking.get(Application.domain+"api/stkloadwalletpush/{phone}/{amount}/{bookid}")
                                                            .addPathParameter("phone", "0"+phone.substring(phone.length()-9,phone.length()))
                                                            .addPathParameter("amount", walletamt)
                                                            .addPathParameter("bookid", topics.getId())
                                                            .setTag("test")
                                                            .setPriority(Priority.LOW)
                                                            .build()
                                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    // do anything with response
                                                                    try {
                                                                        String code = response.getString("code");
                                                                        if(("103").equals(code)){
                                                                            dialogap.cancel();
                                                                            openLoadWalletDialog();
                                                                        }else if(("100").equals(code)){
                                                                            Toast.makeText(BookActivity.this, "Subscribed Successfully", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    //Toast.makeText(BookActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                                @Override
                                                                public void onError(ANError error) {
                                                                    // handle error
                                                                    Toast.makeText(BookActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    dialogap.cancel();
                                                }
                                            }
                                        });

                                        stkpush = (Button) popupView.findViewById(R.id.safPush);
                                        amount = (EditText) popupView.findViewById(R.id.safamount);
                                        stkpush.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                safAmount = amount.getText().toString();
                                                if(safAmount.isEmpty()){
                                                    Toast.makeText(BookActivity.this, "Price Should Not Be Empty", Toast.LENGTH_SHORT).show();
                                                }
                                                else if(Integer.parseInt(safAmount) < 5){
                                                    Toast.makeText(BookActivity.this, "Price Should Not Be Less Than 5 Bob", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(BookActivity.this, "Please wait a moment", Toast.LENGTH_LONG).show();
                                                    AndroidNetworking.post(Application.domain + "api/stkpush")
                                                            .addBodyParameter("phone", "254" + phone.substring(phone.length() - 9, phone.length()))
                                                            .addBodyParameter("bookid", topics.getId())
                                                            .addBodyParameter("amount", safAmount)
                                                            .addBodyParameter("bookname", topics.getName())
                                                            .setTag("test")
                                                            .setPriority(Priority.HIGH)
                                                            .build()
                                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    // do anything with response
                                                                    try {
                                                                        String code = response.getString("CustomerMessage");
                                                                        if ("Success. Request accepted for processing".equals(code)) {
                                                                            Toast.makeText(BookActivity.this, code, Toast.LENGTH_SHORT).show();

                                                                            Thread.sleep(10);
                                                                            dialogap.cancel();
                                                                            dialog.hide();
                                                                        } else {
                                                                            Toast.makeText(BookActivity.this, "Kindly Try Again", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(ANError error) {
                                                                    // handle error
                                                                    Toast.makeText(BookActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                        });
                                        airtel = (Button) popupView.findViewById(R.id.airtelPay);
                                        airtel.setTypeface(tfm);
                                        airtel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //Intent intentStk = getPackageManager().getLaunchIntentForPackage("com.android.stk");
                                                //startActivity(intentStk);
                                                //Toast.makeText(BookActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(BookActivity.this, PesapalActivity.class);
                                                intent.putExtra("phone",phone);
                                                intent.putExtra("bookid", topics.getId());
                                                startActivity(intent);
                                            }
                                        });
                                        TextView tv = (TextView) popupView.findViewById(R.id.tv);
                                        tv.setTypeface(tfm);
                                        tv.setText("Account No:    "+topics.getId().toString() + ".elimu");
                                    }else {
                                        Intent intent = new Intent(BookActivity.this, TermActivity.class);
                                        intent.putExtra("bookid", term.getBookId());
                                        intent.putExtra("bookname", topics.getName());
                                        startActivity(intent);
                                    }

                            }

                            @Override
                            public void onFailure(Call<ReadBook> call, Throwable t) {
                                dialog.hide();
                                Toast.makeText(mcontext, "Sorry, something went wrong. Try after sometime", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //Toast.makeText(mcontext, "You are logged in", Toast.LENGTH_SHORT).show();
                    }
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
            public TextView description;
            public ImageView imageView;
            public Button bookCover,classbtn;
            public TextView lessons;
            public ViewHolder(View itemView) {
                super(itemView);
                bookName = (TextView) itemView.findViewById(R.id.bookname);
                description = (TextView) itemView.findViewById(R.id.bkdescription);
                imageView = (ImageView) itemView.findViewById(R.id.image);
                //bookCover = (Button) itemView.findViewById(R.id.viewCover);
                lessons = (TextView) itemView.findViewById(R.id.lessons);
            }
        }
    }
}
