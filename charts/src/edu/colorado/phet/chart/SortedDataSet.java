/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.chart;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * SortedDataSet
 * <p>
 * A DataSet that maintains its data point in the natural sort order of the
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SortedDataSet extends DataSet {

    private static Comparator comparator = new SortedDataSetComparator();

    public void addPoints( Point2D[] pts ) {
        super.addPoints( pts );
        List dataPoints = super.getDataPoints();
        Collections.sort( dataPoints, comparator );
        notifyObservers( dataPoints );
    }

    public void addPoint( double x, double y ) {
        super.addPoint( x, y );
        List dataPoints = super.getDataPoints();
        Collections.sort( dataPoints, comparator );
        notifyObservers( dataPoints );
    }

    /**
     * Tells all DataSetGraphic observers to clear their data, then to add all the
     * data points. This is needed because their default behavior is optimized to
     * not redraw data that it has already drawn.
     * @param dataPoints
     */
    private void notifyObservers( List dataPoints ) {
        List observers = super.getObservers();
        for( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer)observers.get( i );
            if( observer instanceof DataSetGraphic ) {
                observer.cleared();
                observer.pointsAdded( (Point2D[])dataPoints.toArray(new Point2D[dataPoints.size()]) );
            }
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private static class SortedDataSetComparator implements Comparator {

        public int compare( Object o1, Object o2 ) throws ClassCastException {
            Point2D p1 = (Point2D)o1;
            Point2D p2 = (Point2D)o2;
            int result = 0;

            if( p1.getX() < p2.getX() ) {
                result = -1;
            }
            if( p1.getX() > p2.getX() ) {
                result = 1;
            }
            return result;
        }
    }
}
