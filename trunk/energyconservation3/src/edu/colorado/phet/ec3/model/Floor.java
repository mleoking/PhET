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
    private double y = 300;
    private Vector2D normal = new Vector2D.Double( 0, 1 );
    private EnergyConservationModel model;

    public Floor( EnergyConservationModel model ) {
        this.model = model;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < model.numBodies(); i++ ) {
            Body b = model.bodyAt( i );
            stepInTime( b, dt );
        }
    }

    private void stepInTime( Body b, double dt ) {
        if( b.getMaxY() > y ) {
            b.setPosition( b.getX(), y - b.getHeight() );
            AbstractVector2D scaledInstance = new ImmutableVector2D.Double( b.getVelocity().getX(), -Math.abs( b.getVelocity().getY() ) );
            b.setVelocity( scaledInstance );
        }
    }

    public double getY() {
        return y;
    }

    public Vector2D getNormal() {
        return normal;
    }
}
