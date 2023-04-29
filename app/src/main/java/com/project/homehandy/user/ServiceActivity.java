package com.project.homehandy.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.homehandy.R;
import com.project.homehandy.model.Cart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServiceActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser curUser;

    String name, desc, regular_price, advance_price;
    TextView tv_name, tv_desc;
    String price = "", price_type = "Regular Price";
    Button add_to_cart;
    RadioGroup radioGroup;

    String[] timings = {"10am to 2pm", "5pm to 9pm"};
    String timing = timings[1];

    String myFormat="MM/dd/yy";
    final Calendar myCalendar = Calendar.getInstance();
    EditText dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name", "Name");
        desc = bundle.getString("description", "Description");
        regular_price = bundle.getString("reg_price", "regular_price");
        advance_price = bundle.getString("adv_price", "advance_price");
        price = regular_price;

        ImageView image = findViewById(R.id.image);

        StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("service_images/" + name + ".png");
        imageStorageReference.getDownloadUrl().addOnSuccessListener(downloadUrl -> Glide.with(this).load(downloadUrl.toString()).into(image));

        tv_name = findViewById(R.id.name);
        tv_desc = findViewById(R.id.description);

        tv_name.setText(name);
        tv_desc.setText(desc);

        RadioButton regular_price_tv = findViewById(R.id.regular_price);
        regular_price_tv.setText("Regular Price : " + regular_price + " Rs.");

        RadioButton advance_price_tv = findViewById(R.id.advance_price);
        advance_price_tv.setText("Advance Price : " + advance_price + " Rs.");

        radioGroup = findViewById(R.id.price_group);
        radioGroup.setOnCheckedChangeListener((arg0, id) -> {
            switch (id) {
                case R.id.regular_price:
                    price = regular_price;
                    price_type = "Regular Price";
                    break;
                case R.id.advance_price:
                    price = advance_price;
                    price_type = "Advance Price";
                    break;
                default:
                    price = "";
                    price_type = "Regular Price";
                    break;
            }
        });

        add_to_cart = findViewById(R.id.add_to_cart);
        add_to_cart.setOnClickListener(view -> {
            if (price.equals("")) {
                Toast.makeText(this, "select service type", Toast.LENGTH_SHORT).show();
            } else {
                Cart cart = new Cart(curUser.getUid(), curUser.getDisplayName(), name, price_type,"address", price, dateText.getText().toString(), timing);
                db.collection("Carts")
                        .add(cart).addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                });

            }
        });

        // time selector
        Spinner spin = findViewById(R.id.date_spinner);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timing = timings[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                timings);

        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        spin.setAdapter(ad);

        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateText = findViewById(R.id.date);
        dateText.setText(dateFormat.format(new Date()));
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            dateText.setText(dateFormat.format(myCalendar.getTime()));
        };
        dateText.setOnClickListener(view -> new DatePickerDialog(ServiceActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }
}