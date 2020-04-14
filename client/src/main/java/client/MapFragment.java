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

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private final float DEFAULT_LINE_WIDTH = 12;
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

        filterEvents();
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

    private void filterEvents() {
        ArrayList<Event> filteredEvents = new ArrayList<>();
        // Filter by side of family
        Person user = getPerson(dataCache.userPersonId(), dataCache.allPersons());
        if (dataCache.settings().showFathersSide() && dataCache.settings().showMothersSide()) {
            filteredEvents = dataCache.allEvents();
        } else {
            if (dataCache.settings().showFathersSide()) {
                // Add father's side only
                Person father = getPerson(user.getFatherID(), dataCache.allPersons());
                ArrayList<Person> fathersSide = getPersonsRecursive(father);

                for (Person person : fathersSide) {
                    ArrayList<Event> events = getAllEvents(person.getPersonID(), dataCache.allEvents());
                    filteredEvents.addAll(events);
                }
            } else if (dataCache.settings().showMothersSide()) {
                // Add mother's side only
                Person mother = getPerson(user.getMotherID(), dataCache.allPersons());
                ArrayList<Person> mothersSide = getPersonsRecursive(mother);

                for (Person person : mothersSide) {
                    ArrayList<Event> events = getAllEvents(person.getPersonID(), dataCache.allEvents());
                    filteredEvents.addAll(events);
                }
            }
            // Add the root user and spouse events
            filteredEvents.addAll(getAllEvents(user.getPersonID(), dataCache.allEvents()));
            filteredEvents.addAll(getAllEvents(user.getSpouseID(), dataCache.allEvents()));
        }

        // Filter by gender
        if (!(dataCache.settings().showMaleEvents() && dataCache.settings().showFemaleEvents())) {
            String gender = null;
            if (dataCache.settings().showMaleEvents()) {
                gender = "m";
            } else if (dataCache.settings().showFemaleEvents()) {
                gender = "f";
            }

            ArrayList<Event> filteredByGender = new ArrayList<>();
            if (gender != null) {
                for (Event event : filteredEvents) {
                    Person person = getPerson(event.getPersonID(), dataCache.allPersons());
                    assert person != null;
                    if (person.getGender().toLowerCase().equals(gender)) {
                        filteredByGender.add(event);
                    }
                }
            }
            filteredEvents = filteredByGender;
        }
        // Else no need to filter by gender

        dataCache.setAllEventsFiltered(filteredEvents);
    }

    private ArrayList<Person> getPersonsRecursive(Person root) {
        ArrayList<Person> list = new ArrayList<>();
        if (root == null) {
            return list;
        }
        list.add(root);

        Person father = getPerson(root.getFatherID(), dataCache.allPersons());
        Person mother = getPerson(root.getMotherID(), dataCache.allPersons());

        list.addAll(getPersonsRecursive(father));
        list.addAll(getPersonsRecursive(mother));

        return list;
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
        Person spouse = getPerson(spouseID, dataCache.allPersons());
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
        ArrayList<Event> events = getAllEvents(selectedPerson.getPersonID(), dataCache.allEventsFiltered());
        for (Event event : events) {
            Marker eventMarker = getAssociatedMarker(event, mapMarkers);
            if (eventMarker != null) {
                Polyline line = map.addPolyline(new PolylineOptions()
                        .add(eventMarker.getPosition(), selectedMarker.getPosition())
                        .width(DEFAULT_LINE_WIDTH)
                        .color(Color.rgb(28, 168, 138))
                );
                lifeStoryLines.add(line);
            }
        }
    }

    private void drawFamilyTreeLines() {
        drawFamilyTreeLinesRecursive(selectedPerson, selectedMarker, DEFAULT_LINE_WIDTH);
    }

    private void drawFamilyTreeLinesRecursive(Person currentPerson, Marker currentMarker, float lineWidth) {
        if (currentPerson == null || currentMarker == null) {
            return;
        }

        Person mother = getPerson(currentPerson.getMotherID(), dataCache.allPersons());
        Person father = getPerson(currentPerson.getFatherID(), dataCache.allPersons());
        Marker motherEventMarker = getEarliestEventMarker(currentPerson.getMotherID(), mapMarkers);
        Marker fatherEventMarker = getEarliestEventMarker(currentPerson.getFatherID(), mapMarkers);

        drawFamilyTreeLinesRecursive(mother, motherEventMarker, lineWidth * (float) 0.6);
        drawFamilyTreeLinesRecursive(father, fatherEventMarker, lineWidth * (float) 0.6);

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

    private Person getPerson(String personID, List<Person> personList) {
        if (personID == null) {
            return null;
        }
        Person result = null;
        for (Person person : personList) {
            if (person.getPersonID().equals(personID)) {
                result = person;
            }
        }
        return result;
    }

    private ArrayList<Event> getAllEvents(String personID, List<Event> eventList) {
        ArrayList<Event> result = new ArrayList<>();
        if (personID == null || eventList == null) {
            return result;
        }
        for (Event event : eventList) {
            if (event.getPersonID().equals(personID)) {
                result.add(event);
            }
        }
        return result;
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
