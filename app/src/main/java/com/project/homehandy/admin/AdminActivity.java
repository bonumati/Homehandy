package com.project.homehandy.admin;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.homehandy.R;

public class AdminActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener  {

    BottomNavigationView bottomNavigationView;
    final DashboardFragment dashboardFragment = new DashboardFragment();
    final OrderFragment orderFragment = new OrderFragment();
    final CustomersFragment customersFragment = new CustomersFragment();
    final EmployeesFragment employeesFragment = new EmployeesFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_dashboard);

        fm.beginTransaction().add(R.id.fragment, dashboardFragment, "3").commit();
        fm.beginTransaction().add(R.id.fragment, orderFragment, "2").hide(orderFragment).commit();
        fm.beginTransaction().add(R.id.fragment, customersFragment, "1").hide(customersFragment).commit();
        fm.beginTransaction().add(R.id.fragment, employeesFragment, "4").hide(employeesFragment).commit();
    }

    public void setCurrentTab(String changeTo){
        switch (changeTo) {
            case "dashboard":
                fm.beginTransaction().hide(active).show(dashboardFragment).commit();
                active = dashboardFragment;
                break;
            case "orders":
                fm.beginTransaction().hide(active).show(orderFragment).commit();
                active = orderFragment;
                break;
            case "customers":
                fm.beginTransaction().hide(active).show(customersFragment).commit();
                active = customersFragment;
                break;
            case "employees":
                fm.beginTransaction().hide(active).show(employeesFragment).commit();
                active = employeesFragment;
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_dashboard:
                fm.beginTransaction().hide(active).show(dashboardFragment).commit();
                active = dashboardFragment;
                return true;

            case R.id.menu_orders:
                fm.beginTransaction().hide(active).show(orderFragment).commit();
                active = orderFragment;
                return true;

            case R.id.menu_customer:
                fm.beginTransaction().hide(active).show(customersFragment).commit();
                active = customersFragment;
                return true;

            case R.id.menu_employees:
                fm.beginTransaction().hide(active).show(employeesFragment).commit();
                active = employeesFragment;
                return true;
        }
        return false;
    }
}