package com.amusoft.vlog.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amusoft.vlog.Constants;
import com.amusoft.vlog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child(Constants.firebase_reference);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView,mUsernameView;
    private View mProgressView;
    private View mLoginFormView;


    SharedPreferences prefs ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = getApplication().getSharedPreferences(Constants.shared_preference, 0);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mUsernameView= (EditText) findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        authenticationSetup();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString(),mUsernameView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(mEmailView.getText().toString(),mPasswordView.getText().toString(), mUsernameView.getText().toString());
                prefs.edit().putString(Constants.firebase_reference_user_username,
                        mUsernameView.getText().toString()).commit();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin(final String email, String password, final String username) {
        prefs.edit().putString(Constants.firebase_reference_user_username,
                username).commit();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


if(task.isSuccessful()){
    //Saving userdata to firebase
    HashMap<String, Object> result = new HashMap<>();
    result.put(Constants.firebase_reference_user_email, email);
    result.put(Constants.firebase_reference_user_username,username);
    myRef.push().setValue(result);
    prefs.edit().putString(Constants.firebase_reference_user_username,username).commit();
    Toast.makeText(LoginActivity.this, "Sucessfully Created user", Toast.LENGTH_SHORT).show();

}


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();




                        }

                        // ...
                    }
                });

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

if(task.isSuccessful()){
    Toast.makeText(LoginActivity.this, "Log in Sucessful", Toast.LENGTH_SHORT).show();
    prefs.edit().putString(Constants.firebase_reference_user_username,
            username).commit();


}




                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Authentication failed",Toast.LENGTH_LONG).show();




                }

            }
        });

    }

    private void authenticationSetup() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent i = new Intent(getApplicationContext(),ViewListVLogs.class);
                    startActivity(i);
                    finish();


                } else {
                    // User is signed out
                }
                // ...
            }
        };
        // ...
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
}

