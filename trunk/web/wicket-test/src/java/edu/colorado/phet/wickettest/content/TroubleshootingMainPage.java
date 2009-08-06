package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.util.*;

public class TroubleshootingMainPage extends PhetRegularPage {
    public TroubleshootingMainPage( PageParameters parameters ) {
        super( parameters );

        addTitle( new ResourceModel( "troubleshooting.main.title" ) );
        initializeLocation( getNavMenu().getLocationByKey( "troubleshooting" ) );

        add( new TroubleshootingMainPanel( "troubleshooting-page", getPageContext() ) );
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + "troubleshooting" );
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^troubleshooting$", TroubleshootingMainPage.class );
    }
}