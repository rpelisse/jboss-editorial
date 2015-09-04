/*

    1) retrieve current and sent email if Monday or Friday (reminder)

    2) Built the ICS file base on roster

*/
import java.text.SimpleDateFormat
import java.io.FileOutputStream

import org.jboss.weekly.SMTPClient;

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException

import net.fortuna.ical4j.data._
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.model.property.Uid
import net.fortuna.ical4j.model.parameter.Value
import net.fortuna.ical4j.model.Property

object Args {
  @Parameter(names = Array("-f", "--roster-file"), description = "Path to the roster file", required = true)
  var rosterFile: String = ""

  @Parameter(names = Array("-c", "--generate-ical-file"), description = "Generate an ical file based on roster file", required = false)
  var iCalFile: String = null

  @Parameter(names = Array("-h", "--smtp-hostname"), description = "Hostname of SMTP server used to send reminder", required = false)
  var smtpHostname = ""

  @Parameter(names = Array("-p", "--smtp-port"), description = "Port to use of SMTP server used to send reminder", required = false)
  var smtpPort = ""
}

new JCommander(Args, args.toArray: _*)

// Main starts here
if ( Args.iCalFile != null )
  generateICalFile(Args.iCalFile, Args.rosterFile)
else
  sendReminderIfNeeded()
// Main ends here

def createEvent(weekNo:Int, dayOfTheWeekId: Int, eventDesc: String) = {
    val entryWeek = java.util.Calendar.getInstance()
    entryWeek.set(java.util.Calendar.WEEK_OF_YEAR, weekNo)
    entryWeek.set(java.util.Calendar.DAY_OF_WEEK, dayOfTheWeekId);
    val event = new VEvent(new Date(entryWeek.getTime()), eventDesc)
    event.getProperties().add(new Uid(weekNo + "." + dayOfTheWeekId))
    event.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE)
    event
}

def createCalendar(name:String) = {
  val calendar = new Calendar()
  calendar.getProperties().add(new ProdId("-//" + name + "//iCal4j 1.0//EN"))
  calendar.getProperties().add(Version.VERSION_2_0)
  calendar.getProperties().add(CalScale.GREGORIAN)
  calendar
}

def generateICalFile(iCalFile: String, rosterFile: String) = {
  println("Generating iCal File:" + iCalFile + ", from roster file:" + rosterFile)
  val authors = loadAuthors(rosterFile)
  val calendar = createCalendar("JBoss Weekly Editorial Calendar")
  for ( e <- loadEntries(rosterFile) ) {
    val weekNo = e._1.toInt
    val author = authors.filter ( author => author._1.equals(e._2) ).head._2

    calendar.getComponents().add(createEvent(weekNo, java.util.Calendar.MONDAY, "JBoss Weekly Editorial - Week " + weekNo + " (early week reminder) - " + author))
    calendar.getComponents().add(createEvent(weekNo, java.util.Calendar.THURSDAY, "JBoss Weekly Editorial - Week " + weekNo + " (release date)" + author))
  }
  //println(calendar)
  new CalendarOutputter().output(calendar, new FileOutputStream(iCalFile))
  calendar
}

def loadEntries(rosterFile: String) = {
  val fileLines = parseRosterFile(rosterFile)
  val entryRegex = "^([0-9][0-9]) ([a-z][a-z][a-z])".r
  fileLines.collect { case entryRegex(weekNo, authorId) => (weekNo , authorId) }
}

def loadAuthors(rosterFile: String) = {
  val fileLines = parseRosterFile(rosterFile)
  val authorRegex = "^([a-z][a-z][a-z]) = (.*)$".r
  fileLines.collect{ case authorRegex(id, author) => (id , author) }
}

def sendReminderIfNeeded() = { //roster:String, smtpHostname:String, smtpPort:String) = {
  java.util.Calendar.getInstance().getTime().getDay match {
    case 4 => sendReminder("Hi,\n\nFriendly reminder, it's Thursday, you should publish the weekly by the end of the day.")
    case 1 => sendReminder("Hi,\n\nIt's Monday and this week you are in charge of the JBoss Weekly Editorial. Don't forget about it !")
    case _ => sendReminder("Hi,\n\nFriendly reminder, it's Thursday, you should publish the weekly by the end of the day.")
  }
}

def parseRosterFile(rosterFile:String) = {
  io.Source.fromFile(rosterFile).getLines.toList
}

def sendReminder(message:String) = {

  val EDITORIAL_TEAM_MAIL = "JBoss Editorial Team <jboss-editorial-team@redhat.com>"

  val fileLines = parseRosterFile(Args.rosterFile)
  val authorRegex = "^([a-z][a-z][a-z]) = (.*)$".r
  val entryRegex = "^([0-9][0-9]) ([a-z][a-z][a-z])".r

  val authors = fileLines.collect{ case authorRegex(id, author) => (id , author) }
  val entries = fileLines.collect { case entryRegex(weekNo, authorId) => (weekNo , authorId) }

  val weekNo = java.util.Calendar.getInstance().get(java.util.Calendar.WEEK_OF_YEAR)
  val authorId = entries.filter( entry => entry._1.equals(weekNo.toString) ).head._2
  val author = authors.filter ( author => author._1.equals(authorId) ).head._2
  println("Sending a reminder to:" + author + ", from "  + EDITORIAL_TEAM_MAIL + ", with message:\n" + message)
  new SMTPClient(Args.smtpHostname, Args.smtpPort).sendEmail(author, EDITORIAL_TEAM_MAIL , "JBoss Weekly Editorial Reminder", message)
}
