/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.model.physics.Particle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private Particle particle;

    private boolean facingRight;

    private double coefficientOfRestitution = 1.0;

    private double width;
    private double height;
    private double angularVelocity = 0;

    private ArrayList listeners = new ArrayList();
    private EnergySkateParkModel energySkateParkModel;
    private static ArrayList particles = new ArrayList();
    private static double staticSticky = 0.75;

//    public Body( EnergySkateParkModel model ) {
//        this( 1.3, 1.8, model );
//    }

    public Body( double width, double height, EnergySkateParkModel energySkateParkModel ) {
        this.energySkateParkModel = energySkateParkModel;
        this.width = width;
        this.height = height;
        particle = new Particle( energySkateParkModel.getParticleStage() );
        particle.setMass( 75.0 );
        particle.getParticle1D().setReflect( false );
        particle.setStickiness( staticSticky );
        setGravityState( energySkateParkModel.getGravity(), energySkateParkModel.getZeroPointPotentialY() );
        particles.add( this );
    }

    static {
        JFrame controls = new JFrame();
        final ModelSlider stickiness = new ModelSlider( "Stickiness", "", 0, 5, staticSticky );
        stickiness.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                staticSticky = stickiness.getValue();
                for( int i = 0; i < particles.size(); i++ ) {
                    Body body = (Body)particles.get( i );
                    body.particle.setStickiness( staticSticky );
                }
            }
        } );

        controls.setContentPane( stickiness );
        controls.pack();
        controls.setVisible( true );
    }

    public void setSpline( EnergySkateParkSpline spline, boolean top, double alpha ) {
//        particle.getParticle1D().setCubicSpline2D( spline.getParametricFunction2D(), top, alpha );//todo: correct side
        particle.switchToTrack( spline.getParametricFunction2D(), alpha, top );
    }

    public void setFreeFallMode() {
        particle.setFreeFall();
    }

    public void setPosition( double x, double y ) {
        particle.setPosition( x, y );
    }

    public void reset() {
        setFreeFallMode();
        setPosition( new Point2D.Double( 4, 7.25 ) );
        setAngularVelocity( 0.0 );
        setVelocity( 0, 0 );
        setPosition( 3, 6 );
        particle.setVelocity( 0, 0 );
        particle.resetAngle();
        setThermalEnergy( 0.0 );
//        particle.setGravity( -9.8 );
    }

    private void setThermalEnergy( double thermalEnergy ) {
        this.particle.setThermalEnergy( thermalEnergy );
        notifyEnergyChanged();
    }

    private void notifyEnergyChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.energyChanged();
        }
    }

    public void setGravityState( double gravity, double zeroPointPotentialY ) {
        particle.setGravity( gravity );
        particle.setZeroPointPotentialY( zeroPointPotentialY );
    }

    public void setState( Body body ) {
        //todo: set state for particle
        this.angularVelocity = body.angularVelocity;
        this.facingRight = body.facingRight;
        this.coefficientOfRestitution = body.coefficientOfRestitution;
    }

    public Body copyState() {
        //todo: copy state for particle
        Body copy = new Body( width, height, energySkateParkModel );
        copy.angularVelocity = this.angularVelocity;
        copy.facingRight = facingRight;
        copy.coefficientOfRestitution = coefficientOfRestitution;
        return copy;
    }

    public void stepInTime( double dt ) {
        Body orig = copyState();
        particle.stepInTime( dt );

        updateStateFromParticle();
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.stepFinished();
        }
    }

    private void updateStateFromParticle() {
        if( getSpeed() > 0.01 ) {
            if( !isFreeFallMode() && !isUserControlled() ) {
                facingRight = getVelocity().dot( Vector2D.Double.parseAngleAndMagnitude( 1, getAngle() ) ) > 0;
            }
        }

    }

    public double getY() {
        return getCenterOfMass().getY();
    }

    public double getX() {
        return getCenterOfMass().getX();
    }

    public Point2D getCenterOfMass() {
        return particle.getPosition();
    }

    public AbstractVector2D getVelocity() {
        return particle.getVelocity();
    }

    public void translate( double dx, double dy ) {
        particle.translate( dx, dy );
    }

    public double getEnergyDifferenceAbs( Body body ) {
        return Math.abs( body.getTotalEnergy() - this.getTotalEnergy() );
    }

    public void setVelocity( AbstractVector2D vector2D ) {
        setVelocity( vector2D.getX(), vector2D.getY() );
    }

    public void setVelocity( double vx, double vy ) {
        particle.setVelocity( vx, vy );
        updateStateFromParticle();
        notifyEnergyChanged();
    }

    public void setMass( double value ) {
        particle.setMass( value );
        notifyEnergyChanged();
    }

    public boolean isUserControlled() {
        return particle.isUserControlled();
    }

    public void setUserControlled( boolean userControlled ) {
        particle.setUserControlled( userControlled );
        notifyEnergyChanged();
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

    public Point2D getPosition() {
        return particle.getPosition();
    }

    public double getMass() {
        return particle.getMass();
    }

    public void setFreeFallRotationalVelocity( double dA ) {
        setAngularVelocity( dA );
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
        return particle.getAngle();
    }

    public boolean isFreeFallMode() {
        return particle.isFreeFall();
    }

    public boolean isSplineMode() {
        return false;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setThrust( double xThrust, double yThrust ) {
        particle.setThrust( xThrust, yThrust );
        notifyEnergyChanged();
        notifyThrustChanged();
    }

    public AbstractVector2D getThrust() {
        return particle.getThrust();
    }

    public void splineRemoved( EnergySkateParkSpline spline ) {
        if( particle.getSpline() == spline.getParametricFunction2D() ) {
            particle.setFreeFall();
        }
    }

    public double getFrictionCoefficient() {
        return particle.getFrictionCoefficient();
    }

    public void setFrictionCoefficient( double value ) {
        particle.setFrictionCoefficient( value );
    }

    public double getCoefficientOfRestitution() {
        return coefficientOfRestitution;
    }

    public void setCoefficientOfRestitution( double coefficientOfRestitution ) {
        this.coefficientOfRestitution = coefficientOfRestitution;
    }

    public Shape getFeetShape() {
        double feetFraction = 0.6;
        return getTransform().createTransformedShape( new Rectangle2D.Double( 0, height * ( 1 - feetFraction ), width, height * feetFraction ) );
    }

    public Shape getShape() {
        return getTransform().createTransformedShape( new Rectangle2D.Double( 0, 0, width, height ) );
    }

    public AffineTransform getTransform() {
        AffineTransform transform = new AffineTransform();
        double dy = getHeight();
        transform.translate( getPosition().getX() - getWidth() / 2, getPosition().getY() - dy );
        transform.rotate( getAngle(), getWidth() / 2, dy );
        transform.rotate( Math.PI, getWidth() / 2, getHeight() / 2 );
        return transform;
    }

    public double getWidth() {
        return width;
    }

    public boolean isOnSpline( EnergySkateParkSpline splineSurface ) {
        return particle.isOnSpline( splineSurface.getParametricFunction2D() );
    }

    public double getMechanicalEnergy() {
        return getKineticEnergy() + getPotentialEnergy();
    }

    public double getTotalEnergy() {
        return getMechanicalEnergy() + getThermalEnergy();
    }

    public double getThermalEnergy() {
        return particle.getThermalEnergy();
    }

    public double getGravity() {
        return particle.getGravity();
    }

    public void clearHeat() {
        particle.resetThermalEnergy();
    }

    public double getPotentialEnergy() {
        return particle.getPotentialEnergy();
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity( double angularVelocity ) {
        this.angularVelocity = angularVelocity;
    }

    public ParametricFunction2D getSpline() {
        return particle.getSpline();
    }

    /*If particle is not on spline, results are indeterminate.*/
    public boolean isTop() {
        return particle.getParticle1D().isSplineTop();
    }

    public TraversalState getBestTraversalState( TraversalState origState ) {
        AbstractVector2D normal = new Vector2D.Double( origState.getParametricFunction2D().getUnitNormalVector( origState.getAlpha() ) ).getScaledInstance( origState.isTop() ? 1.0 : -1.0 );
        Point2D location = origState.getParametricFunction2D().evaluate( origState.getAlpha() );
        return particle.getBestTraversalState( location, normal );
    }

    public TraversalState getTraversalState() {
        return particle.getTraversalState();
    }

    public TraversalState getTrackMatch( double dx, double dy ) {
        return particle.getTrackMatch( dx, dy );
    }

    public void setAngle( double angle ) {
        particle.setAngle( angle );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void setDimension( double modelWidth, double modelHeight ) {
        this.width = modelWidth;
        this.height = modelHeight;
        notifyDimensionChanged();
    }

    private void notifyDimensionChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.dimensionChanged();
        }
    }

    public static interface Listener {
        void thrustChanged();

        void doRepaint();

        void stepFinished();

        void energyChanged();

        void dimensionChanged();
    }

    public static class ListenerAdapter implements Listener {
        public void thrustChanged() {

        }

        public void doRepaint() {

        }

        public void stepFinished() {
        }

        public void energyChanged() {
        }

        public void dimensionChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    protected void notifyThrustChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.thrustChanged();
        }
    }

}
