package com.triet.quyetchat.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.triet.quyetchat.R;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout edt_email, edt_password;
    Button btn_login;
    Toolbar toolbar;
    ProgressDialog loginPRDialog;
    DatabaseReference mUserDatabase;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        addListener();

    }
    private void init() {

        edt_email = (TextInputLayout) findViewById(R.id.login_displayemail);
        edt_password = (TextInputLayout) findViewById(R.id.login_password);
        btn_login = (Button) findViewById(R.id.btn_loginacc);
        loginPRDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);

    }

    private void addListener() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login_email = edt_email.getEditText().getText().toString();
                String login_password = edt_password.getEditText().getText().toString();

                if(!TextUtils.isEmpty(login_email) && !TextUtils.isEmpty(login_password)){

                    loginPRDialog.setTitle("Login User");
                    loginPRDialog.setMessage("Please wait while we check your credentials...");
                    loginPRDialog.setCanceledOnTouchOutside(false);
                    loginPRDialog.show();

                    loginUser(login_email, login_password);

                }
            }
        });

    }

    private void loginUser(String login_email, String login_password) {
        mAuth.signInWithEmailAndPassword(login_email, login_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loginPRDialog.dismiss();

                          String current_user_id = mAuth.getCurrentUser().getUid();
                          String deviceToken = FirebaseInstanceId.getInstance().getToken();
                          mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
//
                                }
                            });


                        } else {
                          loginPRDialog.hide();
                            Toast.makeText(LoginActivity.this, "Can not sing in, Please check the form and try again", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
}
