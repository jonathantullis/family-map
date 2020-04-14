package client;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import com.example.client.R;

import _model.Event;

public class EventActivity extends AppCompatActivity {
    private MapFragment mapFragment;
    private DataCache dataCache = DataCache.getInstance();
    private Event originalEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        originalEvent = dataCache.selectedEvent();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        dataCache.setSelectedEvent(originalEvent);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return false;
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