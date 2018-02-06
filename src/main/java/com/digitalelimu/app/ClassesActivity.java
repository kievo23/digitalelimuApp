package com.digitalelimu.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.digitalelimu.app.models.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClassesActivity extends AppCompatActivity {

    private static final String TAG = "My DEBUG TAG";
    RequestQueue qued;
    BookAdapter mAdapter;
    ProgressDialog dialog;
    String phone = "";
    String accesstoken = "";
    Typeface tfr,tfb,tfm;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        if(phone != ""){
            inflator.inflate(R.menu.main_menu,menu);
        }else{
            inflator.inflate(R.menu.not_logged,menu);

        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menusearch:
                Intent intents = new Intent(ClassesActivity.this, SearchActivity.class);
                startActivity(intents);
                break;
            case R.id.login:
                Intent intentg = new Intent(ClassesActivity.this, RegisterActivity.class);
                startActivity(intentg);
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
                Intent intent = new Intent(ClassesActivity.this, SubscribeActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("accesstoken", accesstoken);
                startActivity(intent);
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
        setContentView(R.layout.activity_subtopics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String urlId = intent.getStringExtra("TOPIC");
        String topic = intent.getStringExtra("TOPICNAME");
        TextView username = (TextView) findViewById(R.id.username);

        tfr = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
        tfb = Typeface.createFromAsset(getAssets(),"Raleway-Bold.ttf");
        tfm = Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");

        SharedPreferences sharedData = getSharedPreferences("userInfo", MODE_PRIVATE);
        phone = sharedData.getString("phone","");
        accesstoken = sharedData.getString("accesstoken","");
        if(phone != ""){
            setTitle(topic);
            username.setText(sharedData.getString("phone",""));
        }else{
            setTitle(topic);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().show();

        Toast toast = Toast.makeText(getApplicationContext(),urlId,Toast.LENGTH_SHORT);
        //toast.show();

        String url = Application.domain+"/api/getClasses/"+urlId;
        qued = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0;i < response.length();i++){
                    try {
                        JSONObject topicObj = (JSONObject) response.get(i);
                        String id = topicObj.getString("id");
                        String sub_topic = topicObj.getString("name");
                        String description = topicObj.getString("description");
                        String books = topicObj.getString("books");
                        String photo = topicObj.getString("photo");

                        SubTopics newBook = new SubTopics(id,sub_topic,description,books,photo);
                        mAdapter.add(newBook);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Toast toast = Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        qued.add(request);

        ArrayList<SubTopics> myArray = new ArrayList<SubTopics>();
        mAdapter = new BookAdapter(this, myArray);

        ListView listView = (ListView) findViewById(R.id.topicsList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = getApplicationContext();
                //Toast toast = Toast.makeText(context, view.getTag().toString(),Toast.LENGTH_SHORT);
                //toast.show();
                Intent intent = new Intent(ClassesActivity.this, BookActivity.class);
                intent.putExtra("CLASSID",view.getTag(R.id.idString).toString());
                intent.putExtra("CLASSNAME",view.getTag(R.id.topicString).toString());
                startActivity(intent);
            }
        });

    }

    private class SubTopics{
        public String id;
        public String sub_topic;
        public String description;
        public String books;
        public String photo;

        public SubTopics(String id,String sub_topic,String description,String books,String photo){
            this.id = id;
            this.sub_topic = sub_topic;
            this.description = description;
            this.books = books;
            this.photo = photo;
        }
    }
    private class BookAdapter extends ArrayAdapter<SubTopics> {
        protected int position;

        public BookAdapter(Context context, ArrayList<SubTopics> subtopics) {
            super(context,0, subtopics);
        }

        @Override
        public View getView(int position,View convertView, ViewGroup parent){

            SubTopics book = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_view, parent, false);
            }
            // Lookup view for data population
            //TextView id = (TextView) convertView.findViewById(R.id.id);
            TextView sub_topic = (TextView) convertView.findViewById(R.id.topic);
            sub_topic.setTypeface(tfb);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            description.setTypeface(tfr);
            TextView books = (TextView) convertView.findViewById(R.id.books);
            books.setTypeface(tfm);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            // Populate the data into the template view using the data object
            //id.setText(book.id);
            sub_topic.setText(book.sub_topic);
            description.setText(book.description);
            books.setText(book.books+" books");
            ClassesActivity.this.importImage(Application.domain+"uploads/"+book.photo,icon);
            convertView.setTag(R.id.idString,book.id);
            convertView.setTag(R.id.topicString,book.sub_topic);

            // Return the completed view to render on screen
            return convertView;

        }
    }
}
