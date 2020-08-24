package com.example.fesco.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.fesco.R;
import com.exmaple.fesco.Login.LoginActivity;
import com.exmaple.fesco.Login.SQLiteHandler;
import com.exmaple.fesco.Login.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    TextView name_top;
    EditText name;
    EditText email;
    EditText phone;
    EditText address;

    SQLiteHandler db;
    SessionManager sessionManager;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupViews();
        setupToolbar();

        db=new SQLiteHandler(this);
        sessionManager=new SessionManager(this);
        HashMap<String, String> user=db.getUserDetails();


        if(sessionManager.isLoggedIn()) {
            String user_name = user.get("name");
            String user_email = user.get("email");
            String user_address = user.get("address");
            String user_phone = user.get("phone");

            name.setText(user_name, TextView.BufferType.EDITABLE);
            address.setText(user_address, TextView.BufferType.EDITABLE);
            phone.setText(user_phone, TextView.BufferType.EDITABLE);
            email.setText(user_email, TextView.BufferType.EDITABLE);

        }else{
            Snackbar.make(scrollView, "شما هنوز وارد حساب کاربری خود نشده اید", Snackbar.LENGTH_INDEFINITE).setAction("ورود", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }
            });
        }
    }

    private  void setupViews(){
        name_top=findViewById(R.id.designation);
        name=findViewById(R.id.name_edit_text);
        email=findViewById(R.id.email_edit_text);
        phone=findViewById(R.id.phone_edit_text);
        address=findViewById(R.id.address_edit_text);
        scrollView=findViewById(R.id.scroll_view);
    }

    public void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_orange)));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
