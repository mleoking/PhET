/*  */
package edu.colorado.phet.waveinterference.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 3:37:21 PM
 */

public class CompositePotential implements Potential {
    private ArrayList p = new ArrayList();

    public void addPotential( Potential potential ) {
        p.add( potential );
    }

    public double getPotential( int x, int y, int timestep ) {
        double sum = 0;
        for( int i = 0; i < p.size(); i++ ) {
            Potential a = (Potential)p.get( i );
            sum += a.getPotential( x, y, timestep );
        }
        return sum;
    }

    public void clear() {
        p.clear();
    }

    public void removePotential( Potential potential ) {
        p.remove( potential );
    }

    public int numPotentials() {
        return p.size();
    }

    public Potential potentialAt( int i ) {
        return (Potential)p.get( i );
    }
}
