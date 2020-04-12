package client;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import _model.Event;
import _model.Person;
import model.EventItem;
import model.PersonDetailItem;
import model.PersonItem;

public class PersonActivity extends AppCompatActivity {
    private DataCache dataCache = DataCache.getInstance();
    private ArrayList<Person> personFamily = new ArrayList<>();
    private ArrayList<Event> personEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Family Map: Person Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Person person = new Gson().fromJson(getIntent().getExtras().get("person").toString(), Person.class);

        List<PersonDetailItem> personDetailItems = new ArrayList<>();
        personDetailItems.add(new PersonDetailItem(person.getFirstName(), "First Name"));
        personDetailItems.add(new PersonDetailItem(person.getLastName(), "Last Name"));
        personDetailItems.add(new PersonDetailItem(person.getGender().toLowerCase().equals("f") ? "Female" : "Male", "Gender"));

        List<EventItem> eventItems = new ArrayList<>();
        List<Event> allEvents = dataCache.getAllEventsFiltered();
        for (Event event : allEvents) {
            if (event.getPersonID().equals(person.getPersonID())) {
                personEvents.add(event);
                eventItems.add(new EventItem(event.getEventID(), event.getEventType().toUpperCase(), event.getCity(), event.getCountry(),
                        event.getYear().toString(), person.getFirstName() + " " + person.getLastName()));
            }
        }

        Collections.sort(eventItems, new YearComparator());

        List<PersonItem> personItems = new ArrayList<>();
        List<Person> allPersons = dataCache.getAllPersonsResult().getData();
        for (Person item : allPersons) {
            if (item.getPersonID().equals(person.getFatherID())) {
                personFamily.add(item);
                personItems.add(new PersonItem(item.getPersonID(), item.getFirstName() + " " + item.getLastName(), "Father", item.getGender()));
            } else if (item.getPersonID().equals(person.getMotherID())) {
                personFamily.add(item);
                personItems.add(new PersonItem(item.getPersonID(), item.getFirstName() + " " + item.getLastName(), "Mother", item.getGender()));
            } else if (item.getPersonID().equals(person.getSpouseID())) {
                personFamily.add(item);
                personItems.add(new PersonItem(item.getPersonID(), item.getFirstName() + " " + item.getLastName(), "Spouse", item.getGender()));
            } else if (item.getFatherID() != null || item.getMotherID() != null) {
                if (item.getFatherID().equals(person.getPersonID()) || item.getMotherID().equals(person.getPersonID())) {
                    personFamily.add(item);
                    personItems.add(new PersonItem(item.getPersonID(), item.getFirstName() + " " + item.getLastName(), "Child", item.getGender()));
                }
            }
        }

        RecyclerView recyclerView = findViewById(R.id.person_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ExpandableListView expandableListView = findViewById(R.id.person_expandable_view);
        expandableListView.setAdapter(new ExpandableListAdapter(eventItems, personItems));

        PersonAdapter adapter = new PersonAdapter(personDetailItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return false;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;

        private final List<EventItem> eventItems;
        private final List<PersonItem> personItems;

        ExpandableListAdapter(List<EventItem> eventItems, List<PersonItem> personItems) {
            this.eventItems = eventItems;
            this.personItems = personItems;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return eventItems.size();
                case PERSON_GROUP_POSITION:
                    return personItems.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return getString(R.string.event_group_title);
                case PERSON_GROUP_POSITION:
                    return getString(R.string.person_group_title);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return eventItems.get(childPosition);
                case PERSON_GROUP_POSITION:
                    return personItems.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.event_group_title);
                    break;
                case PERSON_GROUP_POSITION:
                    titleView.setText(R.string.person_group_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.item_event, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.item_person, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventView, final int childPosition) {
            EventItem eventItem = eventItems.get(childPosition);

            ImageView iconView = eventView.findViewById(R.id.icon);
            iconView.setImageDrawable(new IconDrawable(PersonActivity.this,
                    FontAwesomeIcons.fa_map_marker).colorRes(R.color.primary).sizeDp(40));

            String text = eventItem.getEventType() + ": " + eventItem.getCity() + ", " +
                    eventItem.getCountry() + " (" + eventItem.getYear() + ")";

            TextView eventDetailsView = eventView.findViewById(R.id.event_details);
            eventDetailsView.setText(text);

            TextView personNameView = eventView.findViewById(R.id.person_name);
            personNameView.setText(eventItem.getPersonName());

            eventView.setOnClickListener(v -> {
                Event selectedEvent = null;
                for (Event event : personEvents) {
                    if (event.getEventID().equals(eventItem.getEventID())) {
                        selectedEvent = event;
                    }
                }
                Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                intent.putExtra("event", new Gson().toJson(selectedEvent));
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        private void initializePersonView(View personView, final int childPosition) {
            PersonItem personItem = personItems.get(childPosition);

            IconDrawable genderIcon;
            if (personItem.getGender().toLowerCase().equals("m")) {
                genderIcon = new IconDrawable(PersonActivity.this,
                        FontAwesomeIcons.fa_male).colorRes(R.color.male).sizeDp(40);
            } else {
                genderIcon = new IconDrawable(PersonActivity.this,
                        FontAwesomeIcons.fa_female).colorRes(R.color.female).sizeDp(40);
            }

            ImageView iconView = personView.findViewById(R.id.gender_icon);
            iconView.setImageDrawable(genderIcon);

            TextView personNameView = personView.findViewById(R.id.name);
            personNameView.setText(personItem.getName());

            TextView relationshipView = personView.findViewById(R.id.relationship);
            relationshipView.setText(personItem.getRelationship());

            personView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Person person : personFamily) {
                        if (person.getPersonID().equals(personItem.getPersonID())) {
                            Intent intent = getIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("person", new Gson().toJson(person));
                            startActivity(intent);
                            break;
                        }
                    }
                    Intent intent = PersonActivity.this.getIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class PersonAdapter extends RecyclerView.Adapter<PersonViewHolder> {
        private final List<PersonDetailItem> personDetailItems;

        PersonAdapter(List<PersonDetailItem> personDetailItems) {
            this.personDetailItems = personDetailItems;
        }

        @NonNull
        @Override
        public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = getLayoutInflater().inflate(R.layout.item_person_detail, parent, false);
            return new PersonViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
            holder.bind(personDetailItems.get(position));
        }

        @Override
        public int getItemCount() {
            return personDetailItems.size();
        }
    }

    private class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title;
        private final TextView subtitle;

        private PersonDetailItem personDetailItem;
        private final int viewType;

        PersonViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.person_detail_title);
            subtitle = itemView.findViewById(R.id.person_detail_subtitle);
        }

        private void bind(PersonDetailItem personDetailItem) {
            this.personDetailItem = personDetailItem;
            title.setText(personDetailItem.getDetail());
            subtitle.setText(personDetailItem.getDescription());
        }

        @Override
        public void onClick(View view) {

        }
    }

    public class YearComparator implements Comparator<EventItem> {
        @Override
        public int compare(EventItem o1, EventItem o2) {
            return o1.getYear().compareTo(o2.getYear());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}