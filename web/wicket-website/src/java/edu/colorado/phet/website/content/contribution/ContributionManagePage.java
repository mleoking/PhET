package edu.colorado.phet.website.content.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.panels.contribution.ContributionManagePanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionManagePage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( ContributionManagePage.class.getName() );

    public ContributionManagePage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();

        // TODO: localize
        addTitle( "Manage my Contributions" );

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.manage" ) );

        add( new ContributionManagePanel( "contribution-manage-panel", getPageContext() ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/manage$", ContributionManagePage.class );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "contributions/manage";
            }
        };
    }

}