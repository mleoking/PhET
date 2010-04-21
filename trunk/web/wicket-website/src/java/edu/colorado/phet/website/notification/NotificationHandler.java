package edu.colorado.phet.website.notification;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.data.NotificationEvent;
import edu.colorado.phet.website.data.NotificationEventType;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.util.AbstractChangeListener;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class NotificationHandler {
    private static Scheduler notificationScheduler;

    public static synchronized void initialize() {
        if ( notificationScheduler != null ) {
            // don't initialize first
            return;
        }

        notificationScheduler = new Scheduler();
        notificationScheduler.schedule( "59 11 * fri *", new Runnable() {
            public void run() {
                sendNotifications();
            }
        } );

        notificationScheduler.start();

        HibernateEventListener.addListener( Contribution.class, new AbstractChangeListener() {
            @Override
            public void onInsert( Object object, PostInsertEvent event ) {
                onNewContribution( (Contribution) object );
            }

            @Override
            public void onUpdate( Object object, PostUpdateEvent event ) {
                onUpdatedContribution( (Contribution) object );
            }
        } );
    }

    public static void sendNotifications() {

        final List<NotificationEvent> events = new LinkedList<NotificationEvent>();

        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( org.hibernate.Session session ) {
                Calendar cal = Calendar.getInstance();
                cal.add( Calendar.HOUR, 24 * 7 );
                List list = session.createQuery( "select ne from NotificationEvent as ne where ne.createdAt <= :date" )
                        .setDate( "date", cal.getTime() ).list();
                for ( Object o : list ) {
                    events.add( (NotificationEvent) o );
                }
                return true;
            }
        } );

        try {
            Properties props = System.getProperties();
            props.put( "mail.smtp.host", "mx.colorado.edu" );

            Session session = Session.getDefaultInstance( props, null );

            Message message = new MimeMessage( session );
            message.setFrom( new InternetAddress( "phetnoreply@phetsims.colorado.edu" ) );
            message.addRecipient( Message.RecipientType.TO, new InternetAddress( "jonathan.olson@colorado.edu" ) );
            message.setSubject( "This is a test subject" );

            BodyPart messageBodyPart = new MimeBodyPart();
            String body = "<p>The following events occurred in the last week:</p>";

            body += "<ul>";

            for ( NotificationEvent event : events ) {
                body += "<li>";
                body += event.toString();
                body += "</li>";
            }

            body += "</ul>";

            body += "<p>There were " + events.size() + " events.</p>";

            body += "<br/><p>Mailed automatically by the PhET website</p>";

            messageBodyPart.setContent( body, "text/html; charset=ISO-8859-1" );

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

    public static void onUpdatedContribution( final Contribution contribution ) {
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( org.hibernate.Session session ) {
                NotificationEvent event = new NotificationEvent();
                event.setCreatedAt( new Date() );
                event.setType( NotificationEventType.UPDATED_CONTRIBUTION );
                event.setData( "contribution_id=" + contribution.getId() );
                session.save( event );
                return true;
            }
        } );
    }

}
