/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 28, 2005
 * Time: 10:47:11 PM
 * Copyright (c) Sep 28, 2005 by Sam Reid
 */

public class ForceMode implements UpdateMode {
    private Vector2D.Double netForce;

    public ForceMode() {
        this( new Vector2D.Double() );
    }

    public ForceMode( Vector2D.Double gravity ) {
        this.netForce = new Vector2D.Double( gravity );
    }

    public void setNetForce( Vector2D.Double netForce ) {
        this.netForce = new Vector2D.Double( netForce );
    }

    protected Vector2D.Double getNetForce() {
        return netForce;
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        AbstractVector2D acceleration = getNetForce().getScaledInstance( 1.0 / body.getMass() );
        AbstractVector2D velocity = body.getVelocity().getAddedInstance( acceleration.getScaledInstance( dt ) );
        Point2D newPosition = new Point2D.Double( body.getX() + velocity.getX() * dt, body.getY() + velocity.getY() * dt );
        body.setState( acceleration, velocity, newPosition );
    }
}
