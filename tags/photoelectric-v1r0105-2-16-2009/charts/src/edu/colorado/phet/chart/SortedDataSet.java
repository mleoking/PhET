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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
        ArrayList dataPoints = super.getDataPoints();
        dataPoints.addAll( Arrays.asList( pts ) );
        Collections.sort( dataPoints, comparator );
        setDataPoints( dataPoints );
    }

    public void addPoint( Point2D pt ) {
        ArrayList dataPoints = super.getDataPoints();
        dataPoints.add( pt );
        Collections.sort( dataPoints, comparator );
        setDataPoints( dataPoints );
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
