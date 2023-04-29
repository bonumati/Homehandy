package com.project.homehandy.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.homehandy.R;
import com.project.homehandy.admin.AddServiceActivity;
import com.project.homehandy.admin.AllServicesActivity;
import com.project.homehandy.model.Cart;
import com.project.homehandy.model.Order;
import com.project.homehandy.model.Service;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartFragment extends Fragment {
    private FirestoreRecyclerAdapter<Cart, CartViewHolder> adapter;

    Integer total = 0;
    String payment_mode = "cod";
    TextView cart_total, address;
    Button checkout_btn;
    RadioGroup radioGroup;

    FirebaseUser curUser;
    FirebaseFirestore db;
    Query query;

    List<String> string_arr = new ArrayList<String>();
    LinearLayout cart_empty;
    RelativeLayout cart_container;
    RecyclerView recyclerView;

    public CartFragment() {
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cart_empty = view.findViewById(R.id.cartEmpty);
        cart_container = view.findViewById(R.id.cart_container);
        cart_total = view.findViewById(R.id.cart_total);
        checkout_btn = view.findViewById(R.id.checkout);
        address = view.findViewById(R.id.address);

        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        query = rootRef.collection("Carts")
                .whereEqualTo("user_id", curUser.getUid());

        db.collection("Users").document(curUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> map = document.getData();
                    if (map.get("address") != null && map.get("pincode") != null) {
                        address.setText("Address : " + map.get("address") + " " + map.get("pincode"));
                    }
                }
            }
        });

        radioGroup = view.findViewById(R.id.price_group);
        radioGroup.setOnCheckedChangeListener((arg0, id) -> {
            switch (id) {
                case R.id.cod:
                    payment_mode = "cod";
                    break;
                case R.id.online:
                    payment_mode = "online";
                    break;
                default:
                    payment_mode = "code";
                    break;
            }
        });

        checkout_btn.setOnClickListener(view1 -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Place Order")
                    .setMessage("Are you sure?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                    .setPositiveButton("Ok", (dialogInterface, i) -> {

                        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            string_arr.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                string_arr.add(doc.getString("service_name") + " - " + doc.getString("service_type") + " - " + doc.getString("date") + " - " + doc.getString("timing"));
                            }
                        });

                        db.collection("Users").document(curUser.getUid()).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    Order order = new Order(
                                            curUser.getUid(),
                                            curUser.getDisplayName(),
                                            document.get("address").toString(),
                                            string_arr.toString(),
                                            total.toString(),
                                            payment_mode,
                                            false,
                                            "pending",
                                            new Date()
                                    );
                                    db.collection("Orders").add(order);

                                    CollectionReference itemsRef = rootRef.collection("Carts");
                                    Query query = itemsRef.whereEqualTo("user_id", curUser.getUid());
                                    query.get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (DocumentSnapshot document1 : task1.getResult()) {
                                                itemsRef.document(document1.getId()).delete();
                                            }
                                            total = 0;
                                        }
                                    });

                                    Toast.makeText(getContext(), "Order Placed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }).show();
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            total = 0;
            string_arr.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                total = total + Integer.parseInt(doc.getString("service_price"));
            }
            cart_total.setText(total.toString());
        });

        FirestoreRecyclerOptions<Cart> options = new FirestoreRecyclerOptions.Builder<Cart>()
                .setQuery(query, Cart.class)
                .build();

        total = 0;
        adapter = new FirestoreRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart cart) {
                holder.setName(cart.getService_name(), cart.getDate(), cart.getTiming());
                holder.setPrice(cart.getService_price());
                holder.setId(cart.getId());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
                return new CartViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        // Watch Cart
        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("TAG", "listen:error", e);
                return;
            }

            int size = snapshots.size();
            if (size > 0) {
                cart_empty.setVisibility(View.GONE);
                cart_container.setVisibility(View.VISIBLE);
            } else {
                cart_container.setVisibility(View.GONE);
                cart_empty.setVisibility(View.VISIBLE);
            }
        });

        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private class CartViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String id, name, price;
        ImageView remove_img;

        CartViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            remove_img = view.findViewById(R.id.remove);
            remove_img.setOnClickListener(view -> {
                db.collection("Carts")
                        .document(id)
                        .delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Service removed", Toast.LENGTH_SHORT).show();
//                            adapter.notifyDataSetChanged();
                            total = total - Integer.parseInt(price);
                            cart_total.setText(total + " Rs.");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        });
            });
        }

        void setId(String id) {
            this.id = id;
        }

        void setName(String name, String date, String time) {
            this.name = name;
            TextView name_tv = view.findViewById(R.id.name);
            name_tv.setText(name);
        }

        void setPrice(String _price) {
            this.price = _price;
            TextView price_tv = view.findViewById(R.id.price);
            price_tv.setText(_price + " Rs.");
//            total = total + Integer.parseInt(_price);
//            cart_total.setText(total + " Rs.");
        }
    }
}