/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;
import edu.colorado.phet.energyskatepark.SkaterCharacter;
import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.model.physics.Particle;
import edu.colorado.phet.energyskatepark.model.physics.ParticleStage;
import edu.colorado.phet.energyskatepark.util.OptionalItemSerializableList;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:02:49 AM
 */

public class Body implements Serializable {
    private Body restorePoint = null;

    private Particle particle;
    private boolean facingRight;
    private double width;
    private double height;
    private double angularVelocity = 0;
    private int errorCount = 0;
    private double fractionalEnergyError = 0.0;

    private List listeners = new OptionalItemSerializableList();

    public static final List particles = new ArrayList();

    //    public static double DEFAULT_STICKINESS = 0.75;
    public static double DEFAULT_STICKINESS = 0.9;
    public static double staticSticky = DEFAULT_STICKINESS;
    private SkaterCharacter skaterCharacter;

    public Body( double width, double height, ParticleStage particleStage, double gravity, double zeroPointPotentialY ) {
        this.width = width;
        this.height = height;
        particle = new Particle( particleStage );
        particle.setMass( 75.0 );
        particle.getParticle1D().setReflect( false );
        particle.setStickiness( staticSticky );
        setGravityState( gravity, zeroPointPotentialY );
        particles.add( this );
    }

    public void setSpline( EnergySkateParkSpline spline, boolean top, double alpha ) {
        particle.switchToTrack( spline.getParametricFunction2D(), alpha, top );
    }

    public void setFreeFallMode() {
        particle.setFreeFall();
    }

    public void setPosition( double x, double y ) {
        Point2D origPosition = getPosition();
        particle.setPosition( x, y );
        if( !origPosition.equals( getPosition() ) ) {
            notifyPositionAngleChanged();
        }
    }

    public boolean isRestorePointSet() {
        return restorePoint != null;
    }

    public void reset() {
        setFreeFallMode();
        particle.setVelocity( 0, 0 );
        setThermalEnergy( 0.0 );
        if( !isRestorePointSet() ) {
            setAngularVelocity( 0.0 );
            setVelocity( 0, 0 );
            setPosition( 3 + 1.5, 6 );
            setAngle( Particle.DEFAULT_ANGLE );
        }
        else {
            setPosition( restorePoint.getPosition() );
            setAngularVelocity( restorePoint.getAngularVelocity() );
            setVelocity( restorePoint.getVelocity() );
            setAngle( restorePoint.particle.getAngle() );
        }
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

    public void stepInTime( double dt ) {
        double origAngle = getAngle();
        Point2D origLocation = getPosition();
        double origEnergy = getTotalEnergy();
        particle.stepInTime( dt );

        updateStateFromParticle();
        if( getY() < -0.1 && isFreeFallMode() && Math.abs( getGravity() ) > 0 ) {
            setPosition( getX(), 0.1 );
        }
        if( origAngle != getAngle() || !origLocation.equals( getPosition() ) ) {
            notifyPositionAngleChanged();
        }
        double err = Math.abs( origEnergy - getTotalEnergy() );
        if( err > 1E-5 && getThrust().getMagnitude() == 0 && !isUserControlled() && !isSplineUserControlled() ) {
            System.out.println( "err = " + err );
            errorCount++;
            fractionalEnergyError += err / Math.abs( origEnergy );
        }

    }

    private boolean isSplineUserControlled() {
        return particle.isSplineUserControlled();
    }

    public int getErrorCount() {
        return errorCount;
    }

    public double getFractionalEnergyError() {
        return fractionalEnergyError;
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

    public SerializablePoint2D getCenterOfMass() {
        return particle.getPosition();
    }

    public AbstractVector2D getVelocity() {
        return particle.getVelocity();
    }

    public void translate( double dx, double dy ) {
        particle.translate( dx, dy );
        if( dx != 0 || dy != 0 ) {
            notifyPositionAngleChanged();
        }
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
        if( !userControlled ) {
            try {
                restorePoint = (Body)PersistenceUtil.copy( this );
            }
            catch( PersistenceUtil.CopyFailedException e ) {
                e.printStackTrace();
            }
        }
        notifyEnergyChanged();
    }

    public Body getRestorePoint() {
        return restorePoint;//todo: should this be a deep copy? 
    }

    public double getHeight() {
        return height;
    }

    public SerializablePoint2D getPosition() {
        return particle.getPosition();
    }

    public double getMass() {
        return particle.getMass();
    }

    public double getSpeed() {
        return getVelocity().getMagnitude();
    }

    public void setPosition( SerializablePoint2D point2D ) {
        setPosition( point2D.getX(), point2D.getY() );
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

    public double getBounciness() {
        return particle.getElasticity();
    }

    public void setBounciness( double coefficientOfRestitution ) {
        particle.setElasticity( coefficientOfRestitution );
    }

    public Shape getFeetShape() {
        double feetFraction = 0.6;
        return getTransform().createTransformedShape( new Rectangle2D.Double( 0, height * ( 1 - feetFraction ), width, height * feetFraction ) );
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

    public TraversalState getBestTraversalState( TraversalState origState ) {
        AbstractVector2D normal = new Vector2D.Double( origState.getParametricFunction2D().getUnitNormalVector( origState.getAlpha() ) ).getScaledInstance( origState.isTop() ? 1.0 : -1.0 );
        SerializablePoint2D location = origState.getParametricFunction2D().evaluate( origState.getAlpha() );
        return particle.getBestTraversalState( location, normal );
    }

    public TraversalState getTraversalState() {
        return particle.getTraversalState();
    }

    public TraversalState getTrackMatch( double dx, double dy ) {
        return particle.getTrackMatch( dx, dy );
    }

    public void setAngle( double angle ) {
        double origAngle = getAngle();
        particle.setAngle( angle );
        if( origAngle != angle ) {
            notifyPositionAngleChanged();
        }
    }

    private void notifyPositionAngleChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.positionAngleChanged();
        }
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

    public void clearEnergyError() {
        errorCount = 0;
        fractionalEnergyError = 0;
    }

    public void setStickiness( double sticky ) {
        particle.setStickiness( sticky );
    }

    public double getStickiness() {
        return particle.getStickiness();
    }

    public ParticleStage getParticleStage() {
        return particle.getParticleStage();
    }

    public double getZeroPointPotentialY() {
        return particle.getZeroPointPotentialY();
    }

    public void setRestorePoint( Body restorePoint ) {
        this.restorePoint = restorePoint;
    }

    public boolean isKeepEnergyOnLanding() {
        return !particle.isConvertNormalVelocityToThermalOnLanding();
    }

    public void setKeepEnergyOnLanding( boolean selected ) {
        particle.setConvertNormalVelocityToThermalOnLanding( !selected );
    }

    public void setSkaterCharacter( SkaterCharacter skaterCharacter ) {
        setDimension( skaterCharacter.getModelWidth(), skaterCharacter.getModelHeight() );
        setMass( skaterCharacter.getMass() );
        this.skaterCharacter = skaterCharacter;
        notifySkaterCharacterChanged();
    }

    private void notifySkaterCharacterChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.skaterCharacterChanged();
        }
    }

    public SkaterCharacter getSkaterCharacter() {
        return skaterCharacter;
    }

    public static interface Listener {
        void thrustChanged();

        void energyChanged();

        void dimensionChanged();

        void positionAngleChanged();

        void skaterCharacterChanged();
    }

    public static class ListenerAdapter implements Listener {
        public void thrustChanged() {
        }

        public void energyChanged() {
        }

        public void dimensionChanged() {
        }

        public void positionAngleChanged() {
        }

        public void skaterCharacterChanged() {
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
