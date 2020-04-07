package client;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import com.example.client.R;

public class MainActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        loginFragment = (LoginFragment) fragmentManager.findFragmentById(R.id.login_layout);
        if (loginFragment == null) {
            loginFragment = createLoginFragment("This is my title");
            fragmentManager.beginTransaction()
                    .add(R.id.main_frame_layout, loginFragment)
                    .commit();
        }

        Iconify.with(new FontAwesomeModule());
    }

    private LoginFragment createLoginFragment(String title) {
        LoginFragment fragment = new LoginFragment();

        Bundle args = new Bundle();
        args.putString(LoginFragment.ARG_TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    private MapFragment createMapFragment() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public void switchToMapFragment () {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = createMapFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, mapFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}