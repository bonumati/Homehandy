package com.project.homehandy.user;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.homehandy.R;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    final HomeFragment homeFragment = new HomeFragment();
    final CartFragment cartFragment = new CartFragment();
    final ProfileFragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        fm.beginTransaction().add(R.id.home_fragment, homeFragment, "3").commit();
        fm.beginTransaction().add(R.id.home_fragment, cartFragment, "2").hide(cartFragment).commit();
        fm.beginTransaction().add(R.id.home_fragment, profileFragment, "1").hide(profileFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_home:
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
                return true;
            case R.id.menu_cart:
                fm.beginTransaction().hide(active).show(cartFragment).commit();
                active = cartFragment;
                return true;
            case R.id.menu_profile:
                fm.beginTransaction().hide(active).show(profileFragment).commit();
                active = profileFragment;
                return true;
        }
        return false;
    }
}