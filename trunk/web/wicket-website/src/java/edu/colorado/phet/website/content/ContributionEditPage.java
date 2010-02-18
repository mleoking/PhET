package edu.colorado.phet.website.content;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;
import edu.colorado.phet.website.data.contribution.Contribution;

public class ContributionEditPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( ContributionEditPage.class.getName() );

    public ContributionEditPage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();

        String contributionIdString = parameters.getString( "contributionId" );
        int contributionId = Integer.parseInt( contributionIdString );

        // TODO: localize
        addTitle( "Edit a Contribution" );

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.edit" ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/edit/([^/]+)$", ContributionEditPage.class, new String[]{"contributionId"} );
    }

    public static RawLinkable getLinker( final int contributionId ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "contributions/edit/" + String.valueOf( contributionId );
            }
        };
    }

    public static RawLinkable getLinker( final Contribution contribution ) {
        return getLinker( contribution.getId() );
    }

}