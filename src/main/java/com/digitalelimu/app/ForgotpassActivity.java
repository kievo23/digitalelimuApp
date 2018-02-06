package com.digitalelimu.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digitalelimu.app.models.Application;
import com.digitalelimu.app.models.OAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotpassActivity extends AppCompatActivity {
    private Button reset;
    public EditText mphone;
    ProgressDialog dialog;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        reset = (Button) findViewById(R.id.reset);
        mphone = (EditText) findViewById(R.id.phoneno);

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Application.domain)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final Auth service = retrofit.create(Auth.class);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = mphone.getText().toString();
                dialog.show();
                Call<OAuth> call = service.passwordReset(phone);
                call.enqueue(new Callback<OAuth>() {
                    @Override
                    public void onResponse(Call<OAuth> call, Response<OAuth> response) {
                        //Toast.makeText(TermActivity.this, "Return success", Toast.LENGTH_SHORT).show();
                        if (response.body() != null){
                            //Toast.makeText(ForgotpassActivity.this, response.body().getPhone(), Toast.LENGTH_SHORT).show();
                            dialog.hide();
                            Intent resetpass = new Intent(getApplicationContext(), ResetcodeActivity.class);
                            resetpass.putExtra("phone",phone);
                            resetpass.putExtra("resetcode",response.body().getResetcode());
                            startActivity(resetpass);
                        }else{
                            dialog.hide();
                            Toast.makeText(ForgotpassActivity.this, "Phone number not found", Toast.LENGTH_LONG).show();
                            //Toast.makeText(ForgotpassActivity.this, call.request().url().toString(), Toast.LENGTH_LONG).show();
                            //Log.v("MESSAGE",response.message());
                            //Log.v("MESSAGE",response.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<OAuth> call, Throwable t) {

                    }
                });
            }
        });

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

    }

}
