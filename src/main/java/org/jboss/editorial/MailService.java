package org.jboss.editorial;

import java.util.Optional;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import javax.inject.Inject;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class MailService {

	@Inject
	Mailer mailer;

	public void send(String to, String title, String message) {
		mailer.send(Mail.withText(to, title, message));
	}

    public void test() {
		this.send("rpelisse@redhat.com", "Test", "This is a test");
    }
}
