package com;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import _model.Event;
import _model.Person;
import client.DataCache;
import model.EventItem;
import model.PersonItem;
import process.Filter;
import process.Search;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    private DataCache dataCache = DataCache.getInstance();

    @AfterEach
    public void tearDown() {
        dataCache.invalidateData();
    }

    @Test
    public void filterPass() {
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("child", "jon", "Jonny",
                "Tullis", "m", "dad", "mom", null));
        people.add(new Person("dad", "jon", "Mike",
                "Tullis", "m", null, null, "mom"));
        people.add(new Person("mom", "jon", "Susan",
                "Stevenson", "f", null, null, "dad"));

        dataCache.setAllPersons(people);
        dataCache.setUserPersonId("child");
        dataCache.setUserName("jon");

        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("1", "jon", "child", 1.4, 1.2, "USA", "Provo", "Birth", 2012));
        events.add(new Event("2", "jon", "dad", 1.4, 1.2, "USA", "Provo", "Marriage", 2000));
        events.add(new Event("3", "jon", "mom", 1.4, 1.2, "USA", "Provo", "Marriage", 2000));
        dataCache.setAllEvents(events);

        dataCache.settings().setFathersSide(true);
        dataCache.settings().setMothersSide(true);
        dataCache.settings().setMaleEvents(true);
        dataCache.settings().setFemaleEvents(true);

        Filter.filterEventsBasedOnSettings();
        assertEquals(3, dataCache.allEventsFiltered().size(), "All events should still be there");

        dataCache.settings().setFathersSide(false);
        Filter.filterEventsBasedOnSettings();
        assertEquals(2, dataCache.allEventsFiltered().size(), "Should only find 'child' and 'father' events");

        dataCache.settings().setFathersSide(true);
        dataCache.settings().setMaleEvents(false);
        Filter.filterEventsBasedOnSettings();
        assertEquals(1, dataCache.allEventsFiltered().size(), "Should only find 'mother' event");
    }

    @Test
    public void filterEdgePass() {
        // Try no mother
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("child", "jon", "Jonny",
                "Tullis", "m", "dad", null, null));
        people.add(new Person("dad", "jon", "Mike",
                "Tullis", "m", null, null, null));

        dataCache.setAllPersons(people);
        dataCache.setUserPersonId("child");
        dataCache.setUserName("jon");

        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("1", "jon", "child", 1.4, 1.2, "USA", "Provo", "Birth", 2012));
        events.add(new Event("2", "jon", "dad", 1.4, 1.2, "USA", "Provo", "Marriage", 2000));
        dataCache.setAllEvents(events);

        dataCache.settings().setFathersSide(true);
        dataCache.settings().setMothersSide(true);
        dataCache.settings().setMaleEvents(true);
        dataCache.settings().setFemaleEvents(true);

        Filter.filterEventsBasedOnSettings();
        assertEquals(2, dataCache.allEventsFiltered().size(), "All events should still be there");

        dataCache.settings().setFathersSide(false);
        Filter.filterEventsBasedOnSettings();
        assertEquals(1, dataCache.allEventsFiltered().size(), "Should only find 'child' event");

        dataCache.settings().setFathersSide(true);
        dataCache.settings().setMothersSide(false);
        Filter.filterEventsBasedOnSettings();
        assertEquals(2, dataCache.allEventsFiltered().size(), "Should still find 'child' and 'father' events");
    }

    @Test
    public void sortIndividualEventsPass() {
        // Events should be sorted automatically when added to the DataCache. Check that.
        ArrayList<Event> testEvents = new ArrayList<>();
        testEvents.add(new Event("123", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 2012));
        testEvents.add(new Event("443", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 1312));
        testEvents.add(new Event("235", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 2010));
        testEvents.add(new Event("908", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 1996));
        dataCache.setAllEventsFiltered(testEvents);

        assertEquals(1312, (int) dataCache.allEventsFiltered().get(0).getYear(), "Event not in correct index");
        assertEquals(1996, (int) dataCache.allEventsFiltered().get(1).getYear(), "Event not in correct index");
        assertEquals(2010, (int) dataCache.allEventsFiltered().get(2).getYear(), "Event not in correct index");
        assertEquals(2012, (int) dataCache.allEventsFiltered().get(3).getYear(), "Event not in correct index");
    }

    @Test
    public void sortIndividualEventsEdgePass() {
        // Edge case
        // Try multiple events with the same year and one that's different
        ArrayList<Event> testEvents = new ArrayList<>();
        testEvents.add(new Event("123", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 2010));
        testEvents.add(new Event("443", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 2010));
        testEvents.add(new Event("235", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 2010));
        testEvents.add(new Event("908", "jon", "123", 1.4, 1.2, "USA", "Provo", "type", 1996));
        dataCache.setAllEventsFiltered(testEvents);

        assertEquals(1996, (int) dataCache.allEventsFiltered().get(0).getYear(), "Earliest event was not first in array");
    }

    @Test
    public void searchPass() {
        // Search finds items from the datacache. First load options there.
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("123", "jon", "Jonny",
                "Tullis", "m", "123", "123", "123"));
        people.add(new Person("123", "jon", "Frog",
                "Tullis", "m", "123", "123", "123"));
        people.add(new Person("123", "jon", "Jonny",
                "Cash", "m", "123", "123", "123"));
        people.add(new Person("123", "jon", "Jennifer",
                "Hall", "m", "123", "123", "123"));
        people.add(new Person("123", "jon", "Jennifer",
                "Michaelson", "m", "123", "123", "123"));

        dataCache.setAllPersons(people);
        ArrayList<PersonItem> personResult = new ArrayList<>();

        Search.findPersonItems("J", personResult);
        assertEquals(4, personResult.size());
        personResult.clear();

        Search.findPersonItems("Jo", personResult);
        assertEquals(2, personResult.size());
        personResult.clear();

        Search.findPersonItems("T", personResult);
        assertEquals(2, personResult.size());
        personResult.clear();

        // EVENTS
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "USA", "Provo", "Birth", 2011));
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "USA", "Salt Lake City", "Baptism", 2012));
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "France", "Paris", "Death", 1989));
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "Germany", "Belgium", "Marriage", 2003));

        dataCache.setAllEventsFiltered(events);
        ArrayList<EventItem> eventResult = new ArrayList<>();

        Search.findEventItems("a", eventResult);
        assertEquals(4, eventResult.size());
        eventResult.clear();

        Search.findEventItems("p", eventResult);
        assertEquals(3, eventResult.size());
        eventResult.clear();

        Search.findEventItems("201", eventResult);
        assertEquals(2, eventResult.size());
        eventResult.clear();

        Search.findEventItems("death", eventResult);
        assertEquals(1, eventResult.size());
        eventResult.clear();
    }

    @Test
    public void searchEdgePass() {
        // Search finds items from the datacache. First load options there.
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("123", "jon", "Jonny",
                "Tullis", "m", "123", "123", "123"));
        people.add(new Person("123", "jon", "Frog",
                "Tullis", "m", "123", "123", "123"));
        people.add(new Person("123", "jon", "Jonny",
                "Cash", "m", "123", "123", "123"));
        people.add(new Person("123", "jon", "Jennifer",
                "Hall", "m", "123", "123", "123"));
        people.add(new Person("123", "no", "Jennifer",
                "Michaelson", "m", "123", "123", "123"));

        dataCache.setAllPersons(people);
        ArrayList<PersonItem> personResult = new ArrayList<>();

        Search.findPersonItems("", personResult);
        assertEquals(0, personResult.size());
        personResult.clear();

        Search.findPersonItems("z", personResult);
        assertEquals(0, personResult.size());
        personResult.clear();

        Search.findPersonItems("nothing here", personResult);
        assertEquals(0, personResult.size());
        personResult.clear();

        // EVENTS
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "USA", "Provo", "Birth", 2011));
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "USA", "Salt Lake City", "Baptism", 2012));
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "France", "Paris", "Death", 1989));
        events.add(new Event("123", "jon", "123", 1.2, 1.2,
                "Germany", "Belgium", "Marriage", 2003));

        dataCache.setAllEventsFiltered(events);
        ArrayList<EventItem> eventResult = new ArrayList<>();

        Search.findEventItems("", eventResult);
        assertEquals(0, eventResult.size());
        eventResult.clear();

        Search.findEventItems("pk", eventResult);
        assertEquals(0, eventResult.size());
        eventResult.clear();

        Search.findEventItems("Nothing here", eventResult);
        assertEquals(0, eventResult.size());
        eventResult.clear();
    }
}
