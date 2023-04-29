package com.project.homehandy.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.project.homehandy.R;
import com.project.homehandy.model.Service;

public class AddServiceActivity extends AppCompatActivity {
    ImageView service_image;
    Button select_image_btn, submit_btn;
    TextInputEditText service_name_edit_text, service_description_edit_text, regular_price_edit_text, advance_price_edit_text;
    String id, name, desc, reg_price, adv_price;
    FirebaseFirestore db;
    ProgressDialog dialog;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("service_images");

        service_image = findViewById(R.id.service_image);
        select_image_btn = findViewById(R.id.select_image);
        service_name_edit_text = findViewById(R.id.service_name_edit_text);
        service_description_edit_text = findViewById(R.id.service_desc_edit_text);
        regular_price_edit_text = findViewById(R.id.regular_price_edit_text);
        advance_price_edit_text = findViewById(R.id.advance_price_edit_text);

        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        desc = intent.getStringExtra("description");
        reg_price = intent.getStringExtra("regular_price");
        adv_price = intent.getStringExtra("advance_price");

        if(name != null) {
            StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("service_images/" + name + ".png");
            imageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    Glide.with(getApplicationContext()).load(downloadUrl.toString()).into(service_image);
                }
            });
            service_name_edit_text.setText(name);
        }
        if(desc != null) service_description_edit_text.setText(desc);
        if(reg_price != null) regular_price_edit_text.setText(reg_price);
        if(adv_price != null) advance_price_edit_text.setText(adv_price);

        select_image_btn.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setType("image/*");
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent1, IMAGE_REQUEST);
        });

        submit_btn = findViewById(R.id.submit_btn);

        if (id != null) {
            submit_btn.setText("UPDATE");
        }

        submit_btn.setOnClickListener(view -> {
            dialog.show();
            String name = service_name_edit_text.getText().toString();
            String desc = service_description_edit_text.getText().toString();
            String regular_price = regular_price_edit_text.getText().toString();
            String advance_price = advance_price_edit_text.getText().toString();
            Service service = new Service(name, desc, regular_price, advance_price);

            if (id != null) {
                db.collection("Services").document(id).update("name", name, "description", desc, "regular_price", regular_price, "advance_price", advance_price)
                        .addOnSuccessListener(documentReference -> {
                            if(imageUri != null){
                                final StorageReference fileReference = storageReference.child(name.replace("/", "")+".png");
                                uploadTask = fileReference.putFile(imageUri);
                                uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                                    if(!task.isSuccessful()){
                                        throw task.getException();
                                    }
                                    Toast.makeText(AddServiceActivity.this, "Service updated", Toast.LENGTH_SHORT).show();
                                    return fileReference.getDownloadUrl();
                                });
                            }
                            finish();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddServiceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            finish();
                            dialog.dismiss();
                        });
            } else {
                db.collection("Services").add(service)
                        .addOnSuccessListener(documentReference -> {
                            if(imageUri != null){
                                final StorageReference fileReference = storageReference.child(name.replace("/", "")+"."+getFileExtension(imageUri));
                                uploadTask = fileReference.putFile(imageUri);
                                uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                                    if(!task.isSuccessful()){
                                        throw task.getException();
                                    }
                                    Toast.makeText(AddServiceActivity.this, "Service added", Toast.LENGTH_SHORT).show();
                                    return fileReference.getDownloadUrl();
                                });
                            }
                            finish();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddServiceActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            finish();
                            dialog.dismiss();
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            service_image.setImageURI(Uri.parse(String.valueOf(data.getData())));
            /*if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }*/
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /*private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(uid+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    editProfile.update("imageURI", mUri);
                    progressDialog.dismiss();
                    update();
                    Toast.makeText(this, "Changed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }*/
}