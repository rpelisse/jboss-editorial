JBoss Editorial
====

The following schedule is for committing yourself to the weekly editorial to be published online [JBoss.org](https://www.jboss.org/) by Thursday, noon CET .


Who is doing which editorial when?
----

The roster to answer that is living in this Github repository, as a simple text file: roster.txt. Also on the Github project, there is a scala script used to generate the calendar file and send reminders to the appropriate person each week. If one needs to change the roster, just follow the usual git process (fork, change, push and make a PR).

What needs to be done and how?
----

1. We publish Thursday every two weeks around 7PM (GMT+1). Early on Thursday is fine and Friday is also fine.
* If something arises and you can't deliver your editorial, please try to trade with someone else on the team and update the roster to reflect this change.
* Put a reminder in your calendar so you don’t forget and double check at least check that the reminder automatically sent are indeed arriving in your inbox.
2. How do I find the material for the content of the editorial?
* Any news relevant to the JBoss community can be added into the editorial whatever the source (well, assuming the source is a valid source). However, most of the time, we use the following sources for the content of the editorial:
    * [Planet J‌Boss](https://planet.jboss.org/)
    * [Red Hat Developer Blog](https://developers.redhat.com/)
    * [Red Hat Middleware Blog](https://middlewareblog.redhat.com/) *(note that last entry as of 2020-06-30 was in January, so the source appears to have dryed out)*
* Warning Note that some item of this feed are non-jboss content, so don't cut'n'paste everything without thinking !
3. Communication with the editorial team : Subscribe to the jboss-editorial-team@redhat.com, this is the team list.

How to publish your editorial
----

Here is the workflow for posting editorials to JBoss.org using Github:

* Create a new asciidoc file with the editorial content at [jbossorg/jbossorg.github.io](https://github.com/jbossorg/jbossorg.github.io/tree/src/src/content/posts)
* Commit directly to the **src** branch.
* From there the editorial automatically gets picked up and posted to the site.

Note that you can also update [your profile photo](https://github.com/jbossorg/jbossorg.github.io/tree/src/src/img/people).

Structure of the Editorial
====

As a prime directive: this is your editorial, feel free to write any way you want. Just follow the few guidelines here:

* Title should always be **This week in JBoss ([date-for-thursday-that-week]) - [some-title-you-come-up-with]**.
    * Just copy someones existing title and adjust for your article that week ;)
* Add **tags** based on topics covered before publishing the blog article

If you need help structuring the editorial you can organise the content as suggested below:

* Have a small introduction, written in italic, that summerize the "big news" of the last two weeks (tip: write it last )
* Finish your editorial by adding the following lines (also in italic):
    * *That's all for another edition of the JBoss Editorial, please join us again for more exciting development from the JBoss Communities.*
* Try to regroup all articles about one topic or one product into one section and name it accordingly. Here some section you can use to organise the content:
    * **Techbytes** - you can regroup all "in-depth" technical articles not fitting other section in this one
    * **Releases, releases, releases** - list all the releases of the past two weeks under this section
    * **Decaf'** - any non-Java news can be aggregated into this section (but for the news to make sense, it needs to be somewhat relevant to the JBoss community! Docker or OpenShift news for instance, often falls into this category)
    * **Evangelist's Corner** - Red Hat evangelist, especially Eric D. Schabell, produces a lot of contents between two editorial, I generally regroup them into this section.

If you want to join the team, please contact [me (Romain Pelisse)](belaran@redhat.com)!

Roster and reminder app
====

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

