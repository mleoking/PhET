/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.ec3.model.spline.AbstractSpline;

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

    public EnergyConservationModel() {
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.stepInTime( this, dt );
        }
        for( int i = 0; i < floors.size(); i++ ) {
            floorAt( i ).stepInTime( dt );
        }
        for( int i = 0; i < splines.size(); i++ ) {
            testGrab( splineAt( i ) );
        }
    }

    private void testGrab( AbstractSpline spline ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            testGrab( spline, bodyAt( i ) );
        }
    }

    private void testGrab( AbstractSpline spline, Body body ) {
        Area area = new Area( spline.getAreaShape() );
        area.intersect( new Area( body.getLocatedShape() ) );
        if( !area.isEmpty() ) {
//            System.out.println( "intersected" );
            body.setSplineMode( spline );
        }
    }

    private AbstractSpline splineAt( int i ) {
        return (AbstractSpline)splines.get( i );
    }

    private Floor floorAt( int i ) {
        return (Floor)floors.get( i );
    }

    public void addSpline( AbstractSpline spline ) {
        splines.add( spline );
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
}
