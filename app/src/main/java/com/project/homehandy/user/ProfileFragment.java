package com.project.homehandy.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.homehandy.LoginActivity;
import com.project.homehandy.R;
import com.project.homehandy.admin.OrderFragment;
import com.project.homehandy.model.Order;

import java.util.Collections;
import java.util.Map;

public class ProfileFragment extends Fragment {
    Button logout_btn;
    TextView name_tv, mobile_tv, email_tv, edit_tv, address_tv;
    FirebaseAuth mAuth;
    FirebaseUser curUser;
    FirebaseFirestore db;

    RecyclerView recyclerView;
    Query query;

    private FirestoreRecyclerAdapter<Order, OrderViewHolder> adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        name_tv = view.findViewById(R.id.name);
        mobile_tv = view.findViewById(R.id.mobile);
        email_tv = view.findViewById(R.id.email);
        address_tv = view.findViewById(R.id.address);
        edit_tv = view.findViewById(R.id.edit);

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        query = rootRef.collection("Orders").whereEqualTo("user_id", curUser.getUid()); // .orderBy("created_at", Query.Direction.ASCENDING);
        db.collection("Users").document(curUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> map = document.getData();
                    if (map.get("name") != null) name_tv.setText(map.get("name").toString());
                    if (map.get("mobile") != null) mobile_tv.setText(map.get("mobile").toString());
                    if (map.get("email") != null) email_tv.setText(map.get("email").toString());
                    if (map.get("address") != null && map.get("pincode") != null) {
                        address_tv.setText("Address : " + map.get("address") + " " + map.get("pincode"));
                    }
                }
            }
        });

        edit_tv.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        logout_btn = view.findViewById(R.id.logout);
        logout_btn.setOnClickListener(view1 -> {
            dialog.show();
            mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            dialog.dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order order) {
                holder.setId(order.getId());
                holder.setServices(order.getServices());
                holder.setTotal(order.getCart_total());
                holder.setPaymentMode(order.getPayment_mode());
                holder.setStatus(order.getStatus());
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_order_item, parent, false);
                return new OrderViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private class OrderViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String id, services, total, payment_mode,status;
        TextView rating_tv;
        Button rate_btn;
        LinearLayout rating_container;

        OrderViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            rating_tv = view.findViewById(R.id.rating_text);
            rate_btn = view.findViewById(R.id.rate);
            rating_container = view.findViewById(R.id.rating_container);

            RatingBar simpleRatingBar = view.findViewById(R.id.rating);

            rate_btn.setOnClickListener(view -> {
                db.collection("Orders").document(id)
                        .update("rating", simpleRatingBar.getRating(), "status", "rated")
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        });
            });
        }

        void setId(String id) {
            this.id = id;
        }

        public void setStatus(String status) {
            this.status = status;

            if (status.equals("completed")) {
                rating_tv.setVisibility(View.VISIBLE);
                rating_container.setVisibility(View.VISIBLE);
            } else {
                rating_tv.setVisibility(View.GONE);
                rating_container.setVisibility(View.GONE);
            }

            TextView status_tv = view.findViewById(R.id.status);
            status_tv.setText("Order Status : " + status);
        }

        public void setServices(String services) {
            this.services = services;
            TextView services_tv = view.findViewById(R.id.services);
            services_tv.setText("Service : " + services);
        }

        public void setTotal(String total) {
            this.total = total;
            TextView total_tv = view.findViewById(R.id.total);
            total_tv.setText("Amount : " + total + " Rs.");
        }

        public void setPaymentMode(String payment_mode) {
            this.payment_mode = payment_mode;
            TextView payment_mode_tv = view.findViewById(R.id.paymentMode);
            payment_mode_tv.setText("Payment Mode : " + payment_mode);
        }
    }
}