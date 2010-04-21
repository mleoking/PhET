package edu.colorado.phet.website.notification;

import java.util.Properties;
import java.util.Date;

import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.hibernate.*;

import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.NotificationEvent;
import edu.colorado.phet.website.data.NotificationEventType;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.HibernateTask;

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

    public static void onNewContribution( final Contribution contribution ) {
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( org.hibernate.Session session ) {
                NotificationEvent event = new NotificationEvent();
                event.setCreatedAt( new Date() );
                event.setType( NotificationEventType.NEW_CONTRIBUTION );
                event.setData( "contribution_id=" + contribution.getId() );
                session.save( event );
                return true;
            }
        } );
    }
}
