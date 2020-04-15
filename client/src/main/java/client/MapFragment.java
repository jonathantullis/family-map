package client;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
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

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import com.example.client.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import _model.Event;
import _model.Person;
import process.Data;
import process.Filter;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private final float DEFAULT_LINE_WIDTH = 8;
    private DataCache dataCache = DataCache.getInstance();
    private GoogleMap map;
    private View view;
    private Person selectedPerson = null;
    private ArrayList<Marker> mapMarkers = new ArrayList<>();
    private Marker selectedMarker;
    private Polyline spouseLine;
    private ArrayList<Polyline> familyTreeLines = new ArrayList<>();
    private ArrayList<Polyline> lifeStoryLines = new ArrayList<>();

    public MapFragment (Event selectedEvent) {
        dataCache.setSelectedEvent(selectedEvent);
    }
    public MapFragment () {}

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
            Intent intent = new Intent(this.getContext(), PersonActivity.class);
            intent.putExtra("person", new Gson().toJson(selectedPerson));
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        Filter.filterEventsBasedOnSettings();
        validateSelectedEvent();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        addEventMarkers();
        drawLines();
        setDefaultInfoView();

        map.setOnMarkerClickListener(marker -> {
            selectMarker(marker);
            return true;
        });

        if (dataCache.selectedEvent() != null) {
            selectMarker(dataCache.selectedEvent());
        }
    }

    // If it's not in the list of filtered events then it shouldn't be selected
    private void validateSelectedEvent() {
        if (dataCache.selectedEvent() != null) {
            for (Event event : dataCache.allEventsFiltered()) {
                if (event.getEventID().equals(dataCache.selectedEvent().getEventID())) {
                    return;
                }
            }
        }
        dataCache.setSelectedEvent(null);
    }

    private void setDefaultInfoView() {
        ImageView genderImageView = view.findViewById(R.id.info_panel_icon);
        genderImageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                colorRes(R.color.greyDark).sizeDp(50));

        TextView textView = view.findViewById(R.id.info_panel_title);
        textView.setText("Click on a marker to see event details");
    }

    private void setEventInfoView(Event event) {
        // Find the person associated with the event
        Person person = null;
        for (Person item : dataCache.allPersons()) {
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
                    colorRes(R.color.male).sizeDp(40));
        } else {
            genderImageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.female).sizeDp(40));
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
        ArrayList<Event> events = dataCache.allEventsFiltered();
        ArrayList<Float> colors = new ArrayList<>();
        colors.add(BitmapDescriptorFactory.HUE_ORANGE);
        colors.add(BitmapDescriptorFactory.HUE_ROSE);
        colors.add(BitmapDescriptorFactory.HUE_AZURE);
        colors.add(BitmapDescriptorFactory.HUE_BLUE);
        colors.add(BitmapDescriptorFactory.HUE_CYAN);
        colors.add(BitmapDescriptorFactory.HUE_GREEN);
        colors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colors.add(BitmapDescriptorFactory.HUE_RED);
        colors.add(BitmapDescriptorFactory.HUE_VIOLET);
        colors.add(BitmapDescriptorFactory.HUE_YELLOW);

        int numColorsUsed = 0;
        Marker marker = null;
        HashMap<String, Float> eventColors = new HashMap<>(); // KEY: eventType   VAL: color

        for (Event event : events) {
            float color;
            if (eventColors.containsKey(event.getEventType().toUpperCase())) {
                color = eventColors.get(event.getEventType().toUpperCase());
            } else {
                eventColors.put(event.getEventType().toUpperCase(), colors.get(numColorsUsed % colors.size()));
                numColorsUsed++;
                color = eventColors.get(event.getEventType().toUpperCase());
            }

            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .title(event.getCity() + ", " + event.getCountry())
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
            );

            marker.setTag(event);
            mapMarkers.add(marker);
        }
    }

    private void drawLines() {
        // Remove any existing lines before drawing new ones
        removeAllLines();
        if (this.selectedPerson != null) {
            if (dataCache.settings().showSpouseLines()) {
                drawSpouseLines(selectedPerson.getSpouseID());
            }
            if (dataCache.settings().showFamilyTreeLines()) {
                drawFamilyTreeLines();
            }
            if (dataCache.settings().showLifeStoryLines()) {
                drawLifeStoryLines();
            }
        }
    }

    private void drawSpouseLines(String spouseID) {
        // Find the selected person's spouse
        Person spouse = Data.getPerson(spouseID, dataCache.allPersons());
        if (spouse != null) {
            // Find the marker for the EARLIEST event of the spouse
            Marker earliestEventMarker = getEarliestEventMarker(spouse.getPersonID(), mapMarkers);
            if (earliestEventMarker == null) {
                return;
            }

            spouseLine = map.addPolyline(new PolylineOptions()
                    .add(earliestEventMarker.getPosition(), selectedMarker.getPosition())
                    .width(DEFAULT_LINE_WIDTH)
                    .color(Color.rgb(194, 18, 204))
            );
        }
    }

    private void drawLifeStoryLines() {
        ArrayList<Event> events = Data.getAllEvents(selectedPerson.getPersonID(), dataCache.allEventsFiltered());
        Collections.sort(events, new Event.YearComparator());
        Marker lastEventMarker = null;
        for (Event event : events) {
            Marker eventMarker = getAssociatedMarker(event, mapMarkers);
            if (lastEventMarker != null) {
                if (eventMarker != null) {
                    Polyline line = map.addPolyline(new PolylineOptions()
                            .add(eventMarker.getPosition(), lastEventMarker.getPosition())
                            .width(DEFAULT_LINE_WIDTH)
                            .color(Color.rgb(28, 168, 138))
                    );
                    lifeStoryLines.add(line);
                }
            }
            lastEventMarker = eventMarker;
        }
    }

    private void drawFamilyTreeLines() {
        drawFamilyTreeLinesRecursive(selectedPerson, selectedMarker, DEFAULT_LINE_WIDTH * 2);
    }

    private void drawFamilyTreeLinesRecursive(Person currentPerson, Marker currentMarker, float lineWidth) {
        if (currentPerson == null || currentMarker == null) {
            return;
        }

        Person mother = Data.getPerson(currentPerson.getMotherID(), dataCache.allPersons());
        Person father = Data.getPerson(currentPerson.getFatherID(), dataCache.allPersons());
        Marker motherEventMarker = getEarliestEventMarker(currentPerson.getMotherID(), mapMarkers);
        Marker fatherEventMarker = getEarliestEventMarker(currentPerson.getFatherID(), mapMarkers);

        drawFamilyTreeLinesRecursive(mother, motherEventMarker, lineWidth / 2);
        drawFamilyTreeLinesRecursive(father, fatherEventMarker, lineWidth / 2);

        // Draw lines and return
        if (motherEventMarker != null) {
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(motherEventMarker.getPosition(), currentMarker.getPosition())
                    .color(Color.rgb(13, 71, 161))
                    .width(lineWidth)
            );
            familyTreeLines.add(line);
        }
        if (fatherEventMarker != null) {
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(fatherEventMarker.getPosition(), currentMarker.getPosition())
                    .color(Color.rgb(13, 71, 161))
                    .width(lineWidth)
            );
            familyTreeLines.add(line);
        }
    }

    private Marker getEarliestEventMarker(String personID, List<Marker> list) {
        if (personID == null || list == null) {
            return null;
        }
        Marker earliestMarker = null;
        for (Marker marker : list) {
            Event event = (Event) marker.getTag();
            assert event != null;
            if (personID.equals(event.getPersonID())) {
                if (earliestMarker == null) {
                    earliestMarker = marker;
                } else {
                    Event earliestMarkerEvent = (Event) earliestMarker.getTag();
                    assert earliestMarkerEvent != null;
                    if (earliestMarkerEvent.getYear() > event.getYear()) {
                        earliestMarker = marker;
                    }
                }
            }
        }
        return earliestMarker;
    }

    private Marker getAssociatedMarker(Event event, List<Marker> markers) {
        if (event == null || markers == null) {
            return null;
        }
        Marker result = null;
        for (Marker marker : markers) {
            Event markerEvent = (Event) marker.getTag();
            assert markerEvent != null;
            if (markerEvent.getEventID().equals(event.getEventID())) {
                result = marker;
                break;
            }
        }
        return result;
    }

    private void removeAllLines () {
        // Spouse lines
        if (spouseLine != null) {
            spouseLine.remove();
        }

        // Family Tree lines
        for (Polyline line : familyTreeLines) {
            line.remove();
        }
        familyTreeLines = new ArrayList<>();

        // Life Story lines
        for (Polyline line : lifeStoryLines) {
            line.remove();
        }
        lifeStoryLines = new ArrayList<>();
    }

    public void selectMarker(Event event) {
        for (Marker marker : mapMarkers) {
            Event markerEvent = (Event) marker.getTag();
            assert markerEvent != null && event != null;
            if (markerEvent.getEventID().equals(event.getEventID())) {
                selectMarker(marker);
            }
        }
    }

    private void selectMarker(Marker marker) {
        selectedMarker = marker;
        dataCache.setSelectedEvent((Event) marker.getTag());
        assert dataCache.selectedEvent() != null;
        setEventInfoView(dataCache.selectedEvent());
        marker.showInfoWindow();
        drawLines();
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onMapLoaded() {
    }
}
