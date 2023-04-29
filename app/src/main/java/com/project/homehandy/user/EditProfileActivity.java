package com.project.homehandy.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.homehandy.R;
import com.project.homehandy.admin.AddServiceActivity;

import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    TextInputEditText name_edit_text, email_edit_text, address_edit_text, pincode_edit_text;
    Button submit_btn;

    FirebaseUser curUser;
    FirebaseFirestore db;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser();

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        name_edit_text = findViewById(R.id.name_edit_text);
        email_edit_text = findViewById(R.id.email_edit_text);
        address_edit_text = findViewById(R.id.address_edit_text);
        pincode_edit_text = findViewById(R.id.pincode_edit_text);
        submit_btn = findViewById(R.id.submit_btn);

        db.collection("Users").document(curUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        if(map.get("name") != null) name_edit_text.setText(map.get("name").toString());
                        if(map.get("email") != null) email_edit_text.setText(map.get("email").toString());
                        if(map.get("address") != null) address_edit_text.setText(map.get("address").toString());
                        if(map.get("pincode") != null) pincode_edit_text.setText(map.get("pincode").toString());
                    }
                }
            }
        });

        submit_btn.setOnClickListener(view -> {
            dialog.show();
            String _name = name_edit_text.getText().toString();
            String _email = email_edit_text.getText().toString();
            String _address = address_edit_text.getText().toString();
            String _pincode = pincode_edit_text.getText().toString();

            if(_name.equals("") || _email.equals("") ||_address.equals("") ||_pincode.equals("")){
                Toast.makeText(this, "All fields are compulsory", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(_name)
                    .build();

            curUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    db.collection("Users").document(curUser.getUid())
                            .update("name", _name, "mobile", curUser.getPhoneNumber(), "email", _email, "address", _address, "pincode", _pincode)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                }
            }).addOnFailureListener(e -> dialog.dismiss());
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}