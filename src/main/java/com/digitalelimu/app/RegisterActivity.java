package com.digitalelimu.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalelimu.app.models.Application;
import com.digitalelimu.app.models.OAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private static UserLoginTask mAuthTask = null;
    private static UserRegistrationTask mAuthTaskRst = null;

    // UI references.
    private static AutoCompleteTextView mPhoneView,mEmailView2;
    private static EditText mPasswordView,mPasswordView2,mEmail;
    private static  View mProgressView;
    private static View mLoginFormView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setTitle("Digital Elimu Access");

        getIntent().setAction("Already created");

        /*SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove("phone");
        edit.remove("accesstoken");
        edit.commit();*/

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        // Set up the login form.

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("fragment", String.valueOf(sectionNumber));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                rootView = inflater.inflate(R.layout.fragment_login, container, false);
                mPhoneView = (AutoCompleteTextView) rootView.findViewById(R.id.phone);
                mPasswordView = (EditText) rootView.findViewById(R.id.password);
                mLoginFormView = rootView.findViewById(R.id.login_form);
                mProgressView = rootView.findViewById(R.id.login_progress);
                Button signinButton = (Button) rootView.findViewById(R.id.email_sign_in_button);
                Button forgotpass = (Button) rootView.findViewById(R.id.forgotpassword);
                final View finalRootView1 = rootView;
                signinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getContext(), "Sign in clicked", Toast.LENGTH_SHORT).show();
                        attemptLogin(finalRootView1,mPhoneView,mPasswordView);
                    }
                });
                forgotpass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),ForgotpassActivity.class);
                        startActivity(intent);
                    }
                });
                mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptLogin(finalRootView1,mPhoneView,mPasswordView);
                            return true;
                        }
                        return false;
                    }
                });

            }else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                rootView = inflater.inflate(R.layout.fragment_register, container, false);
                Button signinButton = (Button) rootView.findViewById(R.id.email_sign_in_button2);
                mEmailView2 = (AutoCompleteTextView) rootView.findViewById(R.id.email2);
                mPasswordView2 = (EditText) rootView.findViewById(R.id.password2);
                mEmail = (EditText) rootView.findViewById(R.id.email);
                mLoginFormView = rootView.findViewById(R.id.login_form2);
                mProgressView = rootView.findViewById(R.id.login_progress2);
                final View finalRootView = rootView;
                signinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptRegistration(finalRootView,mEmailView2,mPasswordView2,mEmail);
                    }
                });


                mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptRegistration(finalRootView,mEmailView2,mPasswordView2,mEmail);
                            return true;
                        }
                        return false;
                    }
                });
            }
            return rootView;
        }
        private void attemptLogin(View rootView,AutoCompleteTextView username, EditText passwordView) {
            if (mAuthTask != null) {
                //return;
            }else{
                mAuthTask = null;
            }

            // Reset errors.
            username.setError(null);
            passwordView.setError(null);

            // Store values at the time of the login attempt.
            String phone = username.getText().toString();
            String password = passwordView.getText().toString();

            boolean cancel = false;
            View focusView = rootView;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                passwordView.setError(getString(R.string.error_invalid_password));
                focusView = passwordView;
                cancel = true;
            }

            if(SisEmpty(password)){
                passwordView.setError(getString(R.string.error_invalid_password));
                focusView = passwordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(phone)) {
                username.setError(getString(R.string.error_field_required));
                focusView = username;
                cancel = true;
            }else if(!isInteger(phone)){
                username.setError("Not a valid phone number");
                focusView = username;
                cancel = true;
            }else if (!isPhoneValid(phone)) {
                username.setError(getString(R.string.error_invalid_phone));
                focusView = username;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                new RegisterActivity().showProgress(true);
                mAuthTask = new UserLoginTask(phone, password, getContext());
                mAuthTask.execute((Void) null);
            }
        }

        private void attemptRegistration(View rootView,AutoCompleteTextView username, EditText passwordView, EditText emailView) {
            if (mAuthTaskRst != null) {
                //return;
            }else {
                mAuthTaskRst = null;
            }

            // Reset errors.
            username.setError(null);
            passwordView.setError(null);
            emailView.setError(null);

            // Store values at the time of the login attempt.
            String phone = username.getText().toString();
            String password = passwordView.getText().toString();
            String email = emailView.getText().toString();

            boolean cancel = false;
            View focusView = rootView;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                passwordView.setError(getString(R.string.error_invalid_password));
                focusView = passwordView;
                cancel = true;
            }

            if(SisEmpty(password)){
                passwordView.setError(getString(R.string.error_invalid_password));
                focusView = passwordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(phone)) {
                username.setError(getString(R.string.error_field_required));
                focusView = username;
                cancel = true;
            }else if(!isInteger(phone)){
                username.setError("Not a valid phone number");
                focusView = username;
                cancel = true;
            }else if (!isPhoneValid(phone)) {
                username.setError(getString(R.string.error_invalid_phone));
                focusView = username;
                cancel = true;
            }else if(!isEmailValid(email)){
                emailView.setError(getString(R.string.error_invalid_email));
                focusView = emailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                new RegisterActivity().showProgress(true);
                mAuthTaskRst = new UserRegistrationTask(phone, password, email,getContext());
                mAuthTaskRst.execute((Void) null);
            }
        }

        private boolean isPhoneValid(String email) {
            //TODO: Replace this with your own logic
            return email.length() == 10;
        }

        private boolean isPasswordValid(String password) {
            //TODO: Replace this with your own logic
            return password.length() > 4;
        }

        public boolean isEmailValid(String email)
        {
            String regExpn =
                    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                            +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                            +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                            +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

            CharSequence inputStr = email;

            Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);

            if(matcher.matches())
                return true;
            else
                return false;
        }

        private boolean isInteger(String s) {
            try {
                Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return false;
            } catch(NullPointerException e) {
                return false;
            }
            // only got here if we didn't return false
            return true;
        }

        private boolean SisEmpty(String s){
            if(s.length() == 0){
                return true;
            }
            return false;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment placeHolder = new PlaceholderFragment();
            return placeHolder.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Login";
                case 1:
                    return "Regiter";
            }
            return null;
        }
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = 400;

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), RegisterActivity.ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(RegisterActivity.ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mPhoneView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private static class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        Auth service;
        Context context;

        UserLoginTask(String phone, String password, Context cntxt) {
            mEmail = phone;
            mPassword = password;
            context = cntxt;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Application.domain+"/api/authUser/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(Auth.class);
        }



        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

           /* SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phone","");
            editor.putString("accesstoken","");
            editor.apply();*/

            //Thread.sleep(2000);
            Call<OAuth> call = service.authUser(mEmail,mPassword);
            call.enqueue(new Callback<OAuth>() {
                @Override
                public void onResponse(Call<OAuth> call, Response<OAuth> response) {
                    if(response.body() != null){
                        OAuth user = response.body();
                        new RegisterActivity().startSomething(user,mPassword,context);

                    }else {
                        mAuthTaskRst = null;
                        new RegisterActivity().failure(context);
                    }
                    //Toast.makeText(LoginActivity.this, user.getPhone(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<OAuth> call, Throwable t) {
                    mAuthTaskRst = null;
                    new RegisterActivity().failure(context);
                }
            });

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            new RegisterActivity().showProgress(false);
        }
    }

    private static class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPhone;
        private final String mPassword;
        private final String mEmail;
        Auth service;
        Context context;

        UserRegistrationTask(String phone, String password, String email, Context cntxt) {
            mPhone = phone;
            mPassword = password;
            mEmail = email;
            context = cntxt;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Application.domain)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(Auth.class);
        }



        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

           /* SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phone","");
            editor.putString("accesstoken","");
            editor.apply();*/

            //Thread.sleep(2000);
            Call<OAuth> call = service.authRegister(new OAuth(mPhone,mPassword,mEmail));
            call.enqueue(new Callback<OAuth>() {
                @Override
                public void onResponse(Call<OAuth> call, Response<OAuth> response) {
                    if(response.body().getPhone() != null){
                        Toast.makeText(context, "Welcome: "+response.body().getPhone(), Toast.LENGTH_SHORT).show();
                        OAuth user = response.body();
                        new RegisterActivity().startSomething(user,mPassword,context);

                    }else {
                        mAuthTaskRst = null;
                        Toast.makeText(context, "Already Registered. Kindly login with your credentials", Toast.LENGTH_LONG).show();
                        new RegisterActivity().showProgress(false);
                    }
                    //Toast.makeText(LoginActivity.this, user.getPhone(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<OAuth> call, Throwable t) {
                    mAuthTaskRst = null;
                    Toast.makeText(context, call.request().url().toString(), Toast.LENGTH_SHORT).show();
                    new RegisterActivity().showProgress(false);
                }
            });

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            new RegisterActivity().showProgress(false);
        }
    }

    public void startSomething(OAuth user, String mPassword, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone",user.getPhone());
        editor.putString("accesstoken",user.getAccesstoken());
        editor.apply();
        mAuthTask = new RegisterActivity.UserLoginTask(user.getPhone(),mPassword,context);
        showProgress(false);
        Toast.makeText(context, "Now Logged In Via: " + user.getPhone(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
        finish();
    }
    public void failure(Context context){
        showProgress(false);
        mAuthTask = null;
        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context,RegisterActivity.class);
        context.startActivity(intent);
        finish();
    }
}
