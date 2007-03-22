/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
import edu.colorado.phet.energyskatepark.test.phys1d.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.test.phys1d.Particle;
import edu.colorado.phet.energyskatepark.test.phys1d.ParticleStage;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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
    private ParticleStage particleStage;

    private Point2D.Double attachmentPoint = new Point2D.Double();
    private Vector2D velocity = new Vector2D.Double();
    private Vector2D.Double acceleration = new Vector2D.Double();
    private double attachmentPointRotation = 0;

    private boolean facingRight;

    private double xThrust = 0.0;
    private double yThrust = 0.0;

    private double frictionCoefficient = 0.0;
    private double coefficientOfRestitution = 1.0;

    private double width;
    private double height;
    private double angularVelocity = 0;
    private boolean freefall = true;

    private AbstractSpline lastFallSpline;
    private long lastFallTime;

    private ArrayList listeners = new ArrayList();
    private EnergySkateParkModel energySkateParkModel;

    private boolean debugAnglesEnabled = false;

    public Body( EnergySkateParkModel model ) {
        this( Body.createDefaultBodyRect().getWidth(), Body.createDefaultBodyRect().getHeight(),  model );
    }

    public Body( double width, double height, EnergySkateParkModel energySkateParkModel ) {
        this.energySkateParkModel = energySkateParkModel;
        this.width = width;
        this.height = height;
        particleStage = new EnergySkateParkSplineListAdapter( energySkateParkModel );
        particle = new Particle( particleStage );
        particle.setMass( 75.0 );
        particle.getParticle1D().setReflect( false );
        setGravityState( energySkateParkModel.getGravity(), energySkateParkModel.getZeroPointPotentialY() );
    }

    public void showControls() {
        JFrame controls=new JFrame( );
        final ModelSlider stickiness=new ModelSlider( "Stickiness","",0,5,particle.getStickiness());
        stickiness.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                particle.setStickiness( stickiness.getValue() );
            }
        } );

        controls.setContentPane( stickiness );
        controls.pack();
        controls.setVisible( true );
    }

    public void stayInSplineModeNewSpline( EnergySkateParkSpline bestMatch ) {
        particle.getParticle1D().setCubicSpline2D( bestMatch.getParametricFunction2D(), true );//todo: correct side
    }

    public void setFreeFallMode() {
        particle.setFreeFall();
    }

    public void setPosition( double x, double y ) {
        particle.setPosition( x, y );
    }

    public void reset() {
        setFreeFallMode();
        setAttachmentPointRotation( 0.0 );
//        setCMRotation( getDefaultBodyAngle() );
        setAttachmentPointPosition( getDefaultBodyPosition() );
        resetMode();
        setVelocity( 0, 0 );
        setPosition( 3, 6 );
        particle.setVelocity( 0, 0 );
        particle.setGravity( -9.8 );
    }


    private Point2D getDefaultBodyPosition() {
        return defaultBodyPosition;
    }

    private Point2D.Double defaultBodyPosition = new Point2D.Double( 4, 7.25 );

    public Particle getParticle() {
        return particle;
    }

    public void setGravityState( double gravity, double zeroPointPotentialY ) {
        particle.setGravity( gravity );
        particle.setZeroPointPotentialY(zeroPointPotentialY);
    }

    public static class StateRecord {
        private ArrayList states = new ArrayList();
        private Body body;

        public StateRecord( Body body ) {
            this.body = body;
        }

        public void addCollisionSpline( AbstractSpline spline, TraversalState top ) {
            states.add( top );
        }

        public boolean containsSpline( AbstractSpline spline ) {
            for( int i = 0; i < states.size(); i++ ) {
                TraversalState traversalState = (TraversalState)states.get( i );
                if( traversalState.getSpline() == spline ) {
                    return true;
                }
            }
            return false;
        }

        public Body getBody() {
            return body;
        }

        public AbstractSpline getSpline( int i ) {
            return getTraversalState( i ).getSpline();
        }

        public TraversalState getTraversalState( int i ) {
            return (TraversalState)states.get( i );
        }

        public int getSplineCount() {
            return states.size();
        }

        public TraversalState getTraversalState( AbstractSpline spline ) {
            for( int i = 0; i < states.size(); i++ ) {
                TraversalState traversalState = getTraversalState( i );
                if( traversalState.getSpline() == spline ) {
                    return traversalState;
                }
            }
            return null;
        }
    }

    public void setState( Body body ) {
        this.angularVelocity = body.angularVelocity;
        setAttachmentPoint( body.attachmentPoint.x, body.attachmentPoint.y );
        this.velocity.setComponents( body.velocity.getX(), body.velocity.getY() );
        this.acceleration.setComponents( body.acceleration.getX(), body.velocity.getY() );
        this.attachmentPointRotation = body.attachmentPointRotation;
        this.facingRight = body.facingRight;
        this.xThrust = body.xThrust;
        this.yThrust = body.yThrust;
        this.coefficientOfRestitution = body.coefficientOfRestitution;
    }

    public Body copyState() {
        Body copy = new Body( width, height, energySkateParkModel );
        copy.angularVelocity = this.angularVelocity;
        copy.setAttachmentPoint( attachmentPoint.x, attachmentPoint.y );
        copy.velocity.setComponents( velocity.getX(), velocity.getY() );
        copy.acceleration.setComponents( acceleration.getX(), acceleration.getY() );
        copy.attachmentPointRotation = attachmentPointRotation;
        copy.facingRight = facingRight;
        copy.xThrust = xThrust;
        copy.yThrust = yThrust;
        copy.coefficientOfRestitution = coefficientOfRestitution;
        return copy;
    }

    public static PDimension createDefaultBodyRect() {
        return new PDimension( 1.3, 1.8 );
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
        setAttachmentPointPosition( particle.getX(), particle.getY() );
        this.velocity = particle.getVelocity();
        if( getSpeed() > 0.01 ) {
            if( !isFreeFallMode() && !isUserControlled() ) {
                facingRight = getVelocity().dot( Vector2D.Double.parseAngleAndMagnitude( 1, getAttachmentPointRotation() ) ) > 0;
            }
        }

        attachmentPointRotation = particle.getAngle() - Math.PI / 2;
    }

    static class TraversalState {
        boolean top;
        double scalarAlongSpline;
        Point2D closestSplineLocation;
        private AbstractSpline spline;

        public TraversalState( boolean top, double scalarAlongSpline, Point2D closestSplineLocation, AbstractSpline spline ) {
            this.top = top;
            this.scalarAlongSpline = scalarAlongSpline;
            this.closestSplineLocation = closestSplineLocation;
            this.spline = spline;
        }

        public boolean isTop() {
            return top;
        }

        public double getScalarAlongSpline() {
            return scalarAlongSpline;
        }

        public Point2D getClosestSplineLocation() {
            return closestSplineLocation;
        }

        public AbstractSpline getSpline() {
            return spline;
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
        return velocity;
    }

    private void setAttachmentPoint( double x, double y ) {
        attachmentPoint.x = x;
        attachmentPoint.y = y;
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
    }

    public void setState( AbstractVector2D acceleration, AbstractVector2D velocity, Point2D newPosition ) {
        this.acceleration.setComponents( acceleration.getX(), acceleration.getY() );
        setVelocity( velocity );
        setAttachmentPointPosition( newPosition );
    }

    public void setMass( double value ) {
        particle.setMass( value );
    }

    public void setAttachmentPointPosition( double x, double y ) {
        setAttachmentPoint( x, y );
    }

    public boolean isUserControlled() {
        return particle.isUserControlled();
    }

    public void setUserControlled( boolean userControlled ) {
        particle.setUserControlled( userControlled );
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
        return particle.getMass();
    }

    public void setFreeFallRotationalVelocity( double dA ) {
        setAngularVelocity( dA );
    }

    public void setAttachmentPointRotation( double attachmentPointRotation ) {
        this.attachmentPointRotation = attachmentPointRotation;
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
        if( this.xThrust != xThrust || this.yThrust != yThrust ) {
            this.xThrust = xThrust;
            this.yThrust = yThrust;
            notifyThrustChanged();
        }
    }

    public AbstractVector2D getThrust() {
        return new ImmutableVector2D.Double( xThrust, yThrust );
    }

    public void splineRemoved( EnergySkateParkSpline spline ) {
        if( particle.getSpline() == spline.getParametricFunction2D() ) {
            particle.setFreeFall();
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

    public Shape getReducedShape( double fractionalSize ) {
        double scale = ( 1.0 - fractionalSize ) / 2.0;
        double dw = width * scale;
        double dh = height * scale;
        return getTransform().createTransformedShape( new Rectangle2D.Double( dw, dh, width - 2 * dw, height - 2 * dh ) );
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
        transform.translate( attachmentPoint.x - getWidth() / 2, attachmentPoint.y - dy );
        transform.rotate( attachmentPointRotation, getWidth() / 2, dy );
        transform.rotate( Math.PI, getWidth() / 2, getHeight() / 2 );
        return transform;
    }

    public void resetMode() {
        setAngularVelocity( 0.0 );
    }

    public double getWidth() {
        return width;
    }

    public boolean isFreeFallFrame() {
        return freefall;
    }

    public boolean isOnSpline( EnergySkateParkSpline splineSurface ) {
        return particle.isOnSpline( splineSurface.getParametricFunction2D() );
    }

    public double getMechanicalEnergy() {
        return getKineticEnergy() + getPotentialEnergy( );
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

    public AbstractVector2D getGravityForce() {
        return new ImmutableVector2D.Double( 0, getGravity() * getMass() );
    }

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

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity( double angularVelocity ) {
        this.angularVelocity = angularVelocity;
    }

    public ParametricFunction2D getSpline() {
        return particle.getSpline();
    }

    public void setAcceleration( double ax, double ay ) {
        acceleration.setComponents( ax, ay );
    }

    public static interface Listener {
        void thrustChanged();

        void doRepaint();

        void stepFinished();
    }

    public static class ListenerAdapter implements Listener {
        public void thrustChanged() {

        }

        public void doRepaint() {

        }

        public void stepFinished() {
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
