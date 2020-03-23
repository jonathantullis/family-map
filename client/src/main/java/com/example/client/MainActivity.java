package com.example.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        loginFragment = (LoginFragment) fragmentManager.findFragmentById(R.id.login_frame_layout);
        if (loginFragment == null) {
            loginFragment = createAddressFragment("This is my title");
            System.out.println("Inside condition");
            System.out.println("fragment: " + loginFragment);
            fragmentManager.beginTransaction()
                    .add(R.id.login_frame_layout, loginFragment)
                    .commit();
        }
    }

    private LoginFragment createAddressFragment(String title) {
        LoginFragment fragment = new LoginFragment();

        Bundle args = new Bundle();
        args.putString(LoginFragment.ARG_TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}