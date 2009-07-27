package edu.colorado.phet.wickettest.menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NavMenu {
    private HashMap<String, NavLocation> cache = new HashMap<String, NavLocation>();
    private List<NavLocation> locations = new LinkedList<NavLocation>();

    public NavMenu() {
        NavLocation home = new NavLocation( null, "home" );
        NavLocation simulations = new NavLocation( null, "simulations" );
        NavLocation troubleshooting = new NavLocation( null, "home" );
    }

    public void addMajorLocation( NavLocation location ) {
        locations.add( location );
        addLocation( location );
    }

    public void addLocation( NavLocation location ) {
        cache.put( location.getKey(), location );
    }

    public List<NavLocation> getLocations() {
        return locations;
    }

    public NavLocation getLocationByKey( String key ) {
        return cache.get( key );
    }
}
