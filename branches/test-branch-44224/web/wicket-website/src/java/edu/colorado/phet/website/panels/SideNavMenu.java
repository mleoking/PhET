package edu.colorado.phet.website.panels;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.menu.NavMenuList;
import edu.colorado.phet.website.util.PageContext;

public class SideNavMenu extends PhetPanel {

    public SideNavMenu( String id, final PageContext context, Collection<NavLocation> currentLocations ) {
        super( id, context );

        List<NavLocation> locations = getNavMenu().getLocations();
        add( new NavMenuList( "side-nav-menu", context, locations, currentLocations, 0 ) );

    }

}