package org.jboss.editorial;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

public class CalendarUtils {

    private CalendarUtils() {
    }

    public static Calendar createCalendar(String name) {
    	Calendar calendar = new Calendar();
    	calendar.getProperties().add(new ProdId("-//" + name + "//iCal4j 1.0//EN"));
    	calendar.getProperties().add(Version.VERSION_2_0);
    	calendar.getProperties().add(CalScale.GREGORIAN);
    	return calendar;
    }

    public static VEvent createEvent(int weekNo, int dayOfTheWeekId, String eventDesc) {
    	java.util.Calendar entryWeek = java.util.Calendar.getInstance();
    	entryWeek.set(java.util.Calendar.WEEK_OF_YEAR, weekNo);
    	entryWeek.set(java.util.Calendar.DAY_OF_WEEK, dayOfTheWeekId);
    	VEvent event = new VEvent(new Date(entryWeek.getTime()), eventDesc);
    	event.getProperties().add(new Uid(weekNo + "." + dayOfTheWeekId));
    	event.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);
    	return event;
    }

    public static Calendar generateCalendar(String name, List<Editorial> editorials, List<Author> authors) {

    	Calendar calendar = createCalendar(name);
    	for (Editorial e : editorials) {
    		int weekNo = e.getWeekNo();
    		String author = authors.stream().filter(a -> (a.getTrigram().equals(e.getTrigram()))).findFirst().get()
    				.getName();
    		calendar.getComponents().add(createEvent(weekNo, java.util.Calendar.MONDAY,
    				"JBoss Weekly Editorial - Week " + weekNo + " (early week reminder) - " + author));
    		calendar.getComponents().add(createEvent(weekNo, java.util.Calendar.THURSDAY,
    				"JBoss Weekly Editorial - Week " + weekNo + " (release date)" + author));
    	}
    	return calendar;
    }

    public static void saveCalendarOnFile(Calendar calendar, String file) {
    	try {
    		new CalendarOutputter().output(calendar, new FileOutputStream(file));
    	} catch (FileNotFoundException e) {
    		throw new IllegalStateException(e);
    	} catch (IOException e) {
    		throw new IllegalStateException(e);
    	} catch (ValidationException e) {
    		throw new IllegalStateException(e);
    	}
    }

    public static int getCurrentWeekNo() {
    	return java.util.Calendar.getInstance().get(java.util.Calendar.WEEK_OF_YEAR);
    }
}
