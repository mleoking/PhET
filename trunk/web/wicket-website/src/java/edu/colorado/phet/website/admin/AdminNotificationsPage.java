package edu.colorado.phet.website.admin;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.notification.NotificationHandler;
import edu.colorado.phet.website.components.RawLabel;

public class AdminNotificationsPage extends AdminPage {
    public AdminNotificationsPage( PageParameters parameters ) {
        super( parameters );

        add( new RawLabel( "report", NotificationHandler.getEventsString( getHibernateSession() ) ) );
    }

}