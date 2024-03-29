package com.example.fungus.serviceprovider1;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fungus.serviceprovider1.model.User;
import com.example.fungus.serviceprovider1.sql.DatabaseTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_READ_CONTACTS = 0;
    private UserLoginTask mAuthTask = null;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private DatabaseTable db;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    ValueEventListener postListener;
    private DatabaseReference ref;
    private String id;
    private int userType = 0;
    private boolean checkLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Service Provider");

        db =  new DatabaseTable((getApplicationContext()));
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                }
            }
        };

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(),"Successfully signed in with: " + user.getEmail(),Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(),"Successfully signed out.",Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };

        sharedPreferences = getApplicationContext().getSharedPreferences("authentication",0);
        Log.e("Check Sign In",Boolean.toString(sharedPreferences.contains("authentication")));
        if(!(sharedPreferences.getString("user_id",null)==null)){
            showProgress(true);
            String id = mAuth.getCurrentUser().getUid();
            final ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        checkLogin = false;
                        Log.e(TAG + " userType", String.valueOf(user.getUserType()));
                        userType = user.getUserType();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                        String regId = pref.getString("regId", null);
                        if (!TextUtils.isEmpty(regId))
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("regID").setValue(regId);
                        else
                            Toast.makeText(getApplicationContext(),"Firebase Reg Id is not received yet!",Toast.LENGTH_SHORT).show();
                        showProgress(false);
                        if(userType==2){
                            startActivity( new Intent(LoginActivity.this,MapsMainActivity_T.class));
                            finish();
                        }else if(userType == 1) {
                            Log.e("usertype", "1");
                            startActivity(new Intent(LoginActivity.this, SPNavigationDrawerActivity.class));
                            finish();
                        }else if(userType == 0){
                            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            ref = mDatabase.child("users").child(mAuth.getCurrentUser().getUid());
            ref.addListenerForSingleValueEvent(valueEventListener);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(checkLogin) {
                            ref.removeEventListener(valueEventListener);
                            showProgress(false);
                            makeMessage("Login fail! Slow network connection!");
                        }
                    }
                }, 7000);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 500);
            return;
        }
        editor = sharedPreferences.edit();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

//    //Login when create app with previous account
//    private void attemptLoginOnCreate(){
//        showProgress(true);
//        String email = sharedPreferences.getString("user_id",null);
//        String password = sharedPreferences.getString("password",null);
//        mAuthTask = new UserLoginTask(email, password);
//        mAuthTask.execute((Void) null);
//    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

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
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        for(User users : db.fnGetAllUser()){
            emails.add(users.getUser_ID());
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
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
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private int userType = 3;
        private final String mEmail;
        private final String mPassword;
        // 0=error; 1=loginSuccess; 2=passwordWrong; 3=emailRegister; 4=RegisterSuccess; 6=Network Error;
        private int condition = 0;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // firebase
            Log.e("email", mEmail);
            Log.e("password", mPassword);
//            postListener =

            Task a = mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "signInWithEmail:success");
                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                                        String regId = pref.getString("regId", null);
                                        if (!TextUtils.isEmpty(regId))
                                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("regID").setValue(regId);
                                        else
                                            Toast.makeText(getApplicationContext(),"Firebase Reg Id is not received yet!",Toast.LENGTH_SHORT).show();
                                        User user = dataSnapshot.getValue(User.class);
                                        if (user != null) {
                                            Log.e(TAG + " userType", String.valueOf(user.getUserType()));
                                            userType = user.getUserType();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "signInWithEmail:failure", task.getException());

                            }
                        }
                    }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Sign In Fail","Slow");
                            makeMessage("Slow Network Connection");
                        }
                    });

            Task b = mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "createUserWithEmail:success");
                                String user = mAuth.getCurrentUser().getUid();
                                User user1 = new User(mEmail, mPassword);
                                if (db.fnInsertUser(user1) == 0) {
                                    condition = 0;
                                    Log.e(TAG, "sql database register fail");
                                } else {
                                    condition = 4;
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            }


                        }
                    });


            try {
                // Simulate network access.
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                condition = 0;
                return false;
            }

            if (a.isSuccessful() && userType != 3) {
                Log.e("hey", "sign in");
                condition = 1;
                return true;
            }else if (!b.isSuccessful()) {
                try {
                    throw b.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    condition = 5;
                    mPasswordView.setError(getString(R.string.error_weak_password));
                    return false;
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    condition = 2;
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    return false;
                } catch (FirebaseAuthUserCollisionException e) {
                    condition = 3;
                    mEmailView.setError(getString(R.string.error_user_exists));
                    return false;
                } catch (FirebaseNetworkException e){
                    condition = 6;
                    return false;
                }
                catch (Exception e) {
                    condition = 0;
                    Log.e(TAG, e.getMessage());
                    return false;
                }
            } else if (b.isSuccessful()) {
                Log.e("hey", "sign up");
                condition = 4;
                return true;
            } else {
                Log.e("hey", "false");
                condition = 0;
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            switch (condition){
                case 0:
                    Toast.makeText(getApplicationContext(),"Authentication Error",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                case 3:
                    mEmailView.requestFocus();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    mPasswordView.requestFocus();
                    break;
                case 6:
                    makeMessage("Please check your network connectivity!");
                    break;
            }

            if (success) {
                editor.putString("user_id",mEmail);
                editor.putString("password",mPassword);
                editor.commit();
                if(condition==4 || userType == 0){
                    Log.e("e","4");
                    startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                    finish();
                }else if(userType == 2){
                    Log.e("usertype","2");
                    startActivity( new Intent(LoginActivity.this,MapsMainActivity_T.class));
                    finish();
                }else if(userType == 1){
                    Log.e("usertype","1");
                    startActivity(new Intent(LoginActivity.this,SPNavigationDrawerActivity.class));
                    finish();
                }else{
                    Log.e("e","e");
                }
            } else {
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void makeMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();;
    }

}

