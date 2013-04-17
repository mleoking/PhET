// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 3:37:21 PM
 */

public class CompositePotential implements Potential {
    private ArrayList<Potential> p = new ArrayList<Potential>();

    public void addPotential( Potential potential ) {
        p.add( potential );
        notifyPotentialAdded();
    }

    public double getPotential( int x, int y, int timestep ) {
        double sum = 0;
        for ( int i = 0; i < p.size(); i++ ) {
            Potential a = (Potential) p.get( i );
            sum += a.getPotential( x, y, timestep );
        }
        return sum;
    }

    public void clear() {
        p.clear();
    }

    public void removePotential( Potential potential ) {
        p.remove( potential );
        notifyPotentialRemoved();
    }

    public int numPotentials() {
        return p.size();
    }

    public Potential getPotential( int i ) {
        return (Potential) p.get( i );
    }

    public static interface Listener {
        void potentialAdded();

        void potentialRemoved();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyPotentialRemoved() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).potentialRemoved();
        }
    }

    public void notifyPotentialAdded() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).potentialAdded();
        }
    }
}
