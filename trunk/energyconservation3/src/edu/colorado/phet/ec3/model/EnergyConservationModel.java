/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

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
    }

    private Floor floorAt( int i ) {
        return (Floor)floors.get( i );
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
