package edu.colorado.phet.website.notification;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class NotificationHandler {
    public static void main( String[] args ) {
        try {
            Properties props = System.getProperties();
            props.put( "mail.smtp.host", "mx.colorado.edu" );

            Session session = Session.getDefaultInstance( props, null );

            Message message = new MimeMessage( session );
            message.setFrom( new InternetAddress( "phetnoreply@phetsims.colorado.edu" ) );
            message.addRecipient( Message.RecipientType.TO, new InternetAddress( "jonathan.olson@colorado.edu" ) );
            message.setSubject( "This is a test subject" );

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText( "This is the message of the test email" );

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart( messageBodyPart );

            // add attachments here, see example

            message.setContent( multipart );

            Transport.send( message );
        }
        catch( MessagingException e ) {
            e.printStackTrace();
        }
    }
}
