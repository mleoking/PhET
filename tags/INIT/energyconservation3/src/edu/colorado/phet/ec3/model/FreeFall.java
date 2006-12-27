/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:57 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeFall implements UpdateMode {
    private Vector2D.Double netForce;

    public FreeFall( double gravity ) {
        this( new Vector2D.Double( 0, gravity ) );
    }

    public FreeFall( Vector2D.Double gravity ) {
        this.netForce = new Vector2D.Double( gravity );
    }

    public void setNetForce( Vector2D.Double netForce ) {
        this.netForce = netForce;
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        AbstractVector2D acceleration = netForce.getScaledInstance( 1.0 / body.getMass() );
        AbstractVector2D velocity = body.getVelocity().getAddedInstance( acceleration.getScaledInstance( dt ) );
        Point2D newPosition = new Point2D.Double( body.getX() + velocity.getX() * dt, body.getY() + velocity.getY() * dt );
        body.setState( acceleration, velocity, newPosition );
    }
}
