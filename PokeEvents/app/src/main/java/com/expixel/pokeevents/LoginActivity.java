package com.expixel.pokeevents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by cellbody on 2016/9/9.
 */

public class LoginActivity extends BaseActivity{

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_welcome);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    nextActivity();
                    Log.d(TAG, "LoginActivity: onAuthStateChanged: signed_IN: " + user.getUid());
                } else {
                    loginConfig();
                    Log.d(TAG, "LoginActivity: onAuthStateChanged: signed_OUT " + firebaseAuth.toString());
                }
            }
        };

        SignInButton bt = (SignInButton) findViewById(R.id.btn_login_google_sign_in);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "LoginActivity: onClick: ");
                onBtClick(view);

            }
        });

    }

    private void loginConfig() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
                        // be available.
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                        Toast.makeText(LoginActivity.this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    protected void nextActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void onBtClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.btn_login_google_sign_in:
                Log.d(TAG, "LoginActivity: click: google sign in ");
                Log.d(TAG, "LoginActivity: onClickSignIn: ");
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    this.startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    Log.d(TAG, "LoginActivity: onClickSignin: mGoogleApiClient is null ? " + mGoogleApiClient.toString());
                    Log.d(TAG, "LoginActivity: onClickSignin: mGoogleApiClient connect ? " + mGoogleApiClient.isConnected());
                }
                break;
            default:
                Log.e(TAG, "LoginActivity: onClick: Something wrong");

        }
    }

    //    public void onClick(View v) {
//        Log.d(TAG, "LoginActivity: onClick: ");
//        int i = v.getId();
//        switch (i) {
//            case R.id.btn_login_google_sign_in:
//                Log.d(TAG, "LoginActivity: click: google sign in ");
//                Log.d(TAG, "LoginActivity: onClickSignIn: ");
//                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                    this.startActivityForResult(signInIntent, RC_SIGN_IN);
//                } else {
//                    Log.d(TAG, "LoginActivity: onClickSignin: mGoogleApiClient is null ? " + mGoogleApiClient.toString());
//                    Log.d(TAG, "LoginActivity: onClickSignin: mGoogleApiClient connect ? " + mGoogleApiClient.isConnected());
//                }
//                break;
//            default:
//                Log.e(TAG, "LoginActivity: onClick: Something wrong");
//
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                String personName = account.getDisplayName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();

                firebaseAuthWithGoogle(account);

                Log.d(TAG, "ProviderGoogle: onActivityResult: \n" +
                        "name: " + personName + "\n" +
                        "email: " + personEmail + "\n" +
                        "pesonId: " + personId + "\n" +
                        "personPhoto: " + personPhoto);
            } else {
                // Google Sign In failed, update UI appropriately

            }
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        showProgressDialog("loadinggggg");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Firebase: signInWithCredential: onComplete: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            String name = mAuth.getCurrentUser().getDisplayName();
                            String imgUrl = mAuth.getCurrentUser().getPhotoUrl().toString();
                            User user = new User(name, imgUrl);
                            String uid = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "ProviderGoogle: firebaseAuthWithGoogle: mAuth: SUCCESS\n" +
                                    "name: " + mAuth.getCurrentUser().getDisplayName() + "\n" +
                                    "email: " + mAuth.getCurrentUser().getEmail() + "\n" +
                                    "pesonId: " + mAuth.getCurrentUser().getUid() + "\n" +
                                    "personPhoto: " + mAuth.getCurrentUser().getPhotoUrl().toString());

                            databaseReference.child("users").child(uid).setValue(user);
                            LoginActivity.this.nextActivity();
                            LoginActivity.this.finish();
                        } else {
                            Log.w(TAG, "signInWithCredential", task.getException());


                        }
                        hideProgressDialog();
                    }
                });
    }

}
