package com.triet.quyetchat.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.triet.quyetchat.R;

public class StartActivity extends AppCompatActivity {

    Button mRegBtn, mloginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();
        addListener();
    }
    private void init() {

        mRegBtn = (Button) findViewById(R.id.btn_reg);
        mloginBtn = (Button) findViewById(R.id.btn_login);

    }


    private void addListener() {
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent rg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(rg_intent);
            }
        });

        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent lg_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(lg_intent);

            }
        });
   }


}
