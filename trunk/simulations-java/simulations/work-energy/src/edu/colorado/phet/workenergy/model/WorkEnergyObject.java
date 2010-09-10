package edu.colorado.phet.workenergy.model;

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
}