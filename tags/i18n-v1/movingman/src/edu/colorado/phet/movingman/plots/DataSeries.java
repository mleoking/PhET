/*PhET, 2004.*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.movingman.common.AutomatedObservable;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:38:31 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class DataSeries extends AutomatedObservable implements ModelElement {
    private ArrayList pts = new ArrayList();

    public DataSeries() {
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

    public void stepInTime( double dt ) {
    }

    public void reset() {
        this.pts = new ArrayList();
        updateObservers();
    }

    public double lastPointAt( int i ) {
        return pointAt( pts.size() - 1 - i );
    }

    public double pointAt( int i ) {
        return ( (Double)pts.get( i ) ).doubleValue();
    }

    public boolean indexInBounds( int index ) {
        return index >= 0 && index < pts.size();
    }

}
