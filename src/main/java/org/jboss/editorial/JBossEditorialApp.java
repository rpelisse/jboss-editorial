package org.jboss.editorial;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "jboss-editorial-app", mixinStandardHelpOptions = true)
public class JBossEditorialApp implements Runnable {

    @Option(names = {"-f", "--roster-file"}, description = "Path to the roster file", required = true) String rosterFile;

    @Option(names = {"-c", "--generate-ical-file"}, description = "Generate an ical file based on roster file") String iCalFile = null;

    private List<Author> authors;
    private List<Editorial> editorials;

    @Override
    public void run() {
        try {
            executeTask();
        } catch ( IOException e ) {
          throw new IllegalArgumentException(e);
        }
    }

    private void executeTask() throws IOException {
        RosterLoader loader = new RosterLoader(Path.of(rosterFile));
        authors = loader.loadAuthorsFromRoster();
        editorials = loader.loadEditorialsFromRoster();
        if ( iCalFile != null ) {
            CalendarUtils.saveCalendarOnFile(CalendarUtils.generateCalendar("JBoss Editorial Calendar", editorials, authors), iCalFile);
        } else
            sendReminderIfNeeded();
    }

    @SuppressWarnings("deprecation")
    private void sendReminderIfNeeded() {
         switch (java.util.Calendar.getInstance().getTime().getDay()) {
            case 1:
                System.out.println("It's Monday, let's send the first reminder.");
                sendReminder("Hi,\n\nIt's Monday and, this week, you are in charge of the JBoss Weekly Editorial. Don't forget about it !");
                break;
              case 4:
                System.out.println("It's Tuesday, let's send the second reminder.");
                sendReminder("Hi,\n\nFriendly reminder, it's Thursday, you should publish the JBoss Weekly Editorial by the end of the day - otherwise notify the list.");
                break;
            default:
                System.out.println("No reminders to be sent today.");
          }

    }

    @Inject Mailer mailer;
    private void sendReminder(String message) {

        int weekNo = CalendarUtils.getCurrentWeekNo();
        Optional<Editorial> editorial = editorials.stream().filter(e -> e.getWeekNo() == weekNo ).findFirst();
        if ( ! editorial.isEmpty() ) {
            String authorsOfTheWeek =  editorial.get().getTrigram();
            if ( authorsOfTheWeek.isEmpty() )
                System.out.println("No editorial this week.");
            else {
                mailer.send(Mail.withText(authors.stream().filter ( a -> a.equals(authorsOfTheWeek) ).findFirst().get().getName(), "JBoss Weekly Editorial Reminder", message));
            }
        } else
            System.out.println("No JBoss Editorial this week (" + weekNo + ")");
    }
}
