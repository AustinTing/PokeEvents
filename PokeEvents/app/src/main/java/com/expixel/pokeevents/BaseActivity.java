package com.expixel.pokeevents;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;



/**
 * Created by cellbody on 2016/9/9.
 */



public class BaseActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    protected static final String TAG = "PokeEvents";


    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.i(TAG, this.getClass().getSimpleName() + ": hideProgressDialog");

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, this.getClass().getSimpleName() + ": onCreate");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, this.getClass().getSimpleName() + ": onStart");


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, this.getClass().getSimpleName() + ": onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, this.getClass().getSimpleName() + ": onPause");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, this.getClass().getSimpleName() + ": onStop");
//
        hideProgressDialog();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, this.getClass().getSimpleName() + ": onDestroy");
    }
}


