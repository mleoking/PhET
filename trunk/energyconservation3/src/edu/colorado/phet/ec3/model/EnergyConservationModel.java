/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;
import edu.colorado.phet.ec3.model.spline.SegmentPath;

import java.awt.geom.Area;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:16 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EnergyConservationModel {
    private ArrayList bodies = new ArrayList();
    private ArrayList floors = new ArrayList();
    private ArrayList splines = new ArrayList();
    private double gravity = 9.8;
    private ArrayList listeners = new ArrayList();
    private double zeroPointPotentialY;
    private double thermalEnergy = 0.0;

    public EnergyConservationModel( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.preStep( dt );
        }
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.stepInTime( this, dt );
        }
        for( int i = 0; i < floors.size(); i++ ) {
            floorAt( i ).stepInTime( dt );
        }

        doGrabs();
    }

    private void doGrabs() {
        for( int i = 0; i < bodies.size(); i++ ) {
            if( bodyAt( i ).isFreeFallMode() ) {
//                System.out.println( "EnergyConservationModel.doGrabs@" + System.currentTimeMillis() );
                doGrab( bodyAt( i ) );
            }
        }
    }

    private void doGrab( Body body ) {
        double bestScore = Double.POSITIVE_INFINITY;
        AbstractSpline bestSpline = null;
        for( int i = 0; i < splines.size(); i++ ) {
            AbstractSpline abstractSpline = (AbstractSpline)splines.get( i );
            double score = getGrabScore( abstractSpline, body );
            if( score < bestScore ) {
                bestScore = score;
                bestSpline = abstractSpline;
            }
        }
        if( bestSpline != null ) {
            body.setSplineMode( bestSpline );
        }
    }

    boolean intersectsOrig( AbstractSpline spline, Body body ) {

        Area area = new Area( body.getLocatedShape() );
        area.intersect( spline.getArea() );
        return !area.isEmpty();
    }

    boolean intersects( AbstractSpline spline, Body body ) {
        if( spline.getAreaShape().getBounds2D().intersects( body.getLocatedShape().getBounds2D() ) ) {
            SegmentPath path = spline.getSegmentPath();
            boolean intersects = path.intersects( body.getLocatedShape().getBounds2D() );
            return intersects;
        }
        else {
            return false;
        }
    }

    private double getGrabScore( AbstractSpline spline, Body body ) {
        boolean intersects = intersects( spline, body );
        if( intersects ) {
//            System.out.println( "intersected" );
            double position = 0;
            try {
                position = new SplineLogic( body ).guessPositionAlongSpline( spline );
            }
            catch( NullIntersectionException e ) {
//                e.printStackTrace();
                return Double.POSITIVE_INFINITY;
            }
            Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.

            double bodyYPerp = segment.getUnitNormalVector().dot( body.getPositionVector() );
            double segmentYPerp = segment.getUnitNormalVector().dot( new ImmutableVector2D.Double( segment.getCenter2D() ) );

            double y = ( bodyYPerp - segmentYPerp - body.getHeight() / 2.0 );

            if( y > -20 ) {
                return -y;
            }
            else {
                return Double.POSITIVE_INFINITY;
            }

        }
        return Double.POSITIVE_INFINITY;
    }

    public AbstractSpline splineAt( int i ) {
        return (AbstractSpline)splines.get( i );
    }

    public Floor floorAt( int i ) {
        return (Floor)floors.get( i );
    }

    public void addSpline( AbstractSpline spline, AbstractSpline reverse ) {
        splines.add( spline );
        splines.add( reverse );
    }

    public void addBody( Body body ) {
        bodies.add( body );
    }

    public int numBodies() {
        return bodies.size();
    }

    public Body bodyAt( int i ) {
        return (Body)bodies.get( i );
    }

    public void addFloor( Floor floor ) {
        floors.add( floor );
    }

    public double getPotentialEnergy( Body body ) {
        double h = zeroPointPotentialY - body.getY();
        return body.getMass() * gravity * h;
    }

    public double getTotalEnergy( Body body ) {
        double ke = body.getKineticEnergy();
        double pe = getPotentialEnergy( body );
        return ke + pe;
    }

    public double getGravity() {
        return gravity;
    }

    public void removeSpline( AbstractSpline spline ) {
        splines.remove( spline );
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.splineRemoved( spline );
        }
    }

    public double getZeroPointPotentialY() {
        return zeroPointPotentialY;
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }

    public void reset() {
        bodies.clear();
//        floors.clear();
        splines.clear();
        gravity = 9.8;
        thermalEnergy = 0.0;
    }

    public static interface EnergyModelListener {

        void preStep( double dt );
    }

    public void addEnergyModelListener( EnergyModelListener listener ) {
        listeners.add( listener );
    }
}
