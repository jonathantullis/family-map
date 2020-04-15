package process;

import java.util.ArrayList;

import _model.Event;
import _model.Person;
import client.DataCache;
import model.EventItem;
import model.PersonItem;

public class Search {
    public static void findPersonItems(String searchText, ArrayList<PersonItem> listToFill) {
        // Results come from the dataCache
        DataCache dataCache = DataCache.getInstance();
        if (searchText.length() > 0) {
            for (Person person : dataCache.allPersons()) {
                boolean containsSearchText = false;
                if (person.getFirstName().toLowerCase().contains(searchText.toLowerCase())) {
                    containsSearchText = true;
                } else if (person.getLastName().toLowerCase().contains(searchText.toLowerCase())) {
                    containsSearchText = true;
                }

                if (containsSearchText) {
                    listToFill.add(new PersonItem(person.getPersonID(), person.getFirstName() +
                            " " + person.getLastName(), null, person.getGender()));
                }
            }
        }
    }

    public static void findEventItems(String searchText, ArrayList<EventItem> listToFill) {
        // Results come from the dataCache
        DataCache dataCache = DataCache.getInstance();
        if (searchText.length() > 0) {
            for (Event event : dataCache.allEventsFiltered()) {
                boolean containsSearchText = false;
                if (event.getCountry().toLowerCase().contains(searchText.toLowerCase())) {
                    containsSearchText = true;
                } else if (event.getCity().toLowerCase().contains(searchText.toLowerCase())) {
                    containsSearchText = true;
                } else if (event.getEventType().toLowerCase().contains(searchText.toLowerCase())) {
                    containsSearchText = true;
                } else if (event.getYear().toString().toLowerCase().contains(searchText.toLowerCase())) {
                    containsSearchText = true;
                }

                if (containsSearchText) {
                    String personName = null;
                    for (Person person : dataCache.allPersons()) {
                        if (event.getPersonID().equals(person.getPersonID())) {
                            personName = person.getFirstName() + " " + person.getLastName();
                        }
                    }
                    listToFill.add(new EventItem(event.getEventID(), event.getEventType(), event.getCity(),
                            event.getCountry(), event.getYear().toString(), personName));
                }
            }
        }
    }
}
