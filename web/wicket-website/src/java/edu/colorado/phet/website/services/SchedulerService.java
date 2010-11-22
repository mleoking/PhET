package edu.colorado.phet.website.services;

import it.sauronsoftware.cron4j.Scheduler;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.newsletter.InitialSubscribePanel;
import edu.colorado.phet.website.notification.NotificationHandler;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.SearchUtils;

/**
 * Consolidates schedulers so we can run background tasks. Single initialization and destruction for schedulers.
 */
public class SchedulerService {

    private static Scheduler masterScheduler;

    private static final Logger logger = Logger.getLogger( NotificationHandler.class.getName() );

    public static synchronized void initialize( final PhetWicketApplication app, final PhetLocalizer localizer ) {
        masterScheduler = new Scheduler();
        masterScheduler.schedule( "59 23 * * *", new Runnable() {
            public void run() {
                logger.info( "Running SearchUtils.reindex" );
                SearchUtils.reindex( app, localizer );
            }
        } );
        masterScheduler.schedule( "59 * * * *", new Runnable() {
            public void run() {
                logger.info( "Running InitialSubscribePanel.resetSecurity" );
                InitialSubscribePanel.resetSecurity();
            }
        } );
        masterScheduler.schedule( "59 23 * * fri", new Runnable() {
            public void run() {
                logger.info( "Running NotificationHandler.sendNotifications" );
                NotificationHandler.sendNotifications();
            }
        } );

        masterScheduler.start();
    }

    public static synchronized void destroy() {
        if ( masterScheduler != null ) {
            masterScheduler.stop();
        }
    }

}
