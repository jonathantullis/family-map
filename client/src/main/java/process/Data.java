package process;

import java.util.ArrayList;
import java.util.List;

import _model.Event;
import _model.Person;

public class Data {
    public static Person getPerson(String personID, List<Person> personList) {
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

    public static ArrayList<Event> getAllEvents(String personID, List<Event> eventList) {
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

    // Returns a Person array with all members of family starting at given root person
    public static ArrayList<Person> getPersonTree(Person root, ArrayList<Person> personList) {
        ArrayList<Person> list = new ArrayList<>();
        if (root == null) {
            return list;
        }
        list.add(root);

        Person father = Data.getPerson(root.getFatherID(), personList);
        Person mother = Data.getPerson(root.getMotherID(), personList);

        list.addAll(getPersonTree(father, personList));
        list.addAll(getPersonTree(mother, personList));

        return list;
    }
}
