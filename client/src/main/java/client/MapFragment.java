package client;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import com.example.client.R;

import java.util.ArrayList;
import java.util.HashMap;

import _model.Event;
import _model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private View view;
    private Person selectedPerson = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LinearLayout infoPanel = view.findViewById(R.id.info_panel);
        infoPanel.setOnClickListener((View v) -> {
            if (selectedPerson == null) {
                return;
            }
            // FIXME open person Activity
            Intent intent = new Intent(this.getContext(), PersonActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        addEventMarkers();

        setDefaultInfoView();

        map.setOnMarkerClickListener(marker -> {
            Event event = (Event) marker.getTag();
            assert event != null;
            setEventInfoView(event);
            marker.showInfoWindow();
            return true;
        });
    }

    private void setDefaultInfoView() {
        ImageView genderImageView = view.findViewById(R.id.info_panel_icon);
        genderImageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                colorRes(R.color.colorAccent).sizeDp(50));

        TextView textView = view.findViewById(R.id.info_panel_title);
        textView.setText("Click on a marker to see event details");
    }

    private void setEventInfoView(Event event) {
        // Find the person associated with the event
        ArrayList<Person> allPersons = DataCache.getInstance().getAllPersonsResult().getData();
        Person person = null;
        for (Person item : allPersons) {
            if (item.getPersonID().equals(event.getPersonID())) {
                person = item;
            }
        }
        assert person != null;
        selectedPerson = person;

        // Set the layout data
        ImageView genderImageView = view.findViewById(R.id.info_panel_icon);
        if (person.getGender().toLowerCase().equals("m")) {
            genderImageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                    colorRes(R.color.colorMale).sizeDp(40));
        } else {
            genderImageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.colorFemale).sizeDp(40));
        }

        TextView name = view.findViewById(R.id.info_panel_title);
        String text = person.getFirstName() + " " + person.getLastName();
        name.setText(text);

        TextView typeAndLocation = view.findViewById(R.id.info_panel_text);
        text = event.getEventType().toUpperCase() + ": "
                + event.getCity() + ", "
                + event.getCountry() + " ("
                + event.getYear() + ")";
        typeAndLocation.setText(text);
    }

    private void addEventMarkers() {
        if (map == null) {
            return;
        }
        ArrayList<Event> events = DataCache.getInstance().getAllEventsFiltered();
        ArrayList<Float> colors = new ArrayList<>();
        colors.add(BitmapDescriptorFactory.HUE_BLUE);
        colors.add(BitmapDescriptorFactory.HUE_CYAN);
        colors.add(BitmapDescriptorFactory.HUE_GREEN);
        colors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colors.add(BitmapDescriptorFactory.HUE_RED);
        colors.add(BitmapDescriptorFactory.HUE_VIOLET);
        colors.add(BitmapDescriptorFactory.HUE_YELLOW);
        final float COLOR_DEATH = BitmapDescriptorFactory.HUE_AZURE;
        final float COLOR_MARRIAGE = BitmapDescriptorFactory.HUE_ROSE;
        final float COLOR_BIRTH = BitmapDescriptorFactory.HUE_ORANGE;

        int numColorsUsed = 0;
        Marker marker = null;
        HashMap<String, Float> colorsUsed = new HashMap<>(); // KEY: eventType   VAL: color

        for (Event event : events) {
            float color;
            if (event.getEventType().toLowerCase().equals("birth")) {
                color = COLOR_BIRTH;
            } else if (event.getEventType().toLowerCase().equals("marriage")) {
                color = COLOR_MARRIAGE;
            } else if (event.getEventType().toLowerCase().equals("death")) {
                color = COLOR_DEATH;
            } else {
                if (colorsUsed.containsKey(event.getEventType())) {
                    color = colorsUsed.get(event.getEventType());
                } else {
                    colorsUsed.put(event.getEventType(), colors.get(numColorsUsed % colors.size()));
                    numColorsUsed++;
                    color = colorsUsed.get(event.getEventType());
                }
            }

            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .title(event.getCity() + ", " + event.getCountry())
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
            );

            marker.setTag(event);
        }
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }
}
