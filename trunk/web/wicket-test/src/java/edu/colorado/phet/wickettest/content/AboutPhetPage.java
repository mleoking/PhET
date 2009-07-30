package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.util.*;

public class AboutPhetPage extends PhetRegularPage {
    public AboutPhetPage( PageParameters parameters ) {
        super( parameters );

        addTitle( new ResourceModel( "about.title" ) );
        initializeLocation( getNavMenu().getLocationByKey( "about" ) );

        add( new AboutPhetPanel( "about-page", getPageContext() ) );
    }

    public static PhetLink createLink( String id ) {
        return new PhetLink( id, "/about" );
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + "about" );
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^about$", AboutPhetPage.class );
    }
}
