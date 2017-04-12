/*

    1) retrieve current and sent email if Monday or Friday (reminder)

    2) Built the ICS file base on roster

*/
import java.text.SimpleDateFormat
import java.io.FileOutputStream
import java.io.File

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException

import javax.mail.{ Session, Transport }
import java.util.Properties
import javax.mail.Message.RecipientType
import javax.mail.Message
import javax.mail.Session
import javax.mail.PasswordAuthentication
import javax.mail.internet.{ InternetAddress, MimeMessage }
import java.util.Arrays
import java.io.FileInputStream

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

import akka.actor.{ActorSystem, ActorLogging, Actor, Props}
import scala.concurrent.duration.Duration
import scala.io.Source
import java.util.Properties
import collection.JavaConverters._

object Args {
  // Feature command
  @Parameter(names = Array("-f", "--roster-file"), description = "Path to the roster file", required = true)
  var rosterFile: String = ""

  @Parameter(names = Array("-c", "--generate-ical-file"), description = "Generate an ical file based on roster file", required = false)
  var iCalFile: String = null

  @Parameter(names = Array("-T", "--send-test-email"), description = "Generate an ical file based on roster file", required = false)
  var testEmail: String = null

  // Configuration paramaters
  @Parameter(names = Array("-h", "--smtp-hostname"), description = "Hostname of SMTP server used to send reminder", required = false)
  var smtpHostname = ""

  @Parameter(names = Array("-p", "--smtp-port"), description = "Port to use of SMTP server used to send reminder", required = false)
  var smtpPort = "465"

  @Parameter(names = Array("-U", "--stmp-username"), description = "Username for SMTP authentification", required = false)
  val smtpUsername = ""

  @Parameter(names = Array("-P", "--stmp-password"), description = "Password for SMTP authentification", required = false)
  val smtpPassword = ""
}

// Main program

// Extracting Parameter from Propery - as the mvn scala plugin does not support passing args to
// script apparently
var args = Seq[String]()
val properties = System.getProperties().asScala
for ( (k,v) <- properties )
  if ( k.contains("roster.") )  {
    args = args:+("--" + k.substring(k.indexOf('.') + 1 ,k.length))
    args = args:+(v)
  }
new JCommander(Args, args.toArray: _*)

if ( Args.testEmail != null) sendTestEmail()

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
    case 4 => sendReminder("Hi,\n\nFriendly reminder, it's Thursday, you should publish the JBoss Weekly Editorial by the end of the day - otherwise notify the list.")
    case 1 => sendReminder("Hi,\n\nIt's Monday and, this week, you are in charge of the JBoss Weekly Editorial. Don't forget about it !")
    case _ => println("No reminders to be sent today.")
  }
}

def parseRosterFile(rosterFile:String) = {
  Source.fromFile(rosterFile).getLines.toList
}

def sendTestEmail(user: String = "belaran@redhat.com") = {
  sendEMail(user, user, "Test Email from Roster App", "Test successful - if you are reading this.")
  System.exit(0)
}

def sendMimeMessage(message: MimeMessage) {
  try {
    Transport.send(message)
  } catch { case t: Throwable => println("Failed to send email:" + t.getMessage())
  }
}

def smtpProps() = {
  val smtpProperties = new Properties()
  smtpProperties.put("mail.smtp.port", Args.smtpPort)
  smtpProperties.put("mail.smtp.host", Args.smtpHostname)
  smtpProperties.put("mail.transport.protocol", "smtp")
  smtpProperties.put("mail.smtp.starttls.enable", "true")
  smtpProperties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory")
  smtpProperties.put("mail.smtp.auth", "true")
  smtpProperties.put("mail.smtp.timeout", "10000")
  smtpProperties.put("mail.smtp.ssl.checkserveridentity", "false")
  smtpProperties.put("mail.smtp.ssl.trust", "*")
  smtpProperties.put("mail.smtp.connectiontimeout", "10000")
  smtpProperties.put("mail.smtp.debug", "false")
  smtpProperties.put("mail.smtp.socketFactory.fallback", "false")
  smtpProperties
}

def getSmtpSession(smtpProperties: Properties) = {
  Session.getInstance(smtpProps, new javax.mail.Authenticator() {  override def getPasswordAuthentication() = { new PasswordAuthentication(Args.smtpUsername, Args.smtpPassword) } } )
}

def sendEMail(from:String, to:String, subject:String, text:String) {

  val session = getSmtpSession(smtpProps())
  val message = new MimeMessage(session)
  val me = "Romain Pelisse <romain@redhat.com>"

  message.setFrom(new InternetAddress(from))
  message.addRecipient(RecipientType.TO, new InternetAddress(to))
  message.addRecipient(RecipientType.TO, new InternetAddress(me))
  message.setSubject(subject)
  message.setText(text)

  sendMimeMessage(message)
}

def sendReminder(message:String) = {

  val EDITORIAL_TEAM_MAIL = "JBoss Editorial Team <jboss-editorial-team@redhat.com>"

  val fileLines = parseRosterFile(Args.rosterFile)
  val authorRegex = "^([a-z][a-z][a-z]) = (.*)$".r
  val entryRegex = "^([0-9][0-9]) ([a-z][a-z][a-z])".r

  val authors = fileLines.collect{ case authorRegex(id, author) => (id , author) }
  val entries = fileLines.collect { case entryRegex(weekNo, authorId) => (weekNo , authorId) }

  val weekNo = java.util.Calendar.getInstance().get(java.util.Calendar.WEEK_OF_YEAR)
  val authorId = entries.filter( entry => entry._1.equals(String.format("%02d", Integer.valueOf(weekNo.toString)))).head._2
  val author = authors.filter ( author => author._1.equals(authorId) ).head._2
  println("Sending a reminder to:" + author + ", from "  + EDITORIAL_TEAM_MAIL + ", with message:\n" + message)
  sendEMail(EDITORIAL_TEAM_MAIL, author,  "JBoss Weekly Editorial Reminder", message)
}
