/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
    private double mass = 200.0;
    private double angle = 0.0;
    private Shape bounds;
    private FreeFall freeFall = new FreeFall( 0 );
    private UpdateMode mode = freeFall;
    private boolean facingRight;
    private final UserControlled userMode = new UserControlled();
    private double xThrust = 0.0;
    private double yThrust = 0.0;

    public Body( Shape bounds ) {
        this.bounds = bounds;
    }

    public void stepInTime( EnergyConservationModel energyConservationModel, double dt ) {
        EnergyDebugger.stepStarted( energyConservationModel, this, dt );
        getMode().stepInTime( energyConservationModel, this, dt );
        if( !isFreeFallMode() && !isUserControlled() ) {
            facingRight = getVelocity().dot( Vector2D.Double.parseAngleAndMagnitude( 1, getAngle() ) ) > 0;
        }
    }

    private UpdateMode getMode() {
        return mode;
    }

    public double getY() {
        return position.y;
    }

    public void setPosition( double x, double y ) {
        Point2D origLoc = new Point2D.Double( position.getX(), position.getY() );
        position.x = x;
        position.y = y;
//        System.out.println( "Body.setPosition" );
        if( origLoc.distance( position ) > 10 ) {
            System.out.println( "Body.translate" );
        }
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
        Point2D origLoc = new Point2D.Double( position.getX(), position.getY() );
        position.x += dx;
        position.y += dy;
//        System.out.println( "Body.translate" );
        if( origLoc.distance( position ) > 10 ) {
            System.out.println( "Body.location jumped by: " + origLoc.distance( position ) );
        }
    }

    public void setVelocity( double vx, double vy ) {
        velocity.setComponents( vx, vy );
    }

    public boolean isUserControlled() {
        return mode == userMode;
    }

    public void setUserControlled( boolean userControlled ) {
        if( userControlled ) {
            setMode( userMode );
        }
        else {
            freeFall.setRotationalVelocity( 0.0 );
            setMode( freeFall );
        }
    }

    public double getMaxY() {
        return getLocatedShape().getBounds2D().getMaxY();
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

    public Shape getLocatedShape() {
        Rectangle2D b = bounds.getBounds2D();
        AffineTransform transform = new AffineTransform();
        transform.translate( getX() - b.getWidth() / 2, getY() - b.getHeight() / 2 );
        transform.rotate( angle, b.getWidth() / 2, b.getHeight() / 2 );

        return transform.createTransformedShape( b );
    }

    public void setSplineMode( AbstractSpline spline ) {
        boolean same = false;
        if( mode instanceof FreeSplineMode ) {
            FreeSplineMode sm = (FreeSplineMode)mode;
            if( sm.getSpline() == spline ) {
                same = true;
            }
        }
        if( !same ) {
//            setMode( new AttachedSplineMode( spline, this ) );
            setMode( new FreeSplineMode( spline, this ) );
        }

    }

    private void setMode( UpdateMode mode ) {
        this.mode = mode;
    }

    public void setFreeFallRotation( double dA ) {
        freeFall.setRotationalVelocity( dA );
    }

    public void setFreeFallMode() {
        setMode( freeFall );
    }

    void fixAngle() {
        while( angle < 0 ) {
            angle += Math.PI * 2;
        }
        while( angle > Math.PI * 2 ) {
            angle -= Math.PI * 2;
        }
    }

    public void setAngle( double angle ) {
        this.angle = angle;

    }

    public double getSpeed() {
        return getVelocity().getMagnitude();
    }

    public void setPosition( Point2D point2D ) {
        setPosition( point2D.getX(), point2D.getY() );
    }

    public AbstractVector2D getPositionVector() {
        return new ImmutableVector2D.Double( getPosition() );
    }

    public double getKineticEnergy() {
        return 0.5 * getMass() * getSpeed() * getSpeed();
    }

    public double getAngle() {
        return angle;
    }

    public void rotate( double dA ) {
        angle += dA;
        fixAngle();
    }

    public boolean isFreeFallMode() {
        return mode == freeFall;
    }

    public static Rectangle createDefaultBodyRect() {
//        return new Rectangle( 50, 20 );
        return new Rectangle( 50, 60 );
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setThrust( double xThrust, double yThrust ) {
        this.xThrust = xThrust;
        this.yThrust = yThrust;
    }

    public AbstractVector2D getThrust() {
        return new ImmutableVector2D.Double( xThrust, yThrust );
    }

    public void splineRemoved( AbstractSpline spline ) {
        if( mode instanceof FreeSplineMode ) {
            FreeSplineMode spm = (FreeSplineMode)mode;
            if( spm.getSpline() == spline ) {
                setFreeFallMode();
            }
        }
    }
}
