package com.project.homehandy.admin;

import android.os.Bundle;
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
import com.project.homehandy.model.Employee;
import com.project.homehandy.model.User;

public class EmployeesFragment extends Fragment {
    private FirestoreRecyclerAdapter<Employee, EmployeeViewHolder> adapter;

    public EmployeesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.employeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Employees");

        FirestoreRecyclerOptions<Employee> options = new FirestoreRecyclerOptions.Builder<Employee>()
                .setQuery(query, Employee.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Employee, EmployeeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position, @NonNull Employee employee) {
                holder.setId(employee.getId());
                holder.setName(employee.getName());
                holder.setMobile(employee.getMobile());
                holder.setAddress(employee.getAddress());
                holder.setJobTitle(employee.getJob_title());
            }

            @NonNull
            @Override
            public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item, parent, false);
                return new EmployeeViewHolder(view);
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

    private class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String id;

        EmployeeViewHolder(View itemView) {
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

        void setAddress(String address) {
            TextView address_tv = view.findViewById(R.id.address);
            address_tv.setText("Address : " + address);
        }

        void setMobile(String mobile) {
            TextView mobile_tv = view.findViewById(R.id.mobile);
            mobile_tv.setText("Contact Number : " + mobile);
        }

        void setJobTitle(String jobTitle) {
            TextView job_tv = view.findViewById(R.id.job_title);
            job_tv.setText("Job Title : " + jobTitle);
        }
    }
}