/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.spline;

import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 25, 2003
 * Time: 7:39:37 PM
 * Copyright (c) Jul 25, 2003 by Sam Reid
 */
public class Spline extends ModelElement {
    ArrayList controlPoints = new ArrayList();
    private ArrayList observers = new ArrayList();
    GeneralPath path = new GeneralPath();
    SegmentPath segmentPath = new SegmentPath();
    private int numSteps = 12;
    private NatCubic cubic;

    public int getNumSteps() {
        return numSteps;
    }

    public String toString() {
        return controlPoints.toString();
    }

    public void addControlPoint( double x, double y ) {
        controlPoints.add( new Point2D.Double( x, y ) );
        for( int i = 0; i < observers.size(); i++ ) {
            SplineObserver splineObserver = (SplineObserver)observers.get( i );
            splineObserver.pointStructureChanged( this );
        }
        recomputePath();
    }

    public Spline copySpline() {
        Spline s = new Spline();
        for( int i = 0; i < numPoints(); i++ ) {
            s.addControlPoint( pointAt( i ).x, pointAt( i ).y );
        }
        return s;
    }

    public GeneralPath getPath() {
        return path;
    }

    private void recomputePath() {
        cubic = new NatCubic();
        for( int i = 0; i < controlPoints.size(); i++ ) {
            Point2D.Double aDouble = (Point2D.Double)controlPoints.get( i );
            cubic.addPoint( (float)aDouble.x, (float)aDouble.y );
        }
        cubic.computePaintState( numSteps );
        path = cubic.getPath();
        segmentPath = cubic.getSegmentPath();
    }

    public SegmentPath getSegmentPath() {
        return segmentPath;
    }

    public void stepInTime( double dt ) {
    }

    public int numPoints() {
        return controlPoints.size();
    }

    public Point2D.Double pointAt( int i ) {
        return (Point2D.Double)controlPoints.get( i );
    }

    public void translate( double x, double y ) {
        for( int i = 0; i < controlPoints.size(); i++ ) {
            Point2D.Double pt = pointAt( i );
            pt.x += x;
            pt.y += y;
        }
        for( int i = 0; i < observers.size(); i++ ) {
            SplineObserver splineObserver = (SplineObserver)observers.get( i );
            splineObserver.splineTranslated( this, x, y );
        }
        recomputePath();
    }

    public void addSplineObserver( SplineObserver observer ) {
        observers.add( observer );
    }

    public void translatePoint( int index, double dx, double dy ) {
        Point2D.Double pt = pointAt( index );
        pt.x += dx;
        pt.y += dy;

        for( int i = 0; i < observers.size(); i++ ) {
            SplineObserver splineObserver = (SplineObserver)observers.get( i );
            splineObserver.splineTranslated( this, dx, dy );
        }
        recomputePath();
    }

    public int getNumSegments() {
        return this.numSteps;
    }

    public void insertControlPoint( int index, double x, double y ) {
        controlPoints.add( index, new Point2D.Double( x, y ) );
        firePointStructureChanged();
    }

    private void firePointStructureChanged() {
        for( int i = 0; i < observers.size(); i++ ) {
            SplineObserver splineObserver = (SplineObserver)observers.get( i );
            splineObserver.pointStructureChanged( this );
        }
        recomputePath();
    }

    public void removeControlPoint( int index ) {
        controlPoints.remove( index );
        firePointStructureChanged();
    }

}
