package com.example.workinstructions;

import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressDialog {

    private ProgressDialog progressDialog;
    private Context context;

    public MyProgressDialog(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading..."); // Set the message to be displayed
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by pressing outside
    }

    public void show() {
        progressDialog.show();
    }

    public void dismiss() {
        progressDialog.dismiss();
    }
}
