/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
    private double mass = 75.0;//kg
    private double angle = Math.PI;

    private boolean facingRight;

    private double xThrust = 0.0;
    private double yThrust = 0.0;

    private FreeFall freeFall = new FreeFall( 0 );
    private UserControlled userMode = new UserControlled();

    private UpdateMode mode = freeFall;
    private double frictionCoefficient = 0.0;
    private double coefficientOfRestitution = 1.0;
    private ArrayList listeners = new ArrayList();
    private double totalSystemEnergy;
    private double width;
    private double height;

    public Body( double width, double height ) {
        this.width = width;
        this.height = height;
    }

    public Body copyState() {
        Body copy = new Body( width, height );//todo better deep copy of bounds?
        copy.position.setLocation( position );
        copy.velocity.setComponents( velocity.getX(), velocity.getY() );
        copy.acceleration.setComponents( acceleration.getX(), velocity.getY() );
        copy.mass = mass;
        copy.angle = angle;
        copy.mode = freeFall;//todo get the mode switch correct.
        copy.facingRight = facingRight;
        copy.xThrust = xThrust;
        copy.yThrust = yThrust;
        copy.coefficientOfRestitution = coefficientOfRestitution;
        return copy;
    }

//    public void setBounds( Shape bounds ) {
//        this.bounds = bounds;
//    }

    double time = 0.0;

    public void stepInTime( EnergyConservationModel energyConservationModel, double dt ) {
        if( isUserControlled() || getMode() == freeFall ) {
            setTotalSystemEnergy( energyConservationModel.getTotalEnergy( this ) );
        }
        time += dt;
//        System.out.println( "getSpeed() = " + getSpeed() + ", time=" + time + ", y=" + getAttachPoint().getY() + ", man's height=" + getHeight() );

//        System.out.println( "Total Energy: " + energyConservationModel.getTotalEnergy( this ) );
        EnergyDebugger.stepStarted( energyConservationModel, this, dt );
//        int MAX = 50;
//        int MAX = 1;
//        int NUM_STEPS_PER_UPDATE = 5;
        int NUM_STEPS_PER_UPDATE = 5;
        for( int i = 0; i < NUM_STEPS_PER_UPDATE; i++ ) {
            getMode().stepInTime( energyConservationModel, this, dt / NUM_STEPS_PER_UPDATE );
        }
        if( !isFreeFallMode() && !isUserControlled() ) {
            facingRight = getVelocity().dot( Vector2D.Double.parseAngleAndMagnitude( 1, getAngle() ) ) > 0;
        }
        EnergyDebugger.stepFinished( energyConservationModel, this );
    }

    private UpdateMode getMode() {
        return mode;
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

    public AbstractVector2D getVelocity() {
        return velocity;
    }

    public void translate( double dx, double dy ) {
        position.x += dx;
        position.y += dy;
    }

    public void setVelocity( AbstractVector2D vector2D ) {
        setVelocity( vector2D.getX(), vector2D.getY() );
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

    public double getMinY() {
        return getLocatedShape().getBounds2D().getMinY();
    }

    public double getMaxY() {
        return getLocatedShape().getBounds2D().getMaxY();
    }

    public Shape getShape() {
        return new Rectangle2D.Double( 0, 0, width, height );
    }

    public double getHeight() {
        return height;
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double( position.getX(), position.getY() );
//        return position;
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
        return mode instanceof FreeFall;
    }

    public static PDimension createDefaultBodyRect() {
        return new PDimension( 1.3, 1.8 );
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setThrust( double xThrust, double yThrust ) {
        if( this.xThrust != xThrust || this.yThrust != yThrust ) {
            this.xThrust = xThrust;
            this.yThrust = yThrust;
            notifyThrustChanged();
        }
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

    public double getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public void setFrictionCoefficient( double value ) {
        this.frictionCoefficient = value;
    }

    public double getCoefficientOfRestitution() {
        return coefficientOfRestitution;
    }

    public void setCoefficientOfRestitution( double coefficientOfRestitution ) {
        this.coefficientOfRestitution = coefficientOfRestitution;
    }

    public void setMass( double value ) {
        this.mass = value;
    }

    public Point2D.Double getAttachPoint() {
        return new Point2D.Double( position.getX(), position.getY() );
    }

    public Shape getLocatedShape() {
        AffineTransform transform = getTransform();
        return transform.createTransformedShape( getShape() );
    }

    public AffineTransform getTransform() {
//        Rectangle2D b = bounds.getBounds2D();
        AffineTransform transform = new AffineTransform();
        transform.translate( getX() - getWidth() / 2, getY() - getHeight() / 2 );
        transform.rotate( angle, getWidth() / 2, getHeight() / 2 );
//        transform.rotate( angle, b.getWidth() / 2, b.getHeight() );
        return transform;
    }

    public void resetMode() {
        freeFall.reset();
    }

    public void setTotalSystemEnergy( double totalEnergy ) {
        this.totalSystemEnergy = totalEnergy;
    }

    public double getTotalSystemEnergy() {
        return totalSystemEnergy;
    }

    public double getWidth() {
        return width;
    }

    public static interface Listener {
        void thrustChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyThrustChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.thrustChanged();
        }
    }
}
