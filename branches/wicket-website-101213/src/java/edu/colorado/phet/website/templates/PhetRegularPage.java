package edu.colorado.phet.website.templates;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.NavBreadCrumbs;

/**
 * This is a page with breadcrumbs (title), menu on the left, and the PhET header and footer
 */
public class PhetRegularPage extends PhetMenuPage {
    public PhetRegularPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    public void initializeLocation( NavLocation currentLocation ) {
        super.initializeLocation( currentLocation );
        add( new NavBreadCrumbs( "breadcrumbs", getPageContext(), currentLocation ) );
    }
}
