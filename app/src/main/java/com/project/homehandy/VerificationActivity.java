package com.project.homehandy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.homehandy.admin.AdminActivity;
import com.project.homehandy.model.User;
import com.project.homehandy.user.EditProfileActivity;
import com.project.homehandy.user.HomeActivity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    private String TAG = "Error";

    private String mVerificationId;
    String uid, mobile;
    Button verify_btn;
    ImageButton back_btn;
    TextInputEditText editTextCode;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextCode = findViewById(R.id.otp_edit_text);
        verify_btn = findViewById(R.id.verify);
        back_btn = findViewById(R.id.back);

        back_btn.setOnClickListener(view -> finish());

        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
         sendVerificationCode(mobile);

         verify_btn.setEnabled(false);
        verify_btn.setOnClickListener(v -> {
            if (!mVerificationId.isEmpty()) {
                String code = editTextCode.getText().toString().trim();

                if(code.isEmpty() || code.length() < 6){
                    editTextCode.setError("Enter a valid OTP");
                    editTextCode.requestFocus();
                    return;
                } else {
                    verifyVerificationCode(code);
                }
            } else {
                Toast.makeText(VerificationActivity.this, "please wait", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                120,
                TimeUnit.SECONDS,
                // TaskExecutors.MAIN_THREAD,
                VerificationActivity.this,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(VerificationActivity.this, "code sent", Toast.LENGTH_SHORT).show();
            //storing the verification id that is sent to the user
            mVerificationId = s;
            verify_btn.setEnabled(true);
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        if (code != null) {
            if (!code.isEmpty()) {
                dialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();

                            FirebaseUser curUser = mAuth.getInstance().getCurrentUser();
                            if (curUser != null)
                                uid = curUser.getUid();

                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener((OnCompleteListener<String>) task1 -> {

                                if (!task1.isSuccessful()) {
                                    return;
                                }

                                String token = task1.getResult();

                                DocumentReference docIdRef = db.collection("Users").document(uid);
                                docIdRef.get().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        DocumentSnapshot document = task2.getResult();
                                        if (!document.exists()) {
                                            User user = new User(
                                                    mobile,
                                                    token,
                                                    "user"
                                            );
                                            db.collection("Users")
                                                    .document(uid)
                                                    .set(user);

                                            Intent intent = new Intent(VerificationActivity.this, EditProfileActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            if (Objects.equals(document.get("type"), "user")) {
                                                Intent intent = new Intent(VerificationActivity.this, EditProfileActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else if (Objects.equals(document.get("type"), "admin")) {
                                                Intent intent = new Intent(VerificationActivity.this, AdminActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(VerificationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }).addOnFailureListener(e -> Log.e(TAG, "Failed to get the token : " + e.getLocalizedMessage()));

                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Toast.makeText(VerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }
}