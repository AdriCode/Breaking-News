package com.example.android.newsfeed;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;

public class MainActivity extends AppCompatActivity{

    private Fragment fragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new NewsFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
