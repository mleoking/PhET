package edu.colorado.phet.website.notification;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.WebsiteProperties;
import edu.colorado.phet.website.data.NotificationEvent;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionComment;
import edu.colorado.phet.website.data.contribution.ContributionNomination;
import edu.colorado.phet.website.data.util.AbstractChangeListener;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

/**
 * Handles email notification of events that should be reviewed by the PhET team
 */
public class NotificationHandler {
    private static Scheduler notificationScheduler;

    /**
     * We need to grab the mail handler configuration from the server parameters
     */
    private static WebsiteProperties websiteProperties;

    private static final Logger logger = Logger.getLogger( NotificationHandler.class.getName() );

    public static synchronized void initialize( WebsiteProperties properties ) {
        websiteProperties = properties;

        if ( !properties.hasMailParameters() ) {
            logger.warn( "Was unable to find mail server credentials. Will not start the notification handler" );
            return;
        }

        if ( notificationScheduler != null ) {
            // don't initialize first
            return;
        }

        notificationScheduler = new Scheduler();
        notificationScheduler.schedule( "59 23 * * fri", new Runnable() {
            public void run() {
                sendNotifications();
            }
        } );

        notificationScheduler.start();

        HibernateEventListener.addListener( ContributionNomination.class, new AbstractChangeListener() {
            @Override
            public void onInsert( Object object, PostInsertEvent event ) {
                NotificationEventType.onNominatedContribution( (ContributionNomination) object );
            }
        } );

        HibernateEventListener.addListener( Contribution.class, new AbstractChangeListener() {
            @Override
            public void onInsert( Object object, PostInsertEvent event ) {
                NotificationEventType.onNewContribution( (Contribution) object );
            }

            @Override
            public void onUpdate( Object object, PostUpdateEvent event ) {
                NotificationEventType.onUpdatedContribution( (Contribution) object );
            }
        } );

        HibernateEventListener.addListener( ContributionComment.class, new AbstractChangeListener() {
            @Override
            public void onInsert( Object object, PostInsertEvent event ) {
                NotificationEventType.onContributionComment( (ContributionComment) object );
            }
        } );
    }

    public static synchronized void destroy() {
        if ( notificationScheduler != null ) {
            notificationScheduler.stop();
        }
    }

    public static void sendNotifications() {
        final List<NotificationEvent> events = new LinkedList<NotificationEvent>();
        final List<PhetUser> usersToNotify = new LinkedList<PhetUser>();

        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( org.hibernate.Session session ) {
                addEventsToList( session, events );
                List list = session.createQuery( "select u from PhetUser as u where u.teamMember = true and u.receiveWebsiteNotifications = true" ).list();
                for ( Object o : list ) {
                    usersToNotify.add( (PhetUser) o );
                }
                return true;
            }
        } );

        try {
            Properties props = System.getProperties();
            //props.put( "mail.smtp.host", "mx.colorado.edu" );

            props.put( "mail.smtp.host", websiteProperties.getMailHost() );

            props.put( "mail.debug", "true" );
            props.put( "mail.smtp.starttls.enable", "true" ); //necessary if you use cu or google, otherwise you receive an error:

            props.put( "mail.smtp.auth", "true" );
            props.put( "mail.smtp.user", websiteProperties.getMailUser() );
            props.put( "password", websiteProperties.getMailPassword() );

            Session session = Session.getInstance( props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication( websiteProperties.getMailUser(), websiteProperties.getMailPassword() );
                }
            } );

            Message message = new MimeMessage( session );
            message.setFrom( new InternetAddress( "phetnoreply@phetsims.colorado.edu" ) );
            for ( PhetUser user : usersToNotify ) {
                message.addRecipient( Message.RecipientType.TO, new InternetAddress( user.getEmail() ) );
            }
            message.addRecipient( Message.RecipientType.TO, new InternetAddress( "phetadmin@gmail.com" ) );
            message.setSubject( "[PhET Website] Events" );

            BodyPart messageBodyPart = new MimeBodyPart();
            String body = eventsToString( events );

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

    public static String getEventsString( org.hibernate.Session session ) {
        final List<NotificationEvent> events = new LinkedList<NotificationEvent>();
        boolean success = HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( org.hibernate.Session session ) {
                addEventsToList( session, events );
                return true;
            }
        } );
        if ( success ) {
            return eventsToString( events );
        }
        else {
            return "Error encountered in retrieving events";
        }
    }


    private static void addEventsToList( org.hibernate.Session session, List<NotificationEvent> events ) {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.HOUR, 24 * 7 );
        List list = session.createQuery( "select ne from NotificationEvent as ne where ne.createdAt <= :date" )
                .setDate( "date", cal.getTime() ).list();
        for ( Object o : list ) {
            events.add( (NotificationEvent) o );
        }
    }

    private static String eventsToString( List<NotificationEvent> events ) {
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
        return body;
    }

}
