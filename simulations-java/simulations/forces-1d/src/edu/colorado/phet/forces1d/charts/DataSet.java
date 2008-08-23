/**
 * Class: DataSet
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.forces1d.charts;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DataSet {
    private ArrayList dataPoints = new ArrayList();
    private ArrayList observers = new ArrayList();

    public Point2D pointAt( int i ) {
        return (Point2D) dataPoints.get( i );
    }

    public int size() {
        return dataPoints.size();
    }

    public Range2D getRange() {
        if ( size() == 0 ) {
            return null;
        }
        double minX = pointAt( 0 ).getX();
        double maxX = pointAt( 0 ).getX();
        double minY = pointAt( 0 ).getY();
        double maxY = pointAt( 0 ).getY();
        for ( int i = 1; i < dataPoints.size(); i++ ) {
            Point2D point2D = (Point2D) dataPoints.get( i );
            double x = point2D.getX();
            double y = point2D.getY();
            if ( x < minX ) {
                minX = x;
            }
            if ( x > maxX ) {
                maxX = x;
            }
            if ( y < minY ) {
                minY = y;
            }
            if ( y > maxY ) {
                maxY = y;
            }
        }
        return new Range2D( minX, minY, maxX, maxY );
    }

    public void removePoint( int index ) {
        dataPoints.remove( index );
    }

    public Point2D getLastPoint() {
        if ( size() == 0 ) {
            throw new RuntimeException( "No such point 'last point', size=0" );
        }
        return pointAt( size() - 1 );
    }

    public void clear() {
        dataPoints.clear();
        for ( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer) observers.get( i );
            observer.cleared();
        }
    }

    public interface Observer {
        void pointAdded( Point2D point );

        void pointRemoved( Point2D point );

        void cleared();
    }

    public boolean isValid( Point2D dataPoint ) {
        if ( dataPoint == null ) {
            return false;
        }
        boolean invalid = Double.isNaN( dataPoint.getX() ) || Double.isNaN( dataPoint.getY() ) || Double.isInfinite( dataPoint.getX() ) || Double.isInfinite( dataPoint.getY() );
        return !invalid;
    }

    public void addPoint( Point2D dataPoint ) {
        if ( !isValid( dataPoint ) ) {
            throw new RuntimeException( "Illegal data point: " + dataPoint );
        }
        if ( dataPoint == null ) {
            throw new RuntimeException( "Null data Point" );
        }

// Iterate over observers
        dataPoints.add( dataPoint );
        notifyObservers( dataPoint );
    }

    public void addPoint( double x, double y ) {
        addPoint( new Point2D.Double( x, y ) );
    }

    public Point2D.Double[] toArray() {
        Point2D.Double[] pts = (Point2D.Double[]) dataPoints.toArray( new Point2D.Double[0] );
        return pts;
    }

    private void notifyObservers( Point2D dataPoint ) {
        for ( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer) observers.get( i );
            observer.pointAdded( dataPoint );
        }
    }

    public void addObserver( Observer observer ) {
        observers.add( observer );
    }

    public String toString() {
        return dataPoints.toString();
    }
}
