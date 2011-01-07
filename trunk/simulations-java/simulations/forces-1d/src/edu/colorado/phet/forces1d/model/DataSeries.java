// Copyright 2002-2011, University of Colorado

/*PhET, 2004.*/
package edu.colorado.phet.forces1d.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:38:31 AM
 */
public class DataSeries {
    private ArrayList pts = new ArrayList();
    private ArrayList listeners = new ArrayList();

    public DataSeries() {
    }

    public void remove( int i ) {
        pts.remove( i );
    }

    public interface Listener {
        void changed();
    }

    public void addPoint( double x ) {
        this.pts.add( new Double( x ) );
        updateObservers();
    }

    public double getLastPoint() {
        return lastPointAt( 0 );
    }

    public int size() {
        return pts.size();
    }

    public void reset() {
        this.pts = new ArrayList();
        updateObservers();
    }

    private void updateObservers() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.changed();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double lastPointAt( int i ) {
        return pointAt( pts.size() - 1 - i );
    }

    public double pointAt( int i ) {

        return ( (Double) pts.get( i ) ).doubleValue();
    }

    public boolean indexInBounds( int index ) {
        return index >= 0 && index < pts.size();
    }

}
