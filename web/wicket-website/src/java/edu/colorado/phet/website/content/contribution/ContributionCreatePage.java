package edu.colorado.phet.website.content.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.ContributionEditPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionCreatePage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( ContributionCreatePage.class.getName() );

    public ContributionCreatePage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();

        // TODO: localize
        addTitle( "Create a Contribution" );

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.submit" ) );

        add( new ContributionEditPanel( "contribution-edit-panel", getPageContext(), new Contribution() ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/submit$", ContributionCreatePage.class );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "contributions/submit";
            }
        };
    }

}
