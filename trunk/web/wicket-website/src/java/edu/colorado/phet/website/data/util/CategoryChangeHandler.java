package edu.colorado.phet.website.data.util;

import java.util.Set;
import java.util.HashSet;

import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.Category;

public class CategoryChangeHandler {
    private static Set<Listener> listeners = new HashSet<Listener>();

    public static synchronized void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static synchronized void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static synchronized void notify( Category category, Simulation simulation ) {
        for ( Listener listener : listeners ) {
            listener.categoryUpdated( category, simulation );
        }
    }

    public interface Listener {
        public void categoryUpdated( Category category, Simulation simulation );
    }
}
