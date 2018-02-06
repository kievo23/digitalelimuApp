package com.digitalelimu.app;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "My DEBUG TAG";
    RequestQueue que;
    BookAdapter mAdapter;
    String phone,accesstoken = "";
    Typeface tfr,tfb,tfm;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        if(phone != ""){
            MenuItem searchItem = menu.findItem(R.id.menusearch);
            //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            inflator.inflate(R.menu.main_menu,menu);
        }else{
            //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            MenuItem searchItem = menu.findItem(R.id.menusearch);
            inflator.inflate(R.menu.not_logged,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menusearch:
                Intent intents = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intents);
                break;
            case R.id.login:
                Intent intentg = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intentg);
                break;
            case R.id.subscriptions:
                Intent intent = new Intent(MainActivity.this, SubscribeActivity.class);
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = Application.domain+"/api/getCategories";
        que = Volley.newRequestQueue(this);

        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        SharedPreferences sharedData = getSharedPreferences("userInfo", MODE_PRIVATE);
        phone = sharedData.getString("phone","");
        accesstoken = sharedData.getString("accesstoken","");

        tfr = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
        tfb = Typeface.createFromAsset(getAssets(),"Raleway-Bold.ttf");
        tfm = Typeface.createFromAsset(getAssets(),"Raleway-Medium.ttf");

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0;i < response.length();i++){
                    try {
                        JSONObject topicObj = (JSONObject) response.get(i);
                        String id = topicObj.getString("id");
                        String topic = topicObj.getString("name");
                        String description = topicObj.getString("description");
                        String photo = topicObj.getString("photo");

                        Book newBook = new Book(id,topic,description,photo);
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

        que.add(request);

        ArrayList<Book> myArray = new ArrayList<Book>();
        mAdapter = new BookAdapter(this, myArray);

        ListView listView = (ListView) findViewById(R.id.topicsList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = getApplicationContext();
                //
                Intent intent = new Intent(MainActivity.this, ClassesActivity.class);
                intent.putExtra("TOPIC",view.getTag(R.id.idString).toString());
                intent.putExtra("TOPICNAME",view.getTag(R.id.topicString).toString());
                //Toast toast = Toast.makeText(context, view.getTag(R.id.topicString).toString(),Toast.LENGTH_SHORT);
                //toast.show();
                startActivity(intent);
            }
        });
    }

    private class Book{
        public String id;
        public String topic;
        public String description;
        public String photo;

        public Book(String id,String topic,String description,String photo){
            this.id = id;
            this.topic = topic;
            this.description = description;
            this.photo = photo;
        }
    }
    private class BookAdapter extends ArrayAdapter<Book> {
        protected int position;

        public BookAdapter(Context context, ArrayList<Book> book) {
            super(context,0, book);
        }

        @Override
        public View getView(int position,View convertView, ViewGroup parent){

            Book book = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_view, parent, false);
            }
            // Lookup view for data population
            //TextView id = (TextView) convertView.findViewById(R.id.id);
            TextView topic = (TextView) convertView.findViewById(R.id.topic);
            topic.setTypeface(tfb);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            description.setTypeface(tfr);
            // Populate the data into the template view using the data object
            //id.setText(book.id);
            topic.setText(book.topic);
            description.setText(book.description);

            MainActivity.this.importImage(Application.domain+"uploads/"+book.photo,icon);
            convertView.setTag(R.id.idString,book.id);
            convertView.setTag(R.id.topicString,book.topic);
            // Return the completed view to render on screen
            return convertView;

        }
    }
}


