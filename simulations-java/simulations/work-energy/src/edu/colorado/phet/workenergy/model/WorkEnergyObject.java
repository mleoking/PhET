package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class WorkEnergyObject {
    private MutableDouble mass = new MutableDouble(20.0);//kg
    private MutableDouble gravity = new MutableDouble(0.0);//kg/m/m; in space to start with

    private MutableVector2D position = new MutableVector2D(0, 0);
    private MutableVector2D velocity = new MutableVector2D(0, 0);
    private MutableVector2D acceleration = new MutableVector2D(0, 0);

    private MutableVector2D appliedForce = new MutableVector2D(0, 0);
    private MutableVector2D frictionForce = new MutableVector2D(0, 0);
    private MutableVector2D gravityForce = new MutableVector2D(0, 0);
    private MutableVector2D netForce = new MutableVector2D(0, 0);

    private MutableDouble kineticEnergy = new MutableDouble(0);
    private MutableDouble thermalEnergy = new MutableDouble(0);
    private MutableDouble potentialEnergy = new MutableDouble(0);
    private MutableDouble totalEnergy = new MutableDouble(0);

    private MutableDouble netWork = new MutableDouble(0);
    private MutableDouble gravityWork = new MutableDouble(0);
    private MutableDouble frictionWork = new MutableDouble(0);
    private MutableDouble appliedWork = new MutableDouble(0);

    public WorkEnergyObject() {
        final SimpleObserver updateNetForce = new SimpleObserver() {
            public void update() {
                netForce.setValue(appliedForce.getValue().getAddedInstance(frictionForce.getValue()).getAddedInstance(gravityForce.getValue()));
            }
        };
        appliedForce.addObserver(updateNetForce);
        frictionForce.addObserver(updateNetForce);
        gravityForce.addObserver(updateNetForce);
    }

    public void stepInTime(double dt) {
        //Assumes driven by applied force, not user setting position manually
        acceleration.setValue(netForce.times(1.0 / mass.getValue()));
        velocity.setValue(acceleration.times(dt).getAddedInstance(velocity.getValue()));
        position.setValue(velocity.times(dt).getAddedInstance(position.getValue()));
//        System.out.println("position = " + position);

        //Velocity changing should trigger KE changing
        //Position changing should possibly trigger PE changing
    }

    public void setAppliedForce(double fx, double fy) {
        appliedForce.setValue(new ImmutableVector2D(fx, fy));
    }

    public MutableVector2D getPositionProperty() {
        return position;
    }
}