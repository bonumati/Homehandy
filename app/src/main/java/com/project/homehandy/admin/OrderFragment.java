package com.project.homehandy.admin;

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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.homehandy.R;
import com.project.homehandy.model.Order;
import com.project.homehandy.model.Service;
import com.project.homehandy.user.EditProfileActivity;

public class OrderFragment extends Fragment {
    private FirestoreRecyclerAdapter<Order, OrderViewHolder> adapter;
    TextView order_count;
    FirebaseFirestore db;
    LinearLayout order_empty_container, order_container;

    public OrderFragment() {
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
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        db = FirebaseFirestore.getInstance();

        TextView order_count_tv = view.findViewById(R.id.orders_count_tv);
        order_empty_container = view.findViewById(R.id.order_empty_container);
        order_container = view.findViewById(R.id.order_container);

        // db.collection("Orders").whereEqualTo("delivered", false)

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Orders"); // .orderBy("created_at", Query.Direction.ASCENDING);

        // Watch Cart
        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("TAG", "listen:error", e);
                return;
            }

            int size = snapshots.size();
            if (size > 0) {
                order_empty_container.setVisibility(View.GONE);
                order_container.setVisibility(View.VISIBLE);
            } else {
                order_container.setVisibility(View.GONE);
                order_empty_container.setVisibility(View.VISIBLE);
            }
        });

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order order) {
                holder.setId(order.getId());
                holder.setName(order.getUser_name());
                holder.setServices(order.getServices());
                holder.setAddress(order.getUser_address());
                holder.setTotal(order.getCart_total());
                holder.setPaymentMode(order.getPayment_mode());
                holder.setStatus(order.getStatus());
                holder.setRating(order.getStatus(), order.getRating());
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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
        String id, name, address, services, total, pay_mode, status, rating;
        Button accept_btn, reject_btn, done_btn;

        OrderViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            accept_btn = view.findViewById(R.id.accept);
            reject_btn = view.findViewById(R.id.reject);
            done_btn = view.findViewById(R.id.done);

            accept_btn.setOnClickListener(view -> db.collection("Orders").document(id).update("status", "accepted")
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Order accepted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    })
            );

            reject_btn.setOnClickListener(view -> db.collection("Orders").document(id).update("status", "rejected")
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Order rejected", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }));

            done_btn.setOnClickListener(view -> db.collection("Orders").document(id).update("status", "completed")
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Order completed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }));
        }

        void setId(String id) {
            this.id = id;
        }

        public void setStatus(String status) {
            this.status = status;
            TextView status_tv = view.findViewById(R.id.status);
            status_tv.setText("Order Status : " + status);

            if(status != null) {
                if (status.equals("pending")) {
                    accept_btn.setVisibility(View.VISIBLE);
                    reject_btn.setVisibility(View.VISIBLE);
                    done_btn.setVisibility(View.GONE);
                } else if(status.equals("accepted")) {
                    accept_btn.setVisibility(View.GONE);
                    reject_btn.setVisibility(View.GONE);
                    done_btn.setVisibility(View.VISIBLE);
                } else {
                    accept_btn.setVisibility(View.GONE);
                    reject_btn.setVisibility(View.GONE);
                    done_btn.setVisibility(View.GONE);
                }
            }
        }

        void setRating(String status, Double rating) {
            this.rating = String.valueOf(rating);
            TextView rating_tv = view.findViewById(R.id.rating);
            if(status.equals("rated")) {
                rating_tv.setText("Rated : " + rating);
            } else {
                rating_tv.setVisibility(View.GONE);
            }
        }

        void setName(String name) {
            this.name = name;
            TextView name_tv = view.findViewById(R.id.name);
            name_tv.setText(name);
        }

        public void setServices(String services) {
            this.services = services;
            TextView services_tv = view.findViewById(R.id.services);
            services_tv.setText(services);
        }

        public void setAddress(String address) {
            this.address = address;
            TextView address_tv = view.findViewById(R.id.address);
            address_tv.setText("Address : " + address);
        }

        public void setTotal(String total) {
            this.total = total;
            TextView total_tv = view.findViewById(R.id.total);
            total_tv.setText(total + " Rs.");
        }

        public void setPaymentMode(String pay_mode) {
            this.pay_mode = pay_mode;
            TextView payment_mode_tv = view.findViewById(R.id.paymentMode);
            payment_mode_tv.setText(pay_mode);
        }
    }
}