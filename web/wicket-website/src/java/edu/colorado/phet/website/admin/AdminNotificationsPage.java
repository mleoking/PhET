package edu.colorado.phet.website.admin;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.notification.NotificationHandler;

public class AdminNotificationsPage extends AdminPage {
    public AdminNotificationsPage( PageParameters parameters ) {
        super( parameters );

        Label label = new Label( "report", NotificationHandler.getEventsString( getHibernateSession() ) );
        label.setEscapeModelStrings( false );
        add( label );
    }

}