package com.triet.quyetchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout edt_status;
    private Button btn_editStatus;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = (Toolbar) findViewById(R.id.status_bar);
        edt_status = (TextInputLayout) findViewById(R.id.edt_status);
        btn_editStatus = (Button) findViewById(R.id.btn_Editstatus);
        String status_value = getIntent().getStringExtra("Status_value");
        edt_status.getEditText().setText(status_value);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_editStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = new ProgressDialog(StatusActivity.this);
                mDialog.setTitle("Saving Changes");
                mDialog.setMessage("Please wait while we save the changes!");
                mDialog.show();
                String status = edt_status.getEditText().getText().toString();
                mDatabaseReference.child("status").setValue(status)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mDialog.dismiss();
                                }else{
                                    Toast.makeText(getApplicationContext(), "There was some error in saving Changes", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}
