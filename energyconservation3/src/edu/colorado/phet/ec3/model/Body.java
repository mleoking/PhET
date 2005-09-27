/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:02:49 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class Body {
    private Point2D.Double position = new Point2D.Double();
    private Vector2D velocity = new Vector2D.Double();
    private Vector2D.Double acceleration = new Vector2D.Double();
    private double mass = 500.0;
    private Shape bounds;

    private boolean onSpline = false;
    private boolean userControlled = false;

    public Body( Shape bounds ) {
        this.bounds = bounds;
    }

    public void stepInTime( EnergyConservationModel energyConservationModel, double dt ) {
        getMode().stepInTime( energyConservationModel, this, dt );
    }

    private UpdateMode getMode() {
        if( userControlled ) {
            return new UserControlled();
        }
        else {
            if( onSpline ) {
                return new SplineMode();
            }
            else {
                return new FreeFall( 9.8 );
            }
        }
//        throw new RuntimeException("Update Mode not found");
    }

    public double getY() {
        return position.y;
    }

    public void setPosition( double x, double y ) {
        position.x = x;
        position.y = y;
    }

    public double getX() {
        return position.x;
    }

    public void setVelocity( AbstractVector2D vector2D ) {
        this.velocity.setComponents( vector2D.getX(), vector2D.getY() );
    }

    public AbstractVector2D getVelocity() {
        return velocity;
    }

    public void translate( double dx, double dy ) {
        position.x += dx;
        position.y += dy;
    }

    public void setVelocity( double vx, double vy ) {
        velocity.setComponents( vx, vy );
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled = userControlled;
    }

    public double getMaxY() {
        return getY() + bounds.getBounds2D().getHeight();
    }

    public Shape getShape() {
        return bounds;
    }

    public double getHeight() {
        return bounds.getBounds2D().getHeight();
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public AbstractVector2D getAcceleration() {
        return acceleration;
    }

    public double getMass() {
        return mass;
    }

    public void setState( AbstractVector2D acceleration, AbstractVector2D velocity, Point2D newPosition ) {
        this.acceleration.setComponents( acceleration.getX(), acceleration.getY() );
        this.velocity.setComponents( velocity.getX(), velocity.getY() );
        this.position.x = newPosition.getX();
        this.position.y = newPosition.getY();
    }
}
