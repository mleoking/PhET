package edu.colorado.phet.wickettest.util;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.NavBreadCrumbs;

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
