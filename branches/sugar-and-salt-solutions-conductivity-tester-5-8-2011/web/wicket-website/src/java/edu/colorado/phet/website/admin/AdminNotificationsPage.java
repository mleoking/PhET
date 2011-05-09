package edu.colorado.phet.website.admin;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.notification.NotificationHandler;

public class AdminNotificationsPage extends AdminPage {
    public AdminNotificationsPage( PageParameters parameters ) {
        super( parameters );

        add( new RawLabel( "report", NotificationHandler.getEventsString( getHibernateSession() ) ) );
    }

}