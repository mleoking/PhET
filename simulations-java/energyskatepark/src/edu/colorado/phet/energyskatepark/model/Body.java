/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
import edu.colorado.phet.energyskatepark.test.phys1d.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.test.phys1d.Particle;
import edu.colorado.phet.energyskatepark.test.phys1d.ParticleStage;
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

    private Particle particle;
    private ParticleStage particleStage;

    private Point2D.Double attachmentPoint = new Point2D.Double();
    private Vector2D velocity = new Vector2D.Double();
    private Vector2D.Double acceleration = new Vector2D.Double();
    private double mass = 75.0;
    private double attachmentPointRotation = 0;

    private boolean facingRight;

    private double xThrust = 0.0;
    private double yThrust = 0.0;

    private double frictionCoefficient = 0.0;
    private double coefficientOfRestitution = 1.0;

    private double width;
    private double height;
    private PotentialEnergyMetric potentialEnergyMetric;
    private double cmRotation = 0;
    private double thermalEnergy = 0;
    private double angularVelocity = 0;
    private double storedTotalEnergy = 0.0;
    private boolean freefall = true;

    private AbstractSpline lastFallSpline;
    private long lastFallTime;

    private ArrayList listeners = new ArrayList();
    private EnergySkateParkModel energySkateParkModel;

    private boolean debugAnglesEnabled = false;
    private static final double POTENTIAL_ENERGY_EQUALITY_EPS = 1E-6;

    public Body( EnergySkateParkModel model ) {
        this( Body.createDefaultBodyRect().getWidth(), Body.createDefaultBodyRect().getHeight(), model.getPotentialEnergyMetric(), model );
    }

    public Body( double width, double height, PotentialEnergyMetric potentialEnergyMetric, EnergySkateParkModel energySkateParkModel ) {
        this.energySkateParkModel = energySkateParkModel;
        this.width = width;
        this.height = height;
        this.potentialEnergyMetric = potentialEnergyMetric;
        cmRotation = Math.PI;
        storedTotalEnergy = getTotalEnergy();

        particleStage = new EnergySkateParkSplineListAdapter( energySkateParkModel );
        particle = new Particle( particleStage );
    }

    public void setPotentialEnergyMetric( PotentialEnergyMetric potentialEnergyMetric ) {
        this.potentialEnergyMetric = potentialEnergyMetric;
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
        setCMRotation( getDefaultBodyAngle() );
        setAttachmentPointPosition( getDefaultBodyPosition() );
        resetMode();
        setVelocity( 0, 0 );
        setThermalEnergy( 0.0 );
        setPosition( 3, 6 );
        particle.setVelocity( 0, 0 );
        particle.setGravity( -9.8 );
    }


    private Point2D getDefaultBodyPosition() {
        return defaultBodyPosition;
    }

    private Point2D.Double defaultBodyPosition = new Point2D.Double( 4, 7.25 );

    private double getDefaultBodyAngle() {
        return Math.PI;
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
        this.mass = body.mass;
        this.attachmentPointRotation = body.attachmentPointRotation;
        this.cmRotation = body.cmRotation;
//        this.mode = body.mode.copy();
        this.thermalEnergy = body.thermalEnergy;
        this.facingRight = body.facingRight;
        this.xThrust = body.xThrust;
        this.yThrust = body.yThrust;
        this.coefficientOfRestitution = body.coefficientOfRestitution;
        this.potentialEnergyMetric = body.potentialEnergyMetric.copy();
        this.storedTotalEnergy = body.storedTotalEnergy;
    }

    public Body copyState() {
        Body copy = new Body( width, height, potentialEnergyMetric, energySkateParkModel );
        copy.angularVelocity = this.angularVelocity;
        copy.setAttachmentPoint( attachmentPoint.x, attachmentPoint.y );
        copy.velocity.setComponents( velocity.getX(), velocity.getY() );
        copy.acceleration.setComponents( acceleration.getX(), acceleration.getY() );
        copy.mass = mass;
        copy.attachmentPointRotation = attachmentPointRotation;
        copy.cmRotation = cmRotation;
//        copy.mode = this.mode.copy();
        copy.thermalEnergy = this.thermalEnergy;
        copy.facingRight = facingRight;
        copy.xThrust = xThrust;
        copy.yThrust = yThrust;
        copy.coefficientOfRestitution = coefficientOfRestitution;
        copy.potentialEnergyMetric = potentialEnergyMetric.copy();
        copy.storedTotalEnergy = this.storedTotalEnergy;
        return copy;
    }

    public static PDimension createDefaultBodyRect() {
        return new PDimension( 1.3, 1.8 );
    }

    public double getStoredTotalEnergy() {
        return storedTotalEnergy;
    }

    boolean recurse = false;
    boolean debugging = false;

    public void stepInTime( double dt ) {
//        System.out.println( "getGravity() = " + getGravity() );
        Body orig = copyState();
//        System.out.println( "getGravity() = " + getGravity() );

        particle.stepInTime( dt );

//        StateRecord collisionList = createCollisionState();
//        stateRecordHistory.add( collisionList );
//        if( stateRecordHistory.size() > 100 ) {
//            stateRecordHistory.remove( 0 );
//        }
        if( isUserControlled() ) {
            this.storedTotalEnergy = getTotalEnergy();
        }
        EnergyDebugger.stepStarted( this, dt );
        int NUM_STEPS_PER_UPDATE = 1;
        for( int i = 0; i < NUM_STEPS_PER_UPDATE; i++ ) {
            double ei = getTotalEnergy();
//            getMode().stepInTime( this, dt / NUM_STEPS_PER_UPDATE );
            double ef = getTotalEnergy();
            double err = Math.abs( ef - ei );
            if( err > 1E-6 && !isUserControlled() ) {
//                System.out.println( "err=" + err + ", i=" + i + ", mode=" + getMode() );
                if( !recurse && debugging ) {
                    setState( orig );
                    recurse = true;
                    stepInTime( dt );
                    recurse = false;
                }
            }
        }
        if( getSpeed() > 0.01 ) {
            if( !isFreeFallMode() && !isUserControlled() ) {
                facingRight = getVelocity().dot( Vector2D.Double.parseAngleAndMagnitude( 1, getAttachmentPointRotation() ) ) > 0;
            }
        }

        if( !MathUtil.isApproxEqual( getPotentialEnergy(), orig.getPotentialEnergy(), POTENTIAL_ENERGY_EQUALITY_EPS ) ) {
            notifyPotentialEnergyChanged();
        }

//        System.out.println( "facingRight = " + facingRight + ", speed=" + getSpeed() );

        setAttachmentPointPosition( particle.getX(), particle.getY() );
        setVelocity( particle.getVelocity() );

        EnergyDebugger.stepFinished( this );
    }

//    public StateRecord createCollisionState() {
//        StateRecord stateRecord = new StateRecord( copyState() );
//        ArrayList splines = energySkateParkModel.getAllSplines();
//        for( int i = 0; i < splines.size(); i++ ) {
//            AbstractSpline spline = (AbstractSpline)splines.get( i );
//            if( new SplineInteraction( energySkateParkModel ).isColliding( spline, this ) ) {
//                stateRecord.addCollisionSpline( spline, getTraversalState( spline ) );
//            }
//        }
//        return stateRecord;
//    }

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
        return getTransform().transform( new Point2D.Double( width / 2, height / 2 ), null );
    }

    public AbstractVector2D getVelocity() {
        return velocity;
    }

    private void setAttachmentPoint( double x, double y ) {
        attachmentPoint.x = x;
        attachmentPoint.y = y;

        notifyPotentialEnergyChanged();
    }

    public void translate( double dx, double dy ) {
        setAttachmentPoint( attachmentPoint.x + dx,
                            attachmentPoint.y + dy );
    }

    public double getEnergyDifferenceAbs( Body body ) {
        return Math.abs( body.getTotalEnergy() - this.getTotalEnergy() );
    }

    public void setVelocity( AbstractVector2D vector2D ) {
        setVelocity( vector2D.getX(), vector2D.getY() );
    }

    public void setVelocity( double vx, double vy ) {
        velocity.setComponents( vx, vy );
    }

    public void setState( AbstractVector2D acceleration, AbstractVector2D velocity, Point2D newPosition ) {
        this.acceleration.setComponents( acceleration.getX(), acceleration.getY() );
        setVelocity( velocity );
        setAttachmentPointPosition( newPosition );
    }

    public void setMass( double value ) {
        this.mass = value;
    }

    public void setAttachmentPointPosition( double x, double y ) {
        setAttachmentPoint( x, y );
    }

    public boolean isUserControlled() {
        return particle.isUserMode();
    }

    public void setUserControlled( boolean userControlled ) {
        if( userControlled ) {
            particle.setUserUpdateStrategy();
        }
        else {
            particle.setFreeFall();
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

    public void setFreeFallRotationalVelocity( double dA ) {
        setAngularVelocity( dA );
    }

    private void clampAngle() {
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
        clampAngle();
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
        if (particle.getSpline()==spline.getParametricFunction2D()){
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
        transform.rotate( cmRotation, getWidth() / 2, getHeight() / 2 );
        return transform;
    }

    public void resetMode() {
        setAngularVelocity( 0.0 );
    }

    public double getWidth() {
        return width;
    }

    public void setCMRotation( double angle ) {
        this.cmRotation = angle;
        debugAngles();
    }

    private void debugAngles() {
        if( debugAnglesEnabled ) {
            if( cmRotation != 0 && attachmentPointRotation != 0 ) {
                throw new RuntimeException( "angles inconsistent" );
            }
        }
    }

    public double getCMRotation() {
        return cmRotation;
    }

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

    public boolean isFreeFallFrame() {
        return freefall;
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

    public boolean isOnSpline( EnergySkateParkSpline splineSurface ) {
        return particle.isOnSpline( splineSurface.getParametricFunction2D() );
    }

    public void notifyDoRepaint() {
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

    public void setThermalEnergy( double thermalEnergy ) {
        this.thermalEnergy = thermalEnergy;
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

    public void convertToFreefall( boolean freeFrame ) {
        this.freefall = !freeFrame;//to ensure the code is called...? awkward!
        if( freeFrame ) {
            convertToFreefall();
        }
        else {
            convertToSpline();
        }
    }

    public static interface Listener {
        void thrustChanged();

        void doRepaint();

        void potentialEnergyChanged();
    }

    public static abstract class ListenerAdapter implements Listener {
        public void thrustChanged() {

        }

        public void doRepaint() {

        }

        public void potentialEnergyChanged() {

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

    protected void notifyPotentialEnergyChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.potentialEnergyChanged();
        }
    }
}
