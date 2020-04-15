package process;

import java.util.ArrayList;

import _model.Event;
import _model.Person;
import client.DataCache;

public class Filter {
    public static void filterEventsBasedOnSettings() {
        DataCache dataCache = DataCache.getInstance();
        ArrayList<Event> filteredEvents = new ArrayList<>();
        // Filter by side of family
        Person user = Data.getPerson(dataCache.userPersonId(), dataCache.allPersons());
        if (dataCache.settings().showFathersSide() && dataCache.settings().showMothersSide()) {
            filteredEvents = dataCache.allEvents();
        } else {
            if (dataCache.settings().showFathersSide()) {
                // Add father's side only
                Person father = Data.getPerson(user.getFatherID(), dataCache.allPersons());
                ArrayList<Person> fathersSide = Data.getPersonTree(father, dataCache.allPersons());

                for (Person person : fathersSide) {
                    ArrayList<Event> events = Data.getAllEvents(person.getPersonID(), dataCache.allEvents());
                    filteredEvents.addAll(events);
                }
            } else if (dataCache.settings().showMothersSide()) {
                // Add mother's side only
                Person mother = Data.getPerson(user.getMotherID(), dataCache.allPersons());
                ArrayList<Person> mothersSide = Data.getPersonTree(mother, dataCache.allPersons());

                for (Person person : mothersSide) {
                    ArrayList<Event> events = Data.getAllEvents(person.getPersonID(), dataCache.allEvents());
                    filteredEvents.addAll(events);
                }
            }
            // Add the root user and spouse events
            filteredEvents.addAll(Data.getAllEvents(user.getPersonID(), dataCache.allEvents()));
            filteredEvents.addAll(Data.getAllEvents(user.getSpouseID(), dataCache.allEvents()));
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
                    Person person = Data.getPerson(event.getPersonID(), dataCache.allPersons());
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
}
