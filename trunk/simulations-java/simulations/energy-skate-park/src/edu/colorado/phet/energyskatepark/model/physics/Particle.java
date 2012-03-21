// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.spline.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.model.LinearFloorSpline2D;
import edu.colorado.phet.energyskatepark.model.TraversalState;
import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ModelActions.bounced;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ModelActions.landed;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ParameterKeys.isFloor;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ParameterKeys.trackIndex;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.SharedComponents.skater;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:42:19 AM
 */

public class Particle implements Serializable {
    private final Particle1D particle1D;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double g = 9.8;
    private double mass = 1.0;

    private double elasticity = DEFAULT_ELASTICITY;
    private double stickiness = 0.25;//see neumann

    private UpdateStrategy updateStrategy = new Particle1DUpdate();
    private final ParticleStage particleStage;
    private boolean convertNormalVelocityToThermalOnLanding = true;

    private double angle = DEFAULT_ANGLE;
    private boolean userControlled = false;
    private double thermalEnergy = 0.0;
    private double zeroPointPotentialY;
    private double xThrust = 0;
    private double yThrust = 0;
    private double frictionCoefficient = 0;
    private final boolean verboseDebug = true;

    private final ArrayList listeners = new ArrayList();

    public static final double DEFAULT_ANGLE = 0;
    public static final double DEFAULT_ELASTICITY = 0.6;
    public static boolean reorientOnBounce = true;

    public Particle( ParametricFunction2D parametricFunction2D ) {
        this( new ParticleStage( parametricFunction2D ) );
    }

    public Particle( ParticleStage particleStage ) {
        particle1D = new Particle1D( particleStage.getCubicSpline2DCount() > 0 ? particleStage.getCubicSpline2D( 0 ) : null, true, g );
        this.particleStage = particleStage;
        setUpdateStrategy( particleStage.getCubicSpline2DCount() > 0 ? ( (UpdateStrategy) new Particle1DUpdate() ) : new FreeFall() );
        setMass( 1.0 );//ensures particle1d has synchronized mass
    }

    public ParticleStage getParticleStage() {
        return particleStage;
    }

    public void stepInTime( double dt ) {
        if ( !userControlled ) {
            double origEnergy = getTotalEnergy();
            Class updateClass = updateStrategy.getClass();
            updateStrategy.stepInTime( dt );
            double finalEnergy = getTotalEnergy();
            double dE = finalEnergy - origEnergy;
            if ( Math.abs( dE ) > 1E-6 && getThrust().getMagnitude() == 0.0 ) {
                EnergySkateParkLogging.println( "Particle.stepInTime: de = " + dE + ", strategy=" + updateClass + ", newStrategy=" + updateStrategy.getClass() );
            }
            update();
        }
    }

    public void setGravity( double g ) {
        this.g = g;
        particle1D.setGravity( g );
    }

    public boolean isFreeFall() {
        return updateStrategy instanceof FreeFall;
    }

    public double getElasticity() {
        return elasticity;
    }

    public void setElasticity( double elasticity ) {
        this.elasticity = elasticity;
    }

    public double getStickiness() {
        return stickiness;
    }

    public void setStickiness( double stickiness ) {
        this.stickiness = stickiness;
    }

    public boolean isAboveSpline1D() {
        return particle1D.isSplineTop();
    }

    public Particle1D getParticle1D() {
        return particle1D;
    }

    public boolean isSplineMode() {
        return updateStrategy instanceof Particle1DUpdate;
    }

    public double getAngle() {
        return angle;
    }

    public double getKineticEnergy() {
        return 0.5 * mass * getSpeed() * getSpeed();
    }

    public double getTotalEnergy() {
        return getKineticEnergy() + getPotentialEnergy() + getThermalEnergy();
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }

    public double getHeightAboveZero() {
        return y - zeroPointPotentialY;
    }

    public double getPotentialEnergy() {
        return -mass * g * ( y - zeroPointPotentialY );//todo: should particle1d know about this as well?
    }

    public double getGravity() {
        return g;
    }

    public boolean isOnSpline( ParametricFunction2D parametricFunction2D ) {
        return updateStrategy instanceof Particle1DUpdate && particle1D.getCubicSpline2D() == parametricFunction2D;
    }

    public ParametricFunction2D getSpline() {
        return ( updateStrategy instanceof Particle1DUpdate ) ? particle1D.getCubicSpline2D() : null;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public void translate( double dx, double dy ) {
        setPosition( x + dx, y + dy );
    }

    public void resetThermalEnergy() {
        this.thermalEnergy = 0.0;
        particle1D.setThermalEnergy( 0.0 );
    }

    public void setMass( double mass ) {
        this.mass = mass;
        particle1D.setMass( mass );
    }

    public void setZeroPointPotentialY( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
        particle1D.setZeroPointPotentialY( zeroPointPotentialY );
    }

    public double getZeroPointPotentialY() {
        return zeroPointPotentialY;
    }

    public ImmutableVector2D getThrust() {
        return new ImmutableVector2D( xThrust, yThrust );
    }

    public void setThrust( double xThrust, double yThrust ) {
        this.xThrust = xThrust;
        this.yThrust = yThrust;
        particle1D.setThrust( xThrust, yThrust );
    }

    public TraversalState getTraversalState() {
        return particle1D.getTraversalState();
    }

    public void setFrictionCoefficient( double value ) {
        this.frictionCoefficient = value;
        particle1D.setFrictionCoefficient( value );
    }

    public double getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public TraversalState getTrackMatch( double dx, double dy ) {
        boolean[] above = getOrigAbove();
        SearchState crossPoint = getBestCrossPoint( new SerializablePoint2D( getPosition().getX() + dx, getPosition().getY() + dy ), getOrigAbove(), getPosition() );

        if ( crossPoint == null || crossPoint.getIndex() == -1 ) {
            return null;
        }
        else {

            return new TraversalState( crossPoint.getTrack(), above[crossPoint.getIndex()], crossPoint.getAlpha() );
        }
    }

    public boolean isSplineUserControlled() {
        return particleStage.isSplineUserControlled();
    }

    interface UpdateStrategy extends Serializable {
        void stepInTime( double dt );
    }

    public double getMass() {
        return mass;
    }

    class Particle1DUpdate implements UpdateStrategy, Serializable {
        public void stepInTime( double dt ) {
            ImmutableVector2D sideVector = particle1D.getSideVector();
            boolean outsideCircle = sideVector.dot( particle1D.getCurvatureDirection() ) < 0;

            //compare a to v/r^2 to see if it leaves the track
            double r = Math.abs( particle1D.getRadiusOfCurvature() );
            double centripForce = getMass() * particle1D.getSpeed() * particle1D.getSpeed() / r;
            double netForceRadial = particle1D.getNetForce().dot( particle1D.getCurvatureDirection() );

            boolean leaveTrack = false;
            if ( netForceRadial < centripForce && outsideCircle ) {
                leaveTrack = true;
            }
            if ( netForceRadial > centripForce && !outsideCircle ) {
                leaveTrack = true;
            }
            if ( leaveTrack && !particle1D.isRollerCoasterMode() ) {
                switchToFreeFall();
                Particle.this.stepInTime( dt );
            }
            else {
                particle1D.stepInTime( dt );
                updateStateFrom1D();
                if ( !particle1D.isReflect() && ( particle1D.getAlpha() < 0 || particle1D.getAlpha() > 1.0 ) ) {

                    //Check to see if it can immediately attach to the floor without going through free fall first
                    //Otherwise it causes a glitch in the thermal energy which is problematic in Energy Skate Park Basics
                    if ( isReadyToAttachToFloor() ) {
                        attachToFloor();
                    }
                    else {

                        //Fall off the edge, but not of the world
                        if ( getSpline() != particleStage.getFloorSpline() ) {
                            switchToFreeFall();
                        }
                    }
                }
            }
        }
    }

    //Check to see if it should immediately attach to the floor from another spline.  This is a special case
    //Because the energy values shouldn't be perturbed.
    private void attachToFloor() {
        y = 0;
        ParametricFunction2D floorSpline = particleStage.getFloorSpline();
        if ( floorSpline != null ) {
            double newAlpha = floorSpline.getClosestPoint( new SerializablePoint2D( x, y ) );
            particle1D.setCubicSpline2D( floorSpline, false, newAlpha );
            setUpdateStrategy( new Particle1DUpdate() );
        }
        else {
            throw new RuntimeException( "Floor not found in attachToFloor" );
        }
    }

    private boolean isReadyToAttachToFloor() {
        return y <= 0 && getSpline() != particleStage.getFloorSpline();
    }

    private void updateStateFrom1D() {
        x = particle1D.getX();
        y = particle1D.getY();
        ImmutableVector2D vel = particle1D.getVelocity2D();
        vx = vel.getX();
        vy = vel.getY();
        angle = particle1D.getSideVector().getAngle() - Math.PI / 2;
        thermalEnergy = particle1D.getThermalEnergy();
    }

    public void setAngle( double angle ) {
        this.angle = angle;
        update();
    }

    private void switchToFreeFall() {
        double origEnergy = particle1D.getEnergy();
        setVelocity( particle1D.getVelocity2D() );
        setFreeFall();
        //todo: update location so it's guaranteed on the right side of the spline?
        offsetOnSpline( particle1D.getCubicSpline2D(), particle1D.getAlpha(), particle1D.isSplineTop() );
        particle1D.detach();
        double dE = getTotalEnergy() - origEnergy;
        if ( Math.abs( dE ) > 1E-6 ) {
            EnergySkateParkLogging.println( "Switching to freefall: energy discrepancy: dE=" + dE );
            if ( dE > 0 ) {//gained energy
                //can we reduce velocity to fix?
                testCorrectVelocity( origEnergy );
            }
        }
    }

    private void testCorrectVelocity( double e0 ) {
        double dE = getTotalEnergy() - e0;
        if ( Math.abs( getKineticEnergy() ) > Math.abs( dE ) ) {//amount we could reduce the energy if we deleted all the kinetic energy:
            verboseDebug( "Could fix all energy by changing velocity." );//todo: maybe should only do this if all velocity is not converted
            correctEnergyReduceVelocity( e0 );
            verboseDebug( "changed velocity: dE=" + ( getTotalEnergy() - e0 ) );
            if ( !MathUtil.isApproxEqual( e0, getTotalEnergy(), 1E-8 ) ) {
                new RuntimeException( "Energy error[p0]" ).printStackTrace();
            }
        }
        else {
            EnergySkateParkLogging.println( "can't correct with velocity correction" );
        }
    }

    private void verboseDebug( String s ) {
        if ( verboseDebug ) {
            EnergySkateParkLogging.println( s );
        }
    }

    private void correctEnergyReduceVelocity( double e0 ) {
        double velocity = getVelocity().getMagnitude();
        for ( int i = 0; i < 100; i++ ) {
            double dv = ( getTotalEnergy() - e0 ) / ( mass * velocity );
            velocity -= dv;
            setVelocity( getVelocity().getInstanceOfMagnitude( Math.abs( velocity ) ) );
            if ( MathUtil.isApproxEqual( e0, getTotalEnergy(), 1E-8 ) ) {
                break;
            }
        }
        setVelocity( getVelocity().getInstanceOfMagnitude( Math.abs( velocity ) ) );
    }

    public boolean isAboveSpline( int index ) {
        return isAboveSpline( particleStage.getCubicSpline2D( index ), particleStage.getCubicSpline2D( index ).getClosestPoint( new SerializablePoint2D( x, y ) ), new SerializablePoint2D( x, y ) );
    }

    public boolean isAboveSplineZero() {
        return isAboveSpline( 0 );
    }

    public boolean[] getOrigAbove() {
        boolean[] orig = new boolean[particleStage.getCubicSpline2DCount()];
        for ( int i = 0; i < particleStage.getCubicSpline2DCount(); i++ ) {
            orig[i] = isAboveSpline( i );
        }
        return orig;
    }

    /*
    see http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/
     */
    public static double pointSegmentDistance( SerializablePoint2D pt3, Line2D.Double line ) {
        SerializablePoint2D p1 = new SerializablePoint2D( line.getP1() );
        SerializablePoint2D p2 = new SerializablePoint2D( line.getP2() );
        double u = ( ( pt3.getX() - p1.getX() ) * ( p2.getX() - p1.getX() ) + ( pt3.getY() - p1.getY() ) * ( p2.getY() - p1.getY() ) ) / p1.distanceSq( p2 );
        if ( u < 0 ) {
            return pt3.distance( line.getP1() );
        }
        else if ( u > 1 ) {
            return pt3.distance( line.getP2() );
        }
        else {
            SerializablePoint2D closest = new SerializablePoint2D( p1.getX() + u * ( p2.getX() - p1.getX() ), p1.getY() + u * ( p2.getY() - p1.getY() ) );
            return closest.distance( pt3 );
        }
    }

    /*
     * Find the state that best matches where the skater should be if it were to join the nearest track on the correct side.
     */
    public TraversalState getBestTraversalState( SerializablePoint2D location, ImmutableVector2D normal ) {
        SearchState bestMatch = getBestSearchPoint( location );
        ImmutableVector2D newNormal = bestMatch.getTrack().getUnitNormalVector( bestMatch.getAlpha() );
        boolean top = newNormal.dot( normal ) > 0;
        return new TraversalState( bestMatch.getTrack(), top, bestMatch.getAlpha() );
    }

    //todo: this code is highly similar to pt-line search code, could be consolidated
    private SearchState getBestSearchPoint( SerializablePoint2D origLoc ) {
        SearchState searchState = new SearchState( Double.POSITIVE_INFINITY, null, 0, -1 );
        for ( int i = 0; i < particleStage.getCubicSpline2DCount(); i++ ) {
            ParametricFunction2D cubicSpline = particleStage.getCubicSpline2D( i );
            double alpha = cubicSpline.getClosestPoint( origLoc );
            if ( alpha > 0.0 && alpha < 1.0 ) {
                double dist = cubicSpline.evaluate( alpha ).distance( origLoc );
                if ( dist < searchState.getDistance() ) {
                    searchState.setDistance( dist );
                    searchState.setTrack( cubicSpline );
                    searchState.setAlpha( alpha );
                    searchState.setIndex( i );
                }
            }
        }
        return searchState;
    }

    class SearchState {
        double distance;
        ParametricFunction2D track;
        double alpha;
        int index;

        public SearchState( double dist, ParametricFunction2D track, double alpha, int index ) {
            this.distance = dist;
            this.track = track;
            this.alpha = alpha;
            this.index = index;
        }

        public double getDistance() {
            return distance;
        }

        public ParametricFunction2D getTrack() {
            return track;
        }

        public double getAlpha() {
            return alpha;
        }

        public int getIndex() {
            return index;
        }

        public void setDistance( double distance ) {
            this.distance = distance;
        }

        public void setTrack( ParametricFunction2D track ) {
            this.track = track;
        }

        public void setAlpha( double alpha ) {
            this.alpha = alpha;
        }

        public void setIndex( int index ) {
            this.index = index;
        }
    }

    class FreeFall implements UpdateStrategy {

        public void stepInTime( double dt ) {
            double origEnergy = getTotalEnergy();
            boolean[] origAbove = getOrigAbove();

            SerializablePoint2D origLoc = new SerializablePoint2D( x, y );
            double ay = g + yThrust;
            double ax = 0 + xThrust;
            vy += ay * dt;
            vx += ax * dt;

            y += vy * dt + 0.5 * ay * dt * dt;
            x += vx * dt + 0.5 * ax * dt * dt;

            double dE = getTotalEnergy() - origEnergy;
            if ( shouldFixFreeFallEnergy() ) {
                double dH = dE / ( getMass() * getGravity() );
                y += dH;
            }
            SerializablePoint2D newLoc = new SerializablePoint2D( x, y );

            //take a min over all possible crossover points
            SearchState searchState = getBestCrossPoint( newLoc, origAbove, origLoc );
            if ( !Double.isInfinite( searchState.getDistance() ) ) {
                EnergySkateParkLogging.println( "searchState.getDistance() = " + searchState.getDistance() );
            }
            boolean interactWithTrack = searchState.getDistance() < 0.2;//this number was determined heuristically for a set of tests (free parameter), doesn't work very well for large gravity field
            if ( interactWithTrack ) {
                interactWithTrack( searchState, newLoc, origLoc, origAbove, origEnergy, dt );
            }
            double finalEnergy = getTotalEnergy();
            if ( shouldFixFreeFallEnergy() && Math.abs( finalEnergy - origEnergy ) >= 1E-6 ) {
                EnergySkateParkLogging.println( "Energy error in freefall, interactWithTrack=" + interactWithTrack );
            }
        }

        private boolean shouldFixFreeFallEnergy() {
            return Math.abs( getMass() * getGravity() ) > 1E-6 && xThrust == 0 && yThrust == 0;
        }

        private void interactWithTrack( SearchState searchState, SerializablePoint2D newLoc, SerializablePoint2D origLoc, boolean[] origAbove, double origEnergy, double dt ) {
            ParametricFunction2D cubicSpline = searchState.getTrack();
            double alpha = searchState.getAlpha();
            ImmutableVector2D parallel = cubicSpline.getUnitParallelVector( alpha );
            ImmutableVector2D norm = cubicSpline.getUnitNormalVector( alpha );
            //reflect the velocity about the parallel direction
            ImmutableVector2D parallelVelocity = parallel.getInstanceOfMagnitude( parallel.dot( getVelocity() ) );

            ImmutableVector2D newNormalVelocity = norm.getInstanceOfMagnitude( norm.dot( getVelocity() ) ).getScaledInstance( elasticity );
            ImmutableVector2D newVelocity = parallelVelocity.getSubtractedInstance( newNormalVelocity );

            double testVal = Math.abs( newNormalVelocity.getMagnitude() / newVelocity.getMagnitude() );

            double p = Math.abs( getVelocity().getMagnitude() / getGravity() / dt );

            boolean bounce = testVal >= ( stickiness + getTrackStickiness( cubicSpline ) );
            double GRAB_THRESHOLD = 3.0;
            if ( p < GRAB_THRESHOLD ) {
                EnergySkateParkLogging.println( "p = " + p );
                EnergySkateParkLogging.println( "Grabbing due to small speed (for this g and dt), threshold=" + GRAB_THRESHOLD + ", v/(g*dt)=" + p );
                bounce = false;
            }

            double newAlpha = cubicSpline.getClosestPoint( newLoc );

            //make sure the velocity is toward the track to enable switching to track (otherwise over a tight curve, the particle doesn't leave the track when N~0)
            boolean velocityTowardTrack = isVelocityTowardTrack( origLoc, cubicSpline, newAlpha );
            if ( bounce || !velocityTowardTrack ) {

                SimSharingManager.sendModelMessage( skater, ModelComponentTypes.modelElement, bounced, ParameterSet.parameterSet( trackIndex, cubicSpline.index ).with( isFloor, cubicSpline instanceof LinearFloorSpline2D ) );
                double energyBeforeBounce = getTotalEnergy();
                setVelocity( newVelocity );

                //set the position to be just on top of the spline
                offsetOnSpline( cubicSpline, newAlpha, origAbove[searchState.getIndex()] );

                if ( getTotalEnergy() > energyBeforeBounce ) {
                    correctEnergyReduceVelocity( energyBeforeBounce );
                }
                thermalEnergy += ( energyBeforeBounce - getTotalEnergy() );
                if ( reorientOnBounce ) {
                    orientAngleOnTrack( cubicSpline, newAlpha, origAbove[searchState.getIndex()] );
                }
            }
            else {
                //grab the track
                double dE0 = getTotalEnergy() - origEnergy;
                SimSharingManager.sendModelMessage( skater, ModelComponentTypes.modelElement, landed, ParameterSet.parameterSet( trackIndex, cubicSpline.index ).with( isFloor, cubicSpline instanceof LinearFloorSpline2D ) );
                switchToTrack( cubicSpline, newAlpha, origAbove[searchState.getIndex()] );
                double dE2 = getTotalEnergy() - origEnergy;
                if ( Math.abs( dE2 ) > 1E-6 ) {
                    //energy error on track attachment.
                    if ( dE2 < 0 ) {//lost energy
                        thermalEnergy += Math.abs( dE2 );
                    }
                    else {
                        //See if particle1d can fix energy problem
                        particle1D.fixEnergy( particle1D.getAlpha(), origEnergy );
                        updateStateFrom1D();
                        double dE3 = getTotalEnergy() - origEnergy;
                        if ( Math.abs( dE3 ) > 1E-6 ) {
                            EnergySkateParkLogging.println( "particle1d couldn't fix, deleting thermal energy (temporary solution): dE=" + Math.abs( dE3 ) );
                            thermalEnergy -= Math.abs( dE3 );
                        }
                        else {
                        }
                    }
                }
            }
        }
    }

    private void orientAngleOnTrack( ParametricFunction2D cubicSpline, double newAlpha, boolean b ) {
        setAngle( cubicSpline.getAngle( newAlpha ) + ( b ? Math.PI : 0.0 ) );
    }

    private SearchState getBestCrossPoint( SerializablePoint2D pt, boolean[] origAbove, SerializablePoint2D origLoc ) {
        SearchState searchState = new SearchState( Double.POSITIVE_INFINITY, null, 0, -1 );
        for ( int i = 0; i < particleStage.getCubicSpline2DCount(); i++ ) {
            ParametricFunction2D cubicSpline = particleStage.getCubicSpline2D( i );
            double alpha = cubicSpline.getClosestPoint( new Line2D.Double( origLoc, pt ) );
            boolean above = isAboveSpline( cubicSpline, alpha, pt );
            //check for crossover
            boolean crossed = origAbove[i] != above;
            if ( crossed && ( alpha >= 0.0 && alpha <= 1.0 ) ) {
                double ptLineDist = pointSegmentDistance( cubicSpline.evaluate( alpha ), new Line2D.Double( origLoc, pt ) );
                if ( ptLineDist < searchState.getDistance() ) {
                    searchState.setDistance( ptLineDist );
                    searchState.setTrack( cubicSpline );
                    searchState.setAlpha( alpha );
                    searchState.setIndex( i );
                }
            }
        }
        return searchState;
    }

    private static double getTrackStickiness( ParametricFunction2D cubicSpline ) {
        if ( cubicSpline instanceof TrackWithStickiness ) {
            TrackWithStickiness trackWithStickiness = (TrackWithStickiness) cubicSpline;
            return trackWithStickiness.getStickiness();
        }
        else {
            return 0.0;
        }
    }

    private boolean isVelocityTowardTrack( SerializablePoint2D origPosition, ParametricFunction2D cubicSpline, double newAlpha ) {
        Vector2D vel = getVelocity();
        Vector2D toTrack = new Vector2D( origPosition, cubicSpline.evaluate( newAlpha ) );
        return vel.dot( toTrack ) > 0;
    }

    private void offsetOnSpline( ParametricFunction2D cubicSpline, double alpha, boolean top ) {
        ImmutableVector2D norm = cubicSpline.getUnitNormalVector( alpha );
        SerializablePoint2D splineLoc = cubicSpline.evaluate( alpha );
        double sign = top ? 1.0 : -1.0;
        SerializablePoint2D finalPosition = new SerializablePoint2D( norm.getInstanceOfMagnitude( 1.0E-3 * sign ).getDestination( splineLoc ) );//todo: determine this free parameter
        setPosition( finalPosition );
    }

    public void setVelocity( ImmutableVector2D velocity ) {
        setVelocity( velocity.getX(), velocity.getY() );
    }

    public Vector2D getVelocity() {
        return new Vector2D( vx, vy );
    }

    public boolean isAboveSpline( ParametricFunction2D parametricFunction2D, double alpha, SerializablePoint2D loc ) {
        ImmutableVector2D v = parametricFunction2D.getUnitNormalVector( alpha );
        Vector2D a = new Vector2D( parametricFunction2D.evaluate( alpha ), loc );
        return a.dot( v ) > 0;
    }

    public void setFreeFall() {
        setUpdateStrategy( new FreeFall() );
    }

    public void switchToTrack( ParametricFunction2D spline, double alpha, boolean top ) {
        double origEnergy = getTotalEnergy();
        particle1D.setThermalEnergy( thermalEnergy );
        Vector2D origVel = getVelocity();
        particle1D.setCubicSpline2D( spline, top, alpha );
        double sign = spline.getUnitParallelVector( alpha ).dot( getVelocity() ) > 0 ? 1.0 : -1.0;

        double newVelocityMagnitude = convertNormalVelocityToThermalOnLanding ? getParallelSpeed( spline, alpha ) : getSpeed();
        particle1D.setVelocity( newVelocityMagnitude * sign );

        setUpdateStrategy( new Particle1DUpdate() );
        ImmutableVector2D newVelocity = particle1D.getVelocity2D();
        double dot = newVelocity.dot( origVel );
        if ( dot < 0 ) {
            EnergySkateParkLogging.println( "Velocities were in contrary directions" );
        }
        double newEnergy = particle1D.getEnergy();
        double dE = ( newEnergy - origEnergy );
        if ( dE <= 0 ) {
            particle1D.addThermalEnergy( Math.abs( dE ) );
        }
        else {
            EnergySkateParkLogging.println( "gained energy on landing, not solved yet" ); //todo: solve energy problem on landing
        }
        updateStateFrom1D();
    }

    public boolean isConvertNormalVelocityToThermalOnLanding() {
        return convertNormalVelocityToThermalOnLanding;
    }

    public void setConvertNormalVelocityToThermalOnLanding( boolean convertNormalVelocityToThermalOnLanding ) {
        this.convertNormalVelocityToThermalOnLanding = convertNormalVelocityToThermalOnLanding;
    }

    private double getParallelSpeed( ParametricFunction2D parametricFunction2D, double alpha ) {
        return Math.abs( parametricFunction2D.getUnitParallelVector( alpha ).dot( getVelocity() ) );
    }

    public double getSpeed() {
        return Math.sqrt( vx * vx + vy * vy );
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled = userControlled;
        if ( userControlled ) {
            setThermalEnergy( 0.0 );
        }
        if ( !userControlled ) {

        }
    }

    public void setThermalEnergy( double thermalEnergy ) {
        this.thermalEnergy = thermalEnergy;
        particle1D.setThermalEnergy( thermalEnergy );
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
        update();
    }

    private void update() {
        notifyListeners();
    }


    public SerializablePoint2D getPosition() {
        return new SerializablePoint2D( x, y );
    }

    public void setPosition( SerializablePoint2D pt ) {
        setPosition( pt.getX(), pt.getY() );
    }

    public void setPosition( double x, double y ) {
        setFreeFall();
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setVelocity( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
        notifyListeners();
    }

    public static interface Listener {
        void particleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.particleChanged();
        }
    }

}
