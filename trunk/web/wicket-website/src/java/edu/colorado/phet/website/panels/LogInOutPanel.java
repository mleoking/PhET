package edu.colorado.phet.website.panels;

import org.apache.wicket.markup.html.link.StatelessLink;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.util.PageContext;

public class LogInOutPanel extends PhetPanel {
    public LogInOutPanel( String id, PageContext context ) {
        super( id, context );

        final PhetSession psession = PhetSession.get();
        if ( psession != null && psession.isSignedIn() ) {
            add( new StatelessLink( "sign-out" ) {
                public void onClick() {
                    PhetSession.get().signOut();
                    setResponsePage( IndexPage.class );
                }
            } );
        }
        else {
            add( new InvisibleComponent( "sign-out" ) );
        }
    }
}
