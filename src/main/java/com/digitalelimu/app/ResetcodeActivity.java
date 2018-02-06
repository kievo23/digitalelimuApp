package com.digitalelimu.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.digitalelimu.app.models.Application;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResetcodeActivity extends AppCompatActivity {
    public String phone,resetcode,inputreset,newpassword,confirmpassword;
    public ProgressDialog dialog;
    public Button verify,changePassword;
    public EditText reset,mnewpassword,mconfirmpassword;
    public LinearLayout newPasswordLayout,verifyLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetcode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        verify = (Button) findViewById(R.id.verifycode);
        changePassword = (Button) findViewById(R.id.changePassword);
        reset = (EditText) findViewById(R.id.reset);
        mnewpassword = (EditText) findViewById(R.id.password);
        mconfirmpassword = (EditText) findViewById(R.id.confirm_password);

        verifyLayout = (LinearLayout) findViewById(R.id.verifyL);
        newPasswordLayout = (LinearLayout) findViewById(R.id.newPasswordLayout);
        newPasswordLayout.setVisibility(LinearLayout.GONE);


        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        Intent thisIntent = getIntent();
        phone = thisIntent.getStringExtra("phone");
        resetcode = thisIntent.getStringExtra("resetcode");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Application.domain)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final Auth service = retrofit.create(Auth.class);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputreset = reset.getText().toString();
                if(inputreset.equals(resetcode)){
                    Toast.makeText(ResetcodeActivity.this, "Code verified!", Toast.LENGTH_SHORT).show();
                    newPasswordLayout.setVisibility(LinearLayout.VISIBLE);
                    verifyLayout.setVisibility(LinearLayout.GONE);
                }else{
                    //Toast.makeText(ResetcodeActivity.this, "From Intent! "+resetcode, Toast.LENGTH_SHORT).show();
                    Toast.makeText(ResetcodeActivity.this, "The Code: "+inputreset+" is Invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newpassword = mnewpassword.getText().toString();
                confirmpassword = mconfirmpassword.getText().toString();
                if(newpassword.length() < 4){
                    Toast.makeText(ResetcodeActivity.this, "Password Too Short!", Toast.LENGTH_SHORT).show();
                }else if(newpassword.equals(confirmpassword)){
                    //Toast.makeText(ResetcodeActivity.this, "", Toast.LENGTH_SHORT).show();
                    dialog.show();
                    inputreset = reset.getText().toString();
                    Call<Integer> call = service.newPassword(phone,newpassword,inputreset);
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            //Toast.makeText(TermActivity.this, "Return success", Toast.LENGTH_SHORT).show();
                            Log.v("RESPONSEE",response.body().toString());
                            if (response.body().toString().equals("1")){
                                dialog.hide();
                                Toast.makeText(ResetcodeActivity.this, "Password Has Been Successfully Changed", Toast.LENGTH_LONG).show();
                                Intent resetpass = new Intent(ResetcodeActivity.this, RegisterActivity.class);
                                startActivity(resetpass);
                            }else{
                                dialog.hide();
                                Toast.makeText(ResetcodeActivity.this, call.request().url().toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {

                        }
                    });
                }else {
                    Toast.makeText(ResetcodeActivity.this, "Password Missmatch!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
