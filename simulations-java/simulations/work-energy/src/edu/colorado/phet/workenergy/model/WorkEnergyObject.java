package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class WorkEnergyObject {
    private DoubleProperty mass = new DoubleProperty( 20.0 );//kg
    private DoubleProperty gravity = new DoubleProperty( 0.0 );//kg/m/m; in space to start with

    private MutableVector2D position = new MutableVector2D( 0, 0 );
    private MutableVector2D velocity = new MutableVector2D( 0, 0 );
    private MutableVector2D acceleration = new MutableVector2D( 0, 0 );

    private MutableVector2D appliedForce = new MutableVector2D( 0, 0 );
    private MutableVector2D frictionForce = new MutableVector2D( 0, 0 );
    private MutableVector2D gravityForce = new MutableVector2D( 0, 0 );
    private MutableVector2D netForce = new MutableVector2D( 0, 0 );

    private DoubleProperty kineticEnergy = new DoubleProperty( 0 );
    private DoubleProperty thermalEnergy = new DoubleProperty( 0 );
    private DoubleProperty potentialEnergy = new DoubleProperty( 0 );
    private DoubleProperty totalEnergy = new DoubleProperty( 0 );

    private DoubleProperty netWork = new DoubleProperty( 0 );
    private DoubleProperty gravityWork = new DoubleProperty( 0 );
    private DoubleProperty frictionWork = new DoubleProperty( 0 );
    private DoubleProperty appliedWork = new DoubleProperty( 0 );

    public WorkEnergyObject() {
        final SimpleObserver updateNetForce = new SimpleObserver() {
            public void update() {
                netForce.setValue( appliedForce.getValue().getAddedInstance( frictionForce.getValue() ).getAddedInstance( gravityForce.getValue() ) );
            }
        };
        appliedForce.addObserver( updateNetForce );
        frictionForce.addObserver( updateNetForce );
        gravityForce.addObserver( updateNetForce );
        updateNetForce.update();

        //Velocity changing should trigger KE changing
        final SimpleObserver updateKineticEnergy = new SimpleObserver() {
            public void update() {
                kineticEnergy.setValue( 1 / 2.0 * mass.getValue() * velocity.getValue().getMagnitudeSq() );
            }
        };
        mass.addObserver( updateKineticEnergy );
        velocity.addObserver( updateKineticEnergy );
        updateKineticEnergy.update();

        //Position changing should possibly trigger PE changing
        final SimpleObserver updatePotentialEnergy = new SimpleObserver() {
            public void update() {
                potentialEnergy.setValue( mass.getValue() );
            }
        };
    }

    public void stepInTime( double dt ) {
        //Assumes driven by applied force, not user setting position manually
        acceleration.setValue( netForce.times( 1.0 / mass.getValue() ) );
        velocity.setValue( acceleration.times( dt ).getAddedInstance( velocity.getValue() ) );
        position.setValue( velocity.times( dt ).getAddedInstance( position.getValue() ) );
//        System.out.println("position = " + position);
    }

    public void setAppliedForce( double fx, double fy ) {
        appliedForce.setValue( new ImmutableVector2D( fx, fy ) );
    }

    public MutableVector2D getPositionProperty() {
        return position;
    }

    public MutableVector2D getVelocityProperty() {
        return velocity;
    }

    public DoubleProperty getKineticEnergyProperty() {
        return kineticEnergy;
    }

    public WorkEnergyObject copy() {
        final WorkEnergyObject snapshot = new WorkEnergyObject();
        snapshot.mass.setValue( mass.getValue() );
        snapshot.gravity.setValue( gravity.getValue() );
        snapshot.position.setValue( position.getValue() );
        snapshot.velocity.setValue( velocity.getValue() );
        snapshot.acceleration.setValue( acceleration.getValue() );
        snapshot.appliedForce.setValue( appliedForce.getValue() );
        snapshot.frictionForce.setValue( frictionForce.getValue() );
        snapshot.gravityForce.setValue( gravityForce.getValue() );
        snapshot.netForce.setValue( netForce.getValue() );
        snapshot.kineticEnergy.setValue( kineticEnergy.getValue() );
        snapshot.thermalEnergy.setValue( thermalEnergy.getValue() );
        snapshot.potentialEnergy.setValue( potentialEnergy.getValue() );
        snapshot.totalEnergy.setValue( totalEnergy.getValue() );
        snapshot.netWork.setValue( netWork.getValue() );
        snapshot.gravityWork.setValue( gravityWork.getValue() );
        snapshot.frictionWork.setValue( frictionWork.getValue() );
        snapshot.appliedWork.setValue( appliedWork.getValue() );
        return snapshot;
    }
}