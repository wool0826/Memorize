package com.example.memorize;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private FragmentAdd fragmentAdd = new FragmentAdd();
    private FragmentStudy fragmentStudy = new FragmentStudy();
    private FragmentSetting fragmentSetting = new FragmentSetting();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switchFragment(transaction, item.getItemId());
                return true;
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        addFragmentToFrame(transaction);
        switchFragment(transaction);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        fragmentAdd.onActivityResult(requestCode,resultCode,data);
    }

    void addFragmentToFrame(FragmentTransaction transaction){
        transaction.add(R.id.mainFrame, fragmentAdd);
        transaction.add(R.id.mainFrame, fragmentStudy);
        transaction.add(R.id.mainFrame, fragmentSetting);
    }
    void switchFragment(FragmentTransaction transaction){
        switchFragment(transaction, R.id.navigation_study);
    }
    void switchFragment(FragmentTransaction transaction, int targetId){
        transaction.hide(fragmentStudy);
        transaction.hide(fragmentAdd);
        transaction.hide(fragmentSetting);

        switch(targetId){
            case R.id.navigation_study:
                transaction.show(fragmentStudy);
                break;
            case R.id.navigation_add:
                transaction.show(fragmentAdd);
                break;
            case R.id.navigation_setting:
                transaction.show(fragmentSetting);
                break;
        }

        transaction.commit();
    }
}
