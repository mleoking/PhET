package edu.colorado.phet.wickettest.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.SideNavMenu;

public abstract class PhetRegularPage extends PhetPage {
    public PhetRegularPage( PageParameters parameters ) {
        super( parameters, true );
    }

    public void initializeMenu( NavLocation currentLocation ) {
        Set<NavLocation> currentLocations = new HashSet<NavLocation>();
        currentLocations.add( currentLocation );
        initializeMenuWithSet( currentLocations );
    }

    public void initializeMenuWithSet( Set<NavLocation> currentLocations ) {
        add( new SideNavMenu( "side-navigation", getPageContext(), currentLocations ) );
    }
}
