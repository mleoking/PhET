/*  */
package edu.colorado.phet.theramp.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:12:09 AM
 */

public class RampPhysicalModel implements ModelElement, Surface.CollisionListener {
    private Surface ground;
    private Surface ramp;
    private Block block;
    private ForceVector wallForce;
    private ForceVector appliedForce;
    private ForceVector gravityForce;
    private ForceVector totalForce;
    private ForceVector frictionForce;
    private ForceVector normalForce;
    private double gravity = 9.8;
    private double appliedWork = 0.0;
    private double frictiveWork = 0.0;
    private double gravityWork = 0.0;
    private double zeroPointY = 0.0;
    private double thermalEnergy = 0.0;

    private boolean userAddingEnergy = false;
    private ArrayList listeners = new ArrayList();
    private SimpleObservable peObservers = new SimpleObservable();
    private SimpleObservable keObservers = new SimpleObservable();
    private double lastTick;

    private ModelElement stepStrategy;
    private double originalBlockKE;

    private RampPhysicalModel lastState;
    private double appliedForceSetValue = 0.0;
    //    private static final double INIT_ANGLE = 0.0;
    //    private static final double INIT_ANGLE = 30 * Math.PI * 2 / 360;
    private static final double INIT_ANGLE = 10.0 * Math.PI * 2 / 360;

    public RampPhysicalModel() {
        ramp = new Ramp( Math.PI / 32, 15.0 );
        ramp.addCollisionListener( this );
        ground = new Ground( 0, 6, -6, 0, 0 );
        ramp.setDistanceOffset( ground.getLength() );
        ground.addCollisionListener( this );
        block = new Block( ramp );
        wallForce = new ForceVector();
        gravityForce = new ForceVector();
        totalForce = new ForceVector();
        frictionForce = new ForceVector();
        appliedForce = new ForceVector();
        normalForce = new ForceVector();
        setStepStrategy( new RampPhysicalModel.NewStepCode() );
        setupForces();
    }

    private void updateAppliedForceValue() {
        setAppliedForce( appliedForceSetValue );
    }

    public void setStepStrategy( ModelElement stepStrategy ) {
        this.stepStrategy = stepStrategy;
    }

    public Surface getRamp() {
        return ramp;
    }

    public Block getBlock() {
        return block;
    }

    public double currentTimeSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

    public void stepInTime( double dt ) {
//        System.out.println( "@"+System.currentTimeMillis() +"dt = " + dt );
        stepStrategy.stepInTime( dt );
        updateAppliedForceValue();
    }

    /**
     * We solve this as a system of 5 equations and 8 unknowns.
     * We take KE & PE as given (from newton), and delta Work applied (computed) as given.
     *
     * @param dt
     */
    private void newStepCode( double dt ) {
//        double origTotalEnergy = getTotalEnergy();
        if ( lastTick != 0.0 ) {
            dt = currentTimeSeconds() - lastTick;
            dt = MathUtil.clamp( 1 / 30.0, dt, 1 / 5.0 );
//            RampModel orig = lastState;
            RampPhysicalModel initialState = getState();

            setupForces();
            double acceleration = totalForce.getParallelComponent() / block.getMass();
            block.setAcceleration( acceleration );
            originalBlockKE = block.getKineticEnergy();
            block.stepInTime( this, dt ); //could fire a collision event.

            if ( block.getStaticFriction() == 0 && block.getKineticFriction() == 0 ) {
//                for ( int i = 0; i < 10; i++ ) {
//                    if ( Math.abs( block.getVelocity() ) > 1E-2 ) {
//                        double totalEnergy = getTotalEnergy();
//                        double dE = totalEnergy - origTotalEnergy;
//                        double dv = dE / block.getMass() / block.getVelocity();
//                        block.setVelocity( block.getVelocity() - dv );
//                    }
//                }

                appliedWork = getTotalEnergy();
                gravityWork = -getPotentialEnergy();
                thermalEnergy = initialState.getThermalEnergy();
                if ( block.isJustCollided() ) {
                    thermalEnergy += lastState.getKineticEnergy();
                }
                frictiveWork = -thermalEnergy;
            }
            else {
                double dW = getAppliedWorkDifferential( initialState );

                //todo if user controlled, this should add to dW (and appliedWork) so it
                //doesn't show up in the thermal energy.
                appliedWork += dW;
                gravityWork = -getPotentialEnergy();
                double etot = appliedWork;
                thermalEnergy = etot - getKineticEnergy() - getPotentialEnergy();
                frictiveWork = -thermalEnergy;

                //So height of totalEnergy bar should always be same as height W_app bar
                double dE = getTotalEnergy() - getAppliedWork();
                if ( Math.abs( dE ) > 1.0E-9 ) {
                    System.out.println( "dE=" + dE + ", EnergyTotal=" + getTotalEnergy() + ", WorkApplied=" + getAppliedWork() );
                }
                //deltaKE = W_net
                double dK = getBlock().getKineticEnergy() - getTotalWork();
                if ( Math.abs( dK ) > 1.0E-9 ) {
                    System.out.println( "dK=" + dK + ", Delta KE=" + getBlock().getKineticEnergy() + ", Net Work=" + getTotalWork() );
                }
            }

            if ( block.getKineticEnergy() != lastState.getKineticEnergy() ) {
                keObservers.notifyObservers();
            }

            if ( getPotentialEnergy() != lastState.getPotentialEnergy() ) {
                peObservers.notifyObservers();
            }
        }
        lastTick = currentTimeSeconds();
        lastState = getState();
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.stepFinished();
        }
    }

    private double getKineticEnergy() {
        return getBlock().getKineticEnergy();
    }

    private double getAppliedWorkDifferential( RampPhysicalModel beforeNewton ) {
        double blockDX = getBlockPosition() - beforeNewton.getBlockPosition();
        double workDueToAppliedForce = getAppliedForce().getParallelComponent() * blockDX;
        double workDueToRampLift = beforeNewton.getPotentialEnergy() - lastState.getPotentialEnergy();
        double dW = workDueToAppliedForce + workDueToRampLift;
        return dW;
    }

    public void setupForces() {
        gravityForce.setX( 0 );
        gravityForce.setY( gravity * block.getMass() );
        double fa = block.getFrictionForce( gravity, appliedForce.getParallelComponent() + gravityForce.getParallelComponent() );
        frictionForce.setParallel( fa );

        double netForce = appliedForce.getParallelComponent() + gravityForce.getParallelComponent() + frictionForce.getParallelComponent();
        normalForce.setPerpendicular( gravityForce.getPerpendicularComponent() );

        double wallForce = getSurface().getWallForce( netForce, getBlock() );
        netForce += wallForce;

        this.wallForce.setParallel( wallForce );
        totalForce.setParallel( netForce );

        updateAppliedForceValue();
    }

    public void setWallForce( double wallForce ) {
        this.wallForce.setParallel( wallForce );
    }

    private double getBlockPosition() {
        return getBlock().getPosition();
    }

    private double getMechanicalEnergy() {
        return block.getKineticEnergy() + getPotentialEnergy();
    }

    public void setUserIsAddingEnergy( boolean userAddingEnergy ) {
        this.userAddingEnergy = userAddingEnergy;
    }

    public double getPotentialEnergy() {
        double height = getBlockHeight();
        return block.getMass() * height * gravity;
    }

    private double getBlockHeight() {
        return block.getLocation2D().getY() - zeroPointY;
    }

    public void setAppliedForce( double appliedForce ) {
//        double origAppliedForce=appliedForceSetValue;
        this.appliedForceSetValue = appliedForce;
        this.appliedForce.setParallel( appliedForce );//could be different, even if applied force set value is same.
//        if( origAppliedForce!= appliedForce ) {
        notifyAppliedForceChanged();//todo: to ensure graphics update, could be fixed later.
//        }
    }

    private void notifyAppliedForceChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.appliedForceChanged();
        }
    }

    public void addKEObserver( SimpleObserver simpleObserver ) {
        keObservers.addObserver( simpleObserver );
    }

    public void addPEObserver( SimpleObserver simpleObserver ) {
        peObservers.addObserver( simpleObserver );
    }

    public ForceVector getWallForce() {
        return wallForce;
    }

    public ForceVector getAppliedForce() {
        return appliedForce;
    }

    public ForceVector getGravityForce() {
        return gravityForce;
    }

    public ForceVector getTotalForce() {
        return totalForce;
    }

    public ForceVector getFrictionForce() {
        return frictionForce;
    }

    public ForceVector getNormalForce() {
        return normalForce;
    }

    public void reset() {
        block.setSurface( ramp );
        block.setPositionInSurface( 10.0 );
        block.setAcceleration( 0.0 );
        block.setVelocity( 0.0 );
        ramp.setAngle( INIT_ANGLE );
        appliedWork = 0;
        frictiveWork = 0;
        gravityWork = 0;
        thermalEnergy = 0.0;
        peObservers.notifyObservers();
        keObservers.notifyObservers();
        lastState = getState();
        appliedForceSetValue = 0.0;
        setupForces();
        initWorks();
    }

    public void initWorks() {
        gravityWork = -getPotentialEnergy();
        appliedWork = -gravityWork + getKineticEnergy();
    }

    public double getFrictiveWork() {
        return frictiveWork;
    }

    public double getGravityWork() {
        return gravityWork;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setZeroPointY( double zeroPointY ) {
        this.zeroPointY = zeroPointY;
        //TODO updates.
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.zeroPointChanged();
        }
        peObservers.notifyObservers();
    }

    public double getZeroPointY() {
        return zeroPointY;
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }

    public double getTotalEnergy() {
        return getPotentialEnergy() + getBlock().getKineticEnergy() + getThermalEnergy();
    }

    public Surface getGround() {
        return ground;
    }

    private Surface getSurface() {
        return block.getSurface();
    }

    public void setMass( double value ) {
        block.setMass( value );
    }

    public void setObject( RampObject rampObject ) {
        getBlock().setMass( rampObject.getMass() );
        getBlock().setStaticFriction( rampObject.getStaticFriction() );
        getBlock().setKineticFriction( rampObject.getKineticFriction() );
    }

    public void setStepStrategyConstrained() {
        setStepStrategy( new NewStepCode() );
    }

    public void collided( Surface surface ) {
        if ( block.isFrictionless() ) {
            double changeInEnergy = Math.abs( block.getKineticEnergy() - originalBlockKE );

            thermalEnergy += changeInEnergy;
            frictiveWork -= changeInEnergy;
        }
    }

    public void clearHeat() {
        thermalEnergy = 0.0;
        frictiveWork = 0.0;
        initWorks();
    }

    public double getRampAngle() {
        return getRamp().getAngle();
    }

    public void setRampAngle( double angle ) {
        getRamp().setAngle( angle );
    }

    public double getGlobalMaxPosition() {
        return getGround().getLength() + getRamp().getLength();
    }

    public double getGlobalMinPosition() {
        return 0;
    }

    public double getGlobalBlockPosition() {
        return block.getPosition();
    }

    public void setGlobalBlockPosition( double position ) {
        if ( position <= getGround().getLength() ) {
            block.setSurface( getGround() );
            block.setPositionInSurface( position );
        }
        else {
            block.setSurface( getRamp() );
            block.setPositionInSurface( position - getGround().getLength() );
        }
    }

    public double getAppliedForceScalar() {
        return appliedForceSetValue;
    }

    public double getParallelFrictionForce() {
        return frictionForce.getParallelComponent();
    }

    public double getParallelAppliedForce() {
        return appliedForce.getParallelComponent();
    }

    public double getParallelWeightForce() {
        return gravityForce.getParallelComponent();
    }

    public double getParallelWallForce() {
        return wallForce.getParallelComponent();
    }

    public Surface getSurfaceGraphic( double modelLocation ) {
        if ( modelLocation <= ground.getLength() ) {
            return ground;
        }
        else {
            return ramp;
        }
    }

    public class ForceVector extends Vector2D.Double {

        public void setParallel( double parallel ) {
            setX( Math.cos( -getSurface().getAngle() ) * parallel );
            setY( Math.sin( -getSurface().getAngle() ) * parallel );
//            System.out.println( "parallel = " + parallel + " magnitude=" + getMagnitude() );
        }

        public double getParallelComponent() {
//            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -ramp.getAngle() );
            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -getSurface().getAngle() );
            return dir.dot( this );
        }

        public double getPerpendicularComponent() {
//            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -ramp.getAngle() );
            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -getSurface().getAngle() );
            dir = dir.getNormalVector();
            return dir.dot( this );
        }

        public void setPerpendicular( double perpendicularComponent ) {
            setX( Math.sin( getSurface().getAngle() ) * perpendicularComponent );
            setY( Math.cos( getSurface().getAngle() ) * perpendicularComponent );
//            System.out.println( "perp= " + perpendicularComponent + " magnitude=" + getMagnitude() );
        }

        public Vector2D toParallelVector() {
            ForceVector fv = new ForceVector();
            fv.setParallel( getParallelComponent() );
            return fv;
        }

        public Vector2D toPerpendicularVector() {
            ForceVector fv = new ForceVector();
            fv.setPerpendicular( -getPerpendicularComponent() );
            return fv;
        }

        public Vector2D toXVector() {
            return new Vector2D.Double( getX(), 0 );
        }

        public Vector2D toYVector() {
            return new Vector2D.Double( 0, getY() );
        }

        public ForceVector copyState() {
            ForceVector copy = new ForceVector();
            copy.setX( getX() );
            copy.setY( getY() );
            return copy;
        }

        public void setState( ForceVector state ) {
            setX( state.getX() );
            setY( state.getY() );
        }
    }

    public double getAppliedWork() {
        return appliedWork;
    }

    public static interface Listener {
        void appliedForceChanged();

        void zeroPointChanged();

        void stepFinished();

//        void angleChanged();
    }

    public static class Adapter implements Listener {

        public void appliedForceChanged() {
        }

        public void zeroPointChanged() {
        }

        public void stepFinished() {
        }

        public void angleChanged() {
        }
    }

    public RampPhysicalModel getState() {
        RampPhysicalModel copy = new RampPhysicalModel();
        copy.ramp = ramp.copyState();
        copy.ground = ground.copyState();
        copy.block = block.copyState( this, copy );
        copy.wallForce = wallForce.copyState();
        copy.appliedForce = appliedForce.copyState();
        copy.gravityForce = gravityForce.copyState();
        copy.totalForce = totalForce.copyState();
        copy.frictionForce = frictionForce.copyState();
        copy.normalForce = normalForce.copyState();
        copy.gravity = gravity;
        copy.appliedWork = appliedWork;
        copy.frictiveWork = frictiveWork;
        copy.gravityWork = gravityWork;
        copy.zeroPointY = zeroPointY;
        copy.thermalEnergy = thermalEnergy;
        copy.appliedForceSetValue = appliedForceSetValue;
        return copy;
    }

    public void setState( RampPhysicalModel state ) {
        ramp.setState( state.getRamp() );
        block.setState( state.getBlock() );
        wallForce.setState( state.wallForce );
        appliedForce.setState( state.appliedForce );
        gravityForce.setState( state.gravityForce );
        totalForce.setState( state.totalForce );
        frictionForce.setState( state.frictionForce );
        normalForce.setState( state.normalForce );
        gravity = state.gravity;
        appliedWork = state.appliedWork;
        frictiveWork = state.frictiveWork;
        gravityWork = state.gravityWork;
        zeroPointY = state.zeroPointY;
        thermalEnergy = state.thermalEnergy;
        appliedForceSetValue = state.appliedForceSetValue;
        //todo notify observers.
    }

    public double getTotalWork() {
        return getGravityWork() + getFrictiveWork() + getAppliedWork();
    }

    public NewStepCode getNewStepCode() {
        return new NewStepCode();
    }

    public class NewStepCode implements ModelElement {
        public void stepInTime( double dt ) {
            newStepCode( dt );
        }
    }
}