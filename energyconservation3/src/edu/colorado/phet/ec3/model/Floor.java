/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:05:30 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class Floor {
    private double y;
    private Vector2D normal = new Vector2D.Double( 0, 1 );
    private EnergySkateParkModel model;

    public Floor( EnergySkateParkModel model, double y ) {
        this.model = model;
        this.y = y;
    }

    public Floor copyState() {
        Floor copy = new Floor( model, y );
        copy.normal.setComponents( normal.getX(), normal.getY() );
        return copy;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < model.numBodies(); i++ ) {
            Body b = model.bodyAt( i );
            stepInTime( b, dt );
        }
    }

    private void stepInTime( Body b, double dt ) {
        if( b.getMinY() < y && !b.isSplineMode() ) {
            double origEnergy = b.getTotalEnergy();
            double overshoot = b.getMinY() - y;
            b.setAttachmentPointPosition( b.getX(), b.getY() - overshoot );
            AbstractVector2D scaledInstance = new ImmutableVector2D.Double( b.getVelocity().getX(), Math.abs( b.getVelocity().getY() ) );
            b.setVelocity( scaledInstance );
            new EnergyConserver().fixEnergy( b, origEnergy );//todo add friction to floor
        }
    }

    public double getY() {
        return y;
    }

    public Vector2D getNormal() {
        return normal;
    }

}
