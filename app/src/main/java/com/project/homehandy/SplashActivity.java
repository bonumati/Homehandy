package com.project.homehandy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.homehandy.admin.AdminActivity;
import com.project.homehandy.user.HomeActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    String uid;
    Intent intent;
    FirebaseUser curUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (curUser != null) {
                uid = curUser.getUid();
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        db.collection("Users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    if (Objects.equals(document.get("type"), "user")) {
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                    } else if (Objects.equals(document.get("type"), "admin")) {
                        intent = new Intent(SplashActivity.this, AdminActivity.class);
                    }
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }).addOnFailureListener(err -> {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        }, 2000);
    }
}