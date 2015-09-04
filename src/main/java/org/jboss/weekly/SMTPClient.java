package org.jboss.weekly;
/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class SMTPClient {

    private static final String SMTP_HOST_FIELD = "mail.smtp.host";
    private static final String SMTP_PORT_FIELD = "mail.smtp.port";

    private final String smtpHostname;
    private final String smtpPort;

    public SMTPClient(String smtpHostname, String smtpPort) {
        this.smtpHostname = smtpHostname;
        this.smtpPort = smtpPort;
    }

    private Session getSession() {
        Properties properties = System.getProperties();
        properties.setProperty(SMTP_HOST_FIELD, smtpHostname);
        properties.setProperty(SMTP_PORT_FIELD, smtpPort);
        return Session.getDefaultInstance(properties);
    }

    private void createAndSendEMail(String to, String from, String subject, String text) throws MessagingException {
        MimeMessage message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }

    public void sendEmail(String to, String from, String subject, String text) {
        try {
            if (canConnectToHost() && !isEmailEmpty(text))
                createAndSendEMail(to, from, subject, text);
            else
                System.err.println("Can't connect to host " + smtpHostname + " or no email content:" + text);
        } catch (MessagingException mex) {
            throw new IllegalStateException(mex);
        }
    }

    private boolean isEmailEmpty(String text) {
        return (text == null || "".equals(text));
    }

    private boolean canConnectToHost() {
        try {
            return InetAddress.getByName(smtpHostname).isReachable(500000);
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

}
