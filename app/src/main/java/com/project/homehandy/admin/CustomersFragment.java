package com.project.homehandy.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.homehandy.R;
import com.project.homehandy.model.User;

public class CustomersFragment extends Fragment {
    private FirestoreRecyclerAdapter<User, CustomerViewHolder> adapter;

    public CustomersFragment() {
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
        View view =inflater.inflate(R.layout.fragment_customers, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.customerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Users")
                .whereEqualTo("type", "user");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, CustomerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerViewHolder holder, int position, @NonNull User user) {
                holder.setId(user.getId());
                holder.setMobile(user.getMobile());
                holder.setName(user.getName());
            }

            @NonNull
            @Override
            public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
                return new CustomerViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

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

    private class CustomerViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String id;

        CustomerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setId(String id) {
            this.id = id;
        }

        void setName(String name) {
            TextView name_tv = view.findViewById(R.id.name);
            name_tv.setText(name);
        }

        void setMobile(String mobile) {
            TextView mobile_tv = view.findViewById(R.id.mobile);
            mobile_tv.setText(mobile);
        }
    }
}