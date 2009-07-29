package edu.colorado.phet.wickettest.util;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.SideNavMenu;

public abstract class PhetRegularPage extends PhetPage {
    public PhetRegularPage( PageParameters parameters ) {
        super( parameters, true );
    }

    public void initializeMenu( NavLocation currentLocation ) {
        System.out.println( "Initializing menu with current location: " + currentLocation );
        add( new SideNavMenu( "side-navigation", getPageContext(), currentLocation ) );
    }
}
