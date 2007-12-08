package edu.colorado.phet.movingman.motion.ramps;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Created by: Sam
 * Dec 8, 2007 at 9:49:55 AM
 */
public class Force1DModelObject {
    private DefaultTemporalVariable time = new DefaultTemporalVariable();
    private DefaultTemporalVariable2D position = new DefaultTemporalVariable2D();
    private DefaultTemporalVariable2D velocity = new DefaultTemporalVariable2D();
    private DefaultTemporalVariable2D acceleration = new DefaultTemporalVariable2D();

    private DefaultTemporalVariable2D appliedForce = new DefaultTemporalVariable2D();
    private DefaultTemporalVariable2D frictionForce = new DefaultTemporalVariable2D();
    private DefaultTemporalVariable2D wallForce = new DefaultTemporalVariable2D();
    private DefaultTemporalVariable2D netForce = new DefaultTemporalVariable2D();
    private DefaultTemporalVariable2D normalForce = new DefaultTemporalVariable2D();
    private DefaultTemporalVariable2D gravityForce = new DefaultTemporalVariable2D();

    private DefaultTemporalVariable gravity = new DefaultTemporalVariable();
    private DefaultTemporalVariable staticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable kineticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable mass = new DefaultTemporalVariable( 30 );
    private DefaultTemporalVariable angle = new DefaultTemporalVariable( 0.0 );

    private DefaultTemporalVariable totalEnergy = new DefaultTemporalVariable();
    private DefaultTemporalVariable potentialEnergy = new DefaultTemporalVariable();
    private DefaultTemporalVariable kineticEnergy = new DefaultTemporalVariable();
    private DefaultTemporalVariable thermalEnergy = new DefaultTemporalVariable();

    private DefaultTemporalVariable totalWork = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedWork = new DefaultTemporalVariable();
    private DefaultTemporalVariable gravityWork = new DefaultTemporalVariable();
    private DefaultTemporalVariable frictionWork = new DefaultTemporalVariable();

    private ControlGraphSeries appliedForceSeries = new ControlGraphSeries( "Fa", Color.blue, "Fa", "N", new BasicStroke( 2 ), null, appliedForce.getX(), true );
    private ControlGraphSeries frictionForceSeries = new ControlGraphSeries( "Ff", Color.red, "Ff", "N", new BasicStroke( 2 ), null, frictionForce.getX(), false );
    private ControlGraphSeries wallForceSeries = new ControlGraphSeries( "Fw", Color.magenta, "Fw", "N", new BasicStroke( 2 ), null, wallForce.getX(), false );
    private ControlGraphSeries netForceSeries = new ControlGraphSeries( "Fnet", Color.green, "Fnet", "N", new BasicStroke( 2 ), null, netForce.getX(), false );
    private ControlGraphSeries normalForceSeries = new ControlGraphSeries( "FNormal", Color.green, "FNormal", "N", new BasicStroke( 2 ), null, normalForce.getX(), false );

    private ControlGraphSeries gravitySeries = new ControlGraphSeries( "Fg", Color.green, "a", "N", new BasicStroke( 2 ), null, gravity, true );
    private ControlGraphSeries staticFrictionSeries = new ControlGraphSeries( "us", Color.green, "", "m/s^2", new BasicStroke( 2 ), null, staticFriction, false );
    private ControlGraphSeries kineticFrictionSeries = new ControlGraphSeries( "uk", Color.green, "", "m/s^2", new BasicStroke( 2 ), null, kineticFriction, false );
    private ControlGraphSeries massSeries = new ControlGraphSeries( "m", Color.green, "a", "m/s^2", new BasicStroke( 2 ), null, mass, true );


    private ITemporalVariable[] getForce1DVars() {
        return new ITemporalVariable[]{appliedForce.getX(), appliedForce.getY(), frictionForce.getX(), frictionForce.getY(), wallForce.getX(), wallForce.getY(), netForce.getX(), netForce.getY(), gravity, staticFriction, kineticFriction, mass};
    }

    public void setAppliedForce( double appliedForceValue ) {
        appliedForce.setXValue( appliedForceValue );
    }

    public ControlGraphSeries getAppliedForceSeries() {
        return appliedForceSeries;
    }

    public ITemporalVariable getAppliedForce() {
        return appliedForce.getX();
    }

    public ITemporalVariable getFrictionForce() {
        return frictionForce.getX();
    }

    public ITemporalVariable getWallForce() {
        return wallForce.getX();
    }

    public ITemporalVariable getNetForce() {
        return netForce.getX();
    }

    public DefaultTemporalVariable getGravity() {
        return gravity;
    }

    public DefaultTemporalVariable getStaticFriction() {
        return staticFriction;
    }

    public DefaultTemporalVariable getKineticFriction() {
        return kineticFriction;
    }

    public DefaultTemporalVariable getMass() {
        return mass;
    }

    public ControlGraphSeries getFrictionForceSeries() {
        return frictionForceSeries;
    }

    public ControlGraphSeries getWallForceSeries() {
        return wallForceSeries;
    }

    public ControlGraphSeries getNetForceSeries() {
        return netForceSeries;
    }

    public ControlGraphSeries getGravitySeries() {
        return gravitySeries;
    }

    public ControlGraphSeries getStaticFrictionSeries() {
        return staticFrictionSeries;
    }

    public ControlGraphSeries getKineticFrictionSeries() {
        return kineticFrictionSeries;
    }

    public ControlGraphSeries getMassSeries() {
        return massSeries;
    }

    public ITemporalVariable getNormalForce() {
        return normalForce.getX();
    }

    public void stepInTime( double dt, double time ) {
        newStepCode( dt, time );
    }

    /**
     * We solve this as a system of 5 equations and 8 unknowns.
     * We take KE & PE as given (from newton), and delta Work applied (computed) as given.
     *
     * @param dt time step
     */
    private void newStepCode( double dt, double time ) {
//            dt = currentTimeSeconds() - lastTick;
//            dt = MathUtil.clamp( 1 / 30.0, dt, 1 / 5.0 );
//            RampModel orig = lastState;
//            RampMotionModel initialState = getState();

        setupForces();
        updateBlock( dt, time );

//        updateEnergyAndWork();

//        lastTick = getTime();
//        lastState = getState();
//        for ( int i = 0; i < listeners.size(); i++ ) {
//            Listener listener = (Listener) listeners.get( i );
//            listener.stepFinished();
//        }
    }

//    private void updateEnergyAndWork() {
//
//        if ( getStaticFriction().getValue() == 0 && getKineticFriction().getValue() == 0 ) {
//            double appliedWork = getTotalEnergy();
//            double gravityWork = -getPotentialEnergy();
//            double thermalEnergy = initialState.getThermalEnergy();
//            if ( block.isJustCollided() ) {
//                thermalEnergy += lastState.getKineticEnergy();
//            }
//            double frictiveWork = -thermalEnergy;
//        }
//        else {
//            double dW = getAppliedWorkDifferential( initialState );
//
//            //todo if user controlled, this should add to dW (and appliedWork) so it
//            //doesn't show up in the thermal energy.
//            appliedWork += dW;
//            gravityWork = -getPotentialEnergy();
//            double etot = appliedWork;
//            thermalEnergy = etot - getKineticEnergy() - getPotentialEnergy();
//            frictiveWork = -thermalEnergy;
//
//            //So height of totalEnergy bar should always be same as height W_app bar
//            double dE = getTotalEnergy() - getAppliedWork();
//            if ( Math.abs( dE ) > 1.0E-9 ) {
//                System.out.println( "dE=" + dE + ", EnergyTotal=" + getTotalEnergy() + ", WorkApplied=" + getAppliedWork() );
//            }
//            //deltaKE = W_net
//            double dK = getBlock().getKineticEnergy() - getTotalWork();
//            if ( Math.abs( dK ) > 1.0E-9 ) {
//                System.out.println( "dK=" + dK + ", Delta KE=" + getBlock().getKineticEnergy() + ", Net Work=" + getTotalWork() );
//            }
//        }
//
//        if ( block.getKineticEnergy() != lastState.getKineticEnergy() ) {
//            keObservers.notifyObservers();
//        }
//
//        if ( getPotentialEnergy() != lastState.getPotentialEnergy() ) {
//            peObservers.notifyObservers();
//        }
//    }

    //todo: should be addValue

    public void setupForces() {
//        appliedForce.setValue( 500, 0 );
//        netForce.setValue( appliedForce.getValue() );
        netForce.setValue( appliedForce.getValue() );
//        gravityForce.setXValue( 0 );
//        gravityForce.setYValue( gravity.getValue() * mass.getValue() );
//        double fa = block.getFrictionForce( gravity, appliedForce.getParallelComponent() + gravityForce.getParallelComponent() );
//        frictionForce.setParallel( fa );
//
//        double netForce = appliedForce.getParallelComponent() + gravityForce.getParallelComponent() + frictionForce.getParallelComponent();
//        normalForce.setPerpendicular( gravityForce.getPerpendicularComponent() );
//
//        double wallForce = getSurface().getWallForce( netForce, getBlock() );
//        netForce += wallForce;
//
//        this.wallForce.setParallel( wallForce );
//        totalForce.setParallel( netForce );
//
//        updateAppliedForceValue();
    }

    private void updateBlock( double dt, double time ) {
        acceleration.setValue( netForce.getScaledInstance( 1.0 / mass.getValue() ) );
        velocity.setValue( acceleration.getScaledInstance( dt ).getAddedInstance( velocity.getValue() ) );
        AbstractVector2D vector2D = velocity.getScaledInstance( dt ).getAddedInstance( position.getValue() );
        if ( vector2D.getX() < -10 || vector2D.getX() > 10 ) {
            vector2D = new Vector2D.Double( MathUtil.clamp( -10, vector2D.getX(), 10 ), vector2D.getY() );
            velocity.setValue( new Vector2D.Double( 0, 0 ) );
            acceleration.setValue( new Vector2D.Double( 0, 0 ) );
        }
        position.setValue( MathUtil.clamp( -10, vector2D.getX(), 10 ), 0 );
        System.out.println( "position = " + position );

//        double a = netForce.getParallelComponent( angle.getValue() ) / mass.getValue();
//        acceleration.setParallel( a, angle.getValue() );
//        velocity.set
//                originalBlockKE = block.getKineticEnergy();
//        block.stepInTime( null, dt ); //could fire a collision event.
    }


    public ITemporalVariable getXVariable() {
        return position.getX();
    }
}
