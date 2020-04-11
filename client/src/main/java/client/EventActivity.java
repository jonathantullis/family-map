package client;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import com.example.client.R;

import _model.Event;

public class EventActivity extends AppCompatActivity {
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = createMapFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.event_frame_layout, mapFragment)
                    .commit();
        }

        Iconify.with(new FontAwesomeModule());
    }

    private MapFragment createMapFragment() {
        Event event = new Gson().fromJson(getIntent().getExtras().get("event").toString(), Event.class);
        MapFragment fragment = event == null ? new MapFragment() : new MapFragment(event);
        return fragment;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}