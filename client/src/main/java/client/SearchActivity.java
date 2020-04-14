package client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import _model.Event;
import _model.Person;
import model.EventItem;
import model.PersonItem;
import model.SettingsItem;


public class SearchActivity extends AppCompatActivity {
    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;
    private DataCache dataCache = DataCache.getInstance();
    private ArrayList<PersonItem> personItems = new ArrayList<>();
    private ArrayList<EventItem> eventItems = new ArrayList<>();
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView prependIconView = findViewById(R.id.icon_image_view);
        EditText textField = findViewById(R.id.edit_text_view);
        ImageView clearTextIcon = findViewById(R.id.icon_clear);

        prependIconView.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_search).
                colorRes(R.color.greyDark).sizeDp(5));

        clearTextIcon.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_times).
                colorRes(R.color.greyDark).sizeDp(5));

        clearTextIcon.setOnClickListener(v -> {
            textField.setText("");
        });

        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                filterBySearch();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchAdapter adapter = new SearchAdapter(personItems, eventItems);
        recyclerView.setAdapter(adapter);
    }

    private void filterBySearch() {
        System.out.println("Reached");
        System.out.println("Text Value: " + searchText);

        for (Person person : dataCache.allPersons()) {
            personItems.add(new PersonItem(person.getPersonID(), person.getFirstName() +
                    " " + person.getLastName(), null, person.getGender()));
        }

        for (Event event : dataCache.allEventsFiltered()) {
            String personName = null;
            for (Person person : dataCache.allPersons()) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    personName = person.getFirstName() + " " + person.getLastName();
                }
            }
            eventItems.add(new EventItem(event.getEventID(), event.getEventType(), event.getCity(),
                    event.getCountry(), event.getYear().toString(), personName));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return false;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<PersonItem> personItems;
        private final List<EventItem> eventItems;

        SearchAdapter(List<PersonItem> personItems, List<EventItem> eventItems) {
            this.personItems = personItems;
            this.eventItems = eventItems;
        }

        @Override
        public int getItemViewType(int position) {
            return position < personItems.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.item_person, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.item_event, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < personItems.size()) {
                holder.bind(personItems.get(position));
            } else {
                holder.bind(eventItems.get(position - personItems.size()));
            }
        }

        @Override
        public int getItemCount() {
            return personItems.size() + eventItems.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleView;
        private final TextView descriptionView;
        private final ImageView iconView;

        private final int viewType;
        private PersonItem personItem;
        private EventItem eventItem;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                titleView = itemView.findViewById(R.id.name);
                descriptionView = itemView.findViewById(R.id.relationship);
                iconView = itemView.findViewById(R.id.gender_icon);
            } else {
                titleView = itemView.findViewById(R.id.event_details);
                descriptionView = itemView.findViewById(R.id.person_name);
                iconView = itemView.findViewById(R.id.icon);
            }
        }

        private void bind(PersonItem personItem) {
            this.personItem = personItem;
            this.titleView.setText(personItem.getName());
            this.descriptionView.setText("Person");
            if (personItem.getGender().toLowerCase().equals("m")) {
                this.iconView.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(R.color.male).sizeDp(40));
            } else {
                this.iconView.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(R.color.female).sizeDp(40));
            }
        }

        private void bind(EventItem eventItem) {
            this.eventItem = eventItem;
            this.iconView.setImageDrawable(new IconDrawable(SearchActivity.this,
                    FontAwesomeIcons.fa_map_marker).colorRes(R.color.primary).sizeDp(40));

            String text = eventItem.getEventType() + ": " + eventItem.getCity() + ", " +
                    eventItem.getCountry() + " (" + eventItem.getYear() + ")";

            this.titleView.setText(text);
            this.descriptionView.setText(eventItem.getPersonName());
        }

        @Override
        public void onClick(View view) {
            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                Person selectedPerson = null;
                for (Person person : dataCache.allPersons()) {
                    if (personItem.getPersonID().equals(person.getPersonID())) {
                        selectedPerson = person;
                        break;
                    }
                }
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("person", new Gson().toJson(selectedPerson));
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else {
                Event selectedEvent = null;
                for (Event event : dataCache.allEventsFiltered()) {
                    if (event.getEventID().equals(eventItem.getEventID())) {
                        selectedEvent = event;
                        break;
                    }
                }
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("event", new Gson().toJson(selectedEvent));
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}