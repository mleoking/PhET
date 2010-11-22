package edu.colorado.phet.website.notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.mail.BodyPart;

import org.apache.log4j.Logger;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.WebsiteProperties;
import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.data.NotificationEvent;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionComment;
import edu.colorado.phet.website.data.contribution.ContributionNomination;
import edu.colorado.phet.website.data.util.AbstractChangeListener;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.util.EmailUtils;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

/**
 * Handles email notification of events that should be reviewed by the PhET team
 */
public class NotificationHandler {
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

    public static void sendNotifications() {
        final String mailHost = websiteProperties.getMailHost();
        final String mailUser = websiteProperties.getMailUser();
        final String mailPassword = websiteProperties.getMailPassword();
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
        final List<String> emailAddresses = new ArrayList<String>();
        emailAddresses.add( "phetadmin@gmail.com" );
        for ( PhetUser user : usersToNotify ) {
            emailAddresses.add( user.getEmail() );
        }
        String body = eventsToString( events );
        final String subject = "[PhET Website] Notifications for Teaching Ideas";
        final ArrayList<BodyPart> additionalParts = new ArrayList<BodyPart>();//other than the message itself which is specified in body

        EmailUtils.sendMessage( mailHost, mailUser, mailPassword, emailAddresses, body, WebsiteConstants.PHET_NO_REPLY_EMAIL_ADDRESS, subject, additionalParts );
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
        cal.add( Calendar.HOUR, -24 * 7 );
        List list = session.createQuery( "select ne from NotificationEvent as ne where ne.createdAt >= :date" )
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
