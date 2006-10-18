/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
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
    private Point2D.Double attachmentPoint = new Point2D.Double();
    private Vector2D velocity = new Vector2D.Double();
    private Vector2D.Double acceleration = new Vector2D.Double();
    private double mass = 75.0;
    private double attachmentPointRotation = 0;

    private boolean facingRight;

    private double xThrust = 0.0;
    private double yThrust = 0.0;

    private FreeFall freeFall;
    private UserControlled userMode = new UserControlled();

    private UpdateMode mode;
    private double frictionCoefficient = 0.0;
    private double coefficientOfRestitution = 1.0;
    private ArrayList listeners = new ArrayList();
    private double width;
    private double height;
    private PotentialEnergyMetric potentialEnergyMetric;
    private double cmRotation = 0;
    private double thermalEnergy = 0;

    public Body( double width, double height, PotentialEnergyMetric potentialEnergyMetric, FreeFall freeFall ) {
        this.freeFall = freeFall;
        this.width = width;
        this.height = height;
        this.potentialEnergyMetric = potentialEnergyMetric;
        cmRotation = Math.PI;
        mode = freeFall;
    }

    public Body copyState() {
        Body copy = new Body( width, height, potentialEnergyMetric, freeFall );
        copy.attachmentPoint.setLocation( attachmentPoint );
        copy.velocity.setComponents( velocity.getX(), velocity.getY() );
        copy.acceleration.setComponents( acceleration.getX(), velocity.getY() );
        copy.mass = mass;
        copy.attachmentPointRotation = attachmentPointRotation;
        copy.cmRotation = cmRotation;
        if( this.mode == freeFall ) {
            copy.mode = copy.freeFall;
        }
        else if( this.mode instanceof FreeSplineMode ) {
            FreeSplineMode splineMode = ( (FreeSplineMode)this.mode );
            copy.mode = splineMode.copy();

        }
        copy.thermalEnergy = this.thermalEnergy;
        copy.facingRight = facingRight;
        copy.xThrust = xThrust;
        copy.yThrust = yThrust;
        copy.coefficientOfRestitution = coefficientOfRestitution;
        copy.potentialEnergyMetric = potentialEnergyMetric.copy();
        return copy;
    }

    public void stepInTime( double dt ) {
        EnergyDebugger.stepStarted( this, dt );
        int NUM_STEPS_PER_UPDATE = 1;
        for( int i = 0; i < NUM_STEPS_PER_UPDATE; i++ ) {
            double ei = new State( this ).getTotalEnergy();
            getMode().stepInTime( this, dt / NUM_STEPS_PER_UPDATE );
            double ef = new State( this ).getTotalEnergy();
            double err = Math.abs( ef - ei );
            if( err > 1E-6 ) {
                System.out.println( "err=" + err + ", i=" + i );
            }
        }
        if( !isFreeFallMode() && !isUserControlled() ) {
            facingRight = getVelocity().dot( Vector2D.Double.parseAngleAndMagnitude( 1, getAttachmentPointRotation() ) ) > 0;
        }
        EnergyDebugger.stepFinished( this );
    }

    private UpdateMode getMode() {
        return mode;
    }

    public double getY() {
        return getCenterOfMass().getY();
    }

    public double getX() {
        return getCenterOfMass().getX();
    }

    public Point2D getCenterOfMass() {
        return getTransform().transform( new Point2D.Double( width / 2, height / 2 ), null );
    }

    public AbstractVector2D getVelocity() {
        return velocity;
    }

    public void translate( double dx, double dy ) {
        Body origState = copyState();
        attachmentPoint.x += dx;
        attachmentPoint.y += dy;
        if( origState.getEnergyDifferenceAbs( this ) > 500 ) {
            System.out.println( "origState.getEnergyDifferenceAbs( this ) = " + origState.getEnergyDifferenceAbs( this ) );
        }
    }

    public double getEnergyDifferenceAbs( Body body ) {
        return Math.abs( body.getTotalEnergy() - this.getTotalEnergy() );
    }

    public void setVelocity( AbstractVector2D vector2D ) {
        setVelocity( vector2D.getX(), vector2D.getY() );
    }

    public void setVelocity( double vx, double vy ) {
        Body origState = copyState();
        velocity.setComponents( vx, vy );
        if( origState.getEnergyDifferenceAbs( this ) > 500 ) {
            System.out.println( "origState.getEnergyDifferenceAbs( this ) = " + origState.getEnergyDifferenceAbs( this ) );
        }
    }

    public void setState( AbstractVector2D acceleration, AbstractVector2D velocity, Point2D newPosition ) {
        this.acceleration.setComponents( acceleration.getX(), acceleration.getY() );
        setVelocity( velocity );
        setAttachmentPointPosition( newPosition );
    }

    public void setMass( double value ) {
        Body origState = copyState();
        this.mass = value;
        if( origState.getEnergyDifferenceAbs( this ) > 500 ) {
            System.out.println( "origState.getEnergyDifferenceAbs( this ) = " + origState.getEnergyDifferenceAbs( this ) );
        }
    }

    public void setAttachmentPointPosition( double x, double y ) {
        Body origState = copyState();
        attachmentPoint.setLocation( x, y );
        if( origState.getEnergyDifferenceAbs( this ) > 500 ) {
            System.out.println( "origState.getEnergyDifferenceAbs( this ) = " + origState.getEnergyDifferenceAbs( this ) );
        }
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
        return getShape().getBounds2D().getMinY();
    }

    public double getMaxY() {
        return getShape().getBounds2D().getMaxY();
    }

    public double getHeight() {
        return height;
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double( attachmentPoint.getX(), attachmentPoint.getY() );
    }

    public AbstractVector2D getAcceleration() {
        return acceleration;
    }

    public double getMass() {
        return mass;
    }

    public void setSplineMode( EnergyConservationModel model, AbstractSpline spline ) {
        boolean same = false;
        if( mode instanceof FreeSplineMode ) {
            FreeSplineMode sm = (FreeSplineMode)mode;
            if( sm.getSpline() == spline ) {
                same = true;
            }
        }
        if( !same ) {
//            setMode( new FreeSplineMode( model, spline ) );
            setMode( new FreeSplineMode2( model, spline ) );
        }
    }

    private void setMode( UpdateMode mode ) {
        this.mode = mode;
        mode.init( this );
    }

    public void setFreeFallRotationalVelocity( double dA ) {
        freeFall.setRotationalVelocity( dA );
    }

    public void setFreeFallMode() {
        setMode( freeFall );
    }

    void fixAngle() {
        while( attachmentPointRotation < 0 ) {
            attachmentPointRotation += Math.PI * 2;
        }
        while( attachmentPointRotation > Math.PI * 2 ) {
            attachmentPointRotation -= Math.PI * 2;
        }
    }

    public void setAttachmentPointRotation( double attachmentPointRotation ) {
        this.attachmentPointRotation = attachmentPointRotation;
        debugAngles();
    }

    public double getSpeed() {
        return getVelocity().getMagnitude();
    }

    public void setAttachmentPointPosition( Point2D point2D ) {
        setAttachmentPointPosition( point2D.getX(), point2D.getY() );
    }

    public AbstractVector2D getPositionVector() {
        return new ImmutableVector2D.Double( getPosition() );
    }

    public double getKineticEnergy() {
        return 0.5 * getMass() * getSpeed() * getSpeed();
    }

    public double getAttachmentPointRotation() {
        return attachmentPointRotation;
    }

    public void rotateAboutAttachmentPoint( double dA ) {
        attachmentPointRotation += dA;
        fixAngle();
    }

    public boolean isFreeFallMode() {
        return mode instanceof FreeFall;
    }

    public boolean isSplineMode() {
        return mode instanceof FreeSplineMode;
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

    public Point2D.Double getAttachPoint() {
        return new Point2D.Double( attachmentPoint.getX(), attachmentPoint.getY() );
    }

    public Shape getReducedShape() {
        double dw = width * 0.2;
        double dh = height * 0.2;
        return getTransform().createTransformedShape( new Rectangle2D.Double( dw, dh, width - 2 * dw, height - 2 * dh ) );
    }

    public Shape getShape() {
        return getTransform().createTransformedShape( new Rectangle2D.Double( 0, 0, width, height ) );
    }

    public AffineTransform getTransform() {
        AffineTransform transform = new AffineTransform();
        double dy = getHeight();
        transform.translate( attachmentPoint.x - getWidth() / 2, attachmentPoint.y - dy );
        transform.rotate( attachmentPointRotation, getWidth() / 2, dy );
        transform.rotate( cmRotation, getWidth() / 2, getHeight() / 2 );
        return transform;
    }

    public void resetMode() {
        freeFall.reset();
    }

    public double getWidth() {
        return width;
    }

    public void setCMRotation( double angle ) {
        this.cmRotation = angle;
        debugAngles();
    }

    private void debugAngles() {
        if( cmRotation != 0 && attachmentPointRotation != 0 ) {
            throw new RuntimeException( "angles inconsistent" );
        }
    }

    public double getCMRotation() {
        return cmRotation;
    }

    boolean freefall = true;

    public void convertToFreefall() {
        if( !freefall ) {
            this.freefall = true;
            if( cmRotation != 0 ) {
                System.out.println( "cmRotation = " + cmRotation );
                cmRotation = 0;
            }
            Point2D center = getCenterOfMass();
            double attachmentPointRotation = getAttachmentPointRotation();
            setAttachmentPointRotation( 0 );
            setCMRotation( attachmentPointRotation );
            Point2D tempCenter = getCenterOfMass();
            translate( -( tempCenter.getX() - center.getX() ), -( tempCenter.getY() - center.getY() ) );
            Point2D newCenter = getCenterOfMass();
            if( newCenter.distance( center ) > 1E-6 ) {
                System.out.println( "newCenter.distance( center ) = " + newCenter.distance( center ) );
            }
        }
    }

    public void convertToSpline() {
        if( freefall ) {
            if( attachmentPointRotation != 0 ) {
                System.out.println( "attachmentPointRotation = " + attachmentPointRotation );
                attachmentPointRotation = 0;
            }
            freefall = false;
            Point2D center = getCenterOfMass();
            double origCMRotation = getCMRotation();
            setCMRotation( 0 );
            setAttachmentPointRotation( origCMRotation );
            Point2D tempCenter = getCenterOfMass();
            translate( -( tempCenter.getX() - center.getX() ), -( tempCenter.getY() - center.getY() ) );
            Point2D newCenter = getCenterOfMass();
            if( newCenter.distance( center ) > 1E-6 ) {
                System.out.println( "newCenter.distance( center ) = " + newCenter.distance( center ) );
            }
        }
    }

    public boolean isOnSpline( SplineSurface splineSurface ) {
        if( mode instanceof FreeSplineMode ) {
            FreeSplineMode sm = (FreeSplineMode)mode;
            return splineSurface.contains( sm.getSpline() );
        }
        return false;
    }

    public void notifyObservers() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.doRepaint();
        }
    }

    public double getMechanicalEnergy() {
        return getKineticEnergy() + potentialEnergyMetric.getPotentialEnergy( this );
    }

    public double getTotalEnergy() {
        return getMechanicalEnergy() + getThermalEnergy();
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }

    public double getGravity() {
        return potentialEnergyMetric.getGravity();
    }

    public void clearHeat() {
        thermalEnergy = 0.0;
    }

    public void addThermalEnergy( double dThermalEnergy ) {
        thermalEnergy += dThermalEnergy;
    }

    public double getPotentialEnergy() {
        return potentialEnergyMetric.getPotentialEnergy( this );
    }

    public AbstractVector2D getGravityForce() {
        return new ImmutableVector2D.Double( 0, getGravity() * getMass() );
    }

    AbstractSpline lastFallSpline;
    long lastFallTime;

    public void setLastFallTime( AbstractSpline spline, long time ) {
        this.lastFallSpline = spline;
        this.lastFallTime = time;
    }


    public AbstractSpline getLastFallSpline() {
        return lastFallSpline;
    }

    public long getLastFallTime() {
        return lastFallTime;
    }


    public static interface Listener {
        void thrustChanged();

        void doRepaint();
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
