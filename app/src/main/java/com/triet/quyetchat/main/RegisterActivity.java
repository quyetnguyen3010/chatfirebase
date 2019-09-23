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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.triet.quyetchat.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout edt_name, edt_password, edt_email;
    Button btn_createName;
    Toolbar mToolbar;

    DatabaseReference mDatabase;
    ProgressDialog rgProgressDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        addListener();
    }

    private void init() {
        edt_name = (TextInputLayout) findViewById(R.id.reg_displayname);
        edt_email = (TextInputLayout) findViewById(R.id.reg_displayemail);
        edt_password = (TextInputLayout) findViewById(R.id.reg_displaypassword);
        btn_createName = (Button) findViewById(R.id.btn_creteAccount);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        rgProgressDialog = new ProgressDialog(this);
    }

    private void addListener() {


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_createName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String displayName = edt_name.getEditText().getText().toString();
                String displayEmail = edt_email.getEditText().getText().toString();
                String displayPassword = edt_password.getEditText().getText().toString();

                if (TextUtils.isEmpty(displayName) || TextUtils.isEmpty(displayEmail) || TextUtils.isEmpty(displayPassword)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (displayPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                } else {

                    rgProgressDialog.setTitle("Registing User");
                    rgProgressDialog.setMessage("Please wait while we create your account...");
                    rgProgressDialog.setCanceledOnTouchOutside(false);
                    rgProgressDialog.show();

                    register_user(displayName, displayEmail, displayPassword);
                }


            }
        });
    }

    private void register_user(final String displayName, String displayEmail, String displayPassword) {
        mAuth.createUserWithEmailAndPassword(displayEmail, displayPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser current_user = mAuth.getCurrentUser();
                    assert current_user != null;
                    String uid = current_user.getUid();
                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", uid);
                    hashMap.put("device_token", device_token);
                    hashMap.put("name", displayName);
                    hashMap.put("status", "Hi there, I'm using Quyet Chat App");
                    hashMap.put("image", "Default");
                    hashMap.put("thumb_image", "Default");


                    mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                rgProgressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });
                } else {
                    rgProgressDialog.hide();
                    String Exception = "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.";
                    if (Exception.equals(task.getException().toString())) {
                        Toast.makeText(getApplicationContext(), "Account already exists!", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
