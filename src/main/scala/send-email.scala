import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException

import javax.mail.{ Session, Transport }
import java.util.Properties
import javax.mail.Message.RecipientType
import javax.mail.internet.{ InternetAddress, MimeMessage }

object Args {
  @Parameter(names = Array("-h", "--smtp-hostname"), description = "SMTP hostname", required = true)
  var host: String = ""

  @Parameter(names = Array("-p", "--smpt-port"), description = "SMTP port", required = true)
  var port: String = null

  @Parameter(names = Array("-f", "--from"), description = "sender email", required = true)
  var from: String = ""

  @Parameter(names = Array("-t", "--recipeient-mail"), description = "Email's recipient", required = true)
  var to: String = ""

  @Parameter(names = Array("-s", "--subject"), description = "Email's subject", required = true)
  var subject: String = ""

  @Parameter(names = Array("-c", "--text"), description = "Email's text", required = true)
  var text: String = ""

}

new JCommander(Args, args.toArray: _*)

val smtpProperties = new Properties()
smtpProperties.put("mail.smtp.port", Args.port)
smtpProperties.put("mail.smtp.host", Args.host)

val session = Session.getDefaultInstance(smtpProperties)
val message = new MimeMessage(session)

message.setFrom(new InternetAddress(Args.from))
message.addRecipient(RecipientType.TO, new InternetAddress(Args.to))
message.setSubject(Args.subject)
message.setText(Args.text)

import java.io.FileOutputStream
import java.io.File

try {
    Transport.send(message)
 } catch { case t: Throwable => {
   println("Error:" + t.getMessage())
 }
}
