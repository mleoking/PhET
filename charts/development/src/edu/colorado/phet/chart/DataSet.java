/**
 * Class: DataSet
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DataSet {
    ArrayList dataPoints = new ArrayList( );

    public Point2D pointAt( int i ) {
        return null;
    }

    public int size() {
        return dataPoints.size();
    }

    public interface Observer {
        void pointAdded( Point2D point );
        void pointRemoved( Point2D point );
    }

    public void addPoint( Point2D dataPoint ) {
        // Iterate over observers
    }

    public void addPoint( double x, double y ) {
        // Iterate over observers
    }

    public void addObserver( Observer observer ) {
    }
}
