package com.project.homehandy.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.homehandy.R;
import com.project.homehandy.model.Service;

public class AllServicesActivity extends AppCompatActivity {
    FloatingActionButton addService_fab;
    private FirestoreRecyclerAdapter<Service, ServiceViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Services")
                .orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Service> options = new FirestoreRecyclerOptions.Builder<Service>()
                .setQuery(query, Service.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Service, ServiceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ServiceViewHolder holder, int position, @NonNull Service service) {
                holder.setName(service.getName());
                holder.setDesc(service.getDescription());
                holder.setRegularPrice(service.getRegular_price());
                holder.setAdvancePrice(service.getAdvance_price());
                holder.setId(service.getId());
            }

            @NonNull
            @Override
            public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);
                return new ServiceViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        addService_fab = findViewById(R.id.addServiceFab);
        addService_fab.setOnClickListener(view -> startActivity(new Intent(AllServicesActivity.this, AddServiceActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private class ServiceViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String id, name, desc, reg_price, adv_price;

        ServiceViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(view -> {
                Intent intent = new Intent(AllServicesActivity.this, AddServiceActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("description", desc);
                intent.putExtra("regular_price", reg_price);
                intent.putExtra("advance_price", adv_price);
                startActivity(intent);
            });
        }

        void setId(String id) {
            this.id = id;
        }

        void setName(String name) {
            this.name = name;
            TextView name_tv = view.findViewById(R.id.name);
            name_tv.setText(name);
        }

        void setDesc(String desc) {
            this.desc = desc;
        }

        void setRegularPrice(String regular_price) {
            this.reg_price = regular_price;
            TextView regular_price_tv = view.findViewById(R.id.reqular_price);
            regular_price_tv.setText("Regular Price = " + regular_price + " Rs.");
        }

        void setAdvancePrice(String advance_price) {
            this.adv_price = advance_price;
            TextView advance_price_tv = view.findViewById(R.id.advance_price);
            advance_price_tv.setText("Advance Price = " + advance_price + " Rs.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}