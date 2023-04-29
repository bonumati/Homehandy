package com.project.homehandy.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.homehandy.LoginActivity;
import com.project.homehandy.R;

public class DashboardFragment extends Fragment {
    Button allServices_btn, logout_btn;
    FirebaseFirestore db;
    TextView order_count_tv, customer_count_tv;
    int order_count = 0, customer_count = 0;
    CardView orders_card, customers_card;

    FirebaseAuth mAuth;

    public DashboardFragment() {
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
        View view =inflater.inflate(R.layout.fragment_dashboard, container, false);

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        orders_card = view.findViewById(R.id.totalOrdersCard);
        customers_card = view.findViewById(R.id.totalCustomersCard);
        logout_btn = view.findViewById(R.id.logout);

        orders_card.setOnClickListener(view12 -> {
            ((AdminActivity)getActivity()).setCurrentTab("orders");
        });

        customers_card.setOnClickListener(view12 -> {
            ((AdminActivity)getActivity()).setCurrentTab("customers");
        });

        logout_btn.setOnClickListener(view13 -> {
            dialog.show();
            mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            dialog.dismiss();
        });

        order_count_tv = view.findViewById(R.id.orders_count_tv);
        customer_count_tv = view.findViewById(R.id.customer_count_tv);

        db = FirebaseFirestore.getInstance();

        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    if(document.get("type").equals("user")) {
                        customer_count++;
                    }
                }
                customer_count_tv.setText(String.valueOf(customer_count));
            }
        });

        db.collection("Orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    order_count++;
                }
                order_count_tv.setText(String.valueOf(order_count));
            }
        });

        allServices_btn = view.findViewById(R.id.all_services_btn);

        allServices_btn.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), AllServicesActivity.class)));
        return view;
    }
}