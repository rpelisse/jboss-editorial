JBoss Weekly Editorial - Reminder and roster app
================================================

This project is both holding the roster of the JBoss Weekly Editorial - which states who is in charge of which editorial over the year, but also an handy script to generate a calendar (.ics) based on it, along with sending reminder (if run in a daily cronjob).

The roster itself is a plain text file at the root of the project, the "app" is a scala script along with some Java dependency.

How to use it ?
----

1) Run it (inside a daily cronjob) to send reminder

```
./src/main/bash/run-scala.sh src/main/scala/roster.scala -f roster.txt -h <smtp-server> -p <smtp-port>
```

Note that everytime this is a run a reminder will be sent (if it's run on either Monday or Thursday). So do set it up as a daily job in cron:

```
@daily /path/to/jboss-weekly-reminder-cronjob.sh
```

A template for the jboss-weekly-reminder-cronjob.sh is prodived in src/main/bash folder.

2) Generate the calendar

```
./src/main/bash/run-scala.sh ./src/main/scala/roster.scala -f ./roster.txt -c calendar.ics
```

This will *NOT* send any reminder, so you can run it as much as you want !

How does it ? How does it run ?
----

The roster app is a scala script using a couple of Open Source java lib (namely at this point, ical4j and jcommander), along with one java class provided in this project. For commidity purpose, the associated maven project build one jar containing all the dependency.


What is the format file for the roster.txt ?
----

It's plain text, but the roster.scala script have two expectation regarding it (ie it uses regex to fetch what it needs):

* each author is listed, one line, next to a *3 characters* acronyms;
* each entry starts, on one line, by a *2 digis* week number.

Bottom line, add whatever you want in the file, but prefixed by '#' to avoid messing with the script ! In doubt, just run it to generate the calendar, it will most likely complains or crashed if you screwed up something !


What else ?
----

* The calendar is generated for the current year.
* Each author name should follow the RFC regarding email, as it will be directly used as an email address.

