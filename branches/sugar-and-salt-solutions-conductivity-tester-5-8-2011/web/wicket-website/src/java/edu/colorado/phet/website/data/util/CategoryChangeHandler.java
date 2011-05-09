package edu.colorado.phet.website.data.util;

import java.util.HashSet;
import java.util.Set;

import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.Simulation;

public class CategoryChangeHandler {
    private static Set<Listener> listeners = new HashSet<Listener>();

    public static synchronized void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static synchronized void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static synchronized void notifySimulationChange( Category category, Simulation simulation ) {
        for ( Listener listener : listeners.toArray( new Listener[0] ) ) {
            listener.categorySimulationChanged( category, simulation );
        }
    }

    public static synchronized void notifyAdded( Category category ) {
        for ( Listener listener : listeners.toArray( new Listener[0] ) ) {
            listener.categoryAdded( category );
        }
    }

    public static synchronized void notifyRemoved( Category category ) {
        for ( Listener listener : listeners.toArray( new Listener[0] ) ) {
            listener.categoryRemoved( category );
        }
    }

    public static synchronized void notifyChildrenReordered( Category category ) {
        for ( Listener listener : listeners.toArray( new Listener[0] ) ) {
            listener.categoryChildrenReordered( category );
        }
    }

    public interface Listener {
        public void categorySimulationChanged( Category category, Simulation simulation );

        public void categoryAdded( Category category );

        public void categoryRemoved( Category category );

        public void categoryChildrenReordered( Category category );
    }

}
