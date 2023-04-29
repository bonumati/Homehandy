package com.project.homehandy.user;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.homehandy.R;
import com.project.homehandy.model.Cart;
import com.project.homehandy.model.Service;

public class HomeFragment extends Fragment {
    private FirestoreRecyclerAdapter<Service, RecyclerView.ViewHolder> adapter;
    FirebaseFirestore db;
    FirebaseUser curUser;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public HomeFragment() {
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
        View main_view = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();
        curUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView recyclerView = main_view.findViewById(R.id.allServiceRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Services")
                .orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Service> options = new FirestoreRecyclerOptions.Builder<Service>()
                .setQuery(query, Service.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Service, RecyclerView.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Service service) {
                    ServiceViewHolder serviceViewHolder = (ServiceViewHolder) holder;
                    serviceViewHolder.setImage(service.getName());
                    serviceViewHolder.setName(service.getName());
                    serviceViewHolder.setDescription(service.getDescription());
                    serviceViewHolder.setRegularPrice(service.getRegular_price());
                    serviceViewHolder.setAdvancePrice(service.getAdvance_price());
                    serviceViewHolder.setId(service.getId());
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item_selectable, parent, false);
                    return new ServiceViewHolder(view);
            }

            @Override
            public int getItemViewType(int position) {
                return TYPE_ITEM;
            }
        };
        recyclerView.setAdapter(adapter);

        return main_view;
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

    private class ServiceViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String id, name, description, reg_price, adv_price;
        CardView service_card;

        ServiceViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            service_card = view.findViewById(R.id.service);
            service_card.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), ServiceActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                intent.putExtra("reg_price", reg_price);
                intent.putExtra("adv_price", adv_price);
                startActivity(intent);
            });
        }

        void setId(String id) {
            this.id = id;
        }

        void setImage(String name) {
            ImageView image = view.findViewById(R.id.image);
            StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("service_images/" + name + ".png");
            imageStorageReference.getDownloadUrl().addOnSuccessListener(downloadUrl -> Glide.with(getContext()).load(downloadUrl.toString()).into(image)).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR", e.toString());
                }
            });
        }

        void setName(String name) {
            this.name = name;
            TextView name_tv = view.findViewById(R.id.name);
            name_tv.setText(name);
        }

        void setRegularPrice(String regular_price) {
            this.reg_price = regular_price;
        }

        void setAdvancePrice(String advance_price) {
            this.adv_price = advance_price;
        }

        void setDescription(String description) {
            this.description = description;
        }
    }
}