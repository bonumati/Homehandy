package com.project.homehandy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    Button send_btn;
    TextInputEditText et_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        send_btn = findViewById(R.id.send_otp);
        et_mobile = findViewById(R.id.mobile_edit_text);

        send_btn.setOnClickListener(view -> {
            String mobile = et_mobile.getText().toString().trim();

            if(mobile.isEmpty() || mobile.length() < 10){
                et_mobile.setError("Enter a valid mobile");
                et_mobile.requestFocus();
                return;
            }

            Intent login = new Intent(LoginActivity.this, VerificationActivity.class);
            login.putExtra("mobile", mobile);
            startActivity(login);
        });
    }

}