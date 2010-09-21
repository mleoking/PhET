package edu.colorado.phet.densityjava.model;

import java.awt.*;

public class Block extends RectangularObject {
    private BlockEnvironment blockEnvironment;  //environment for computing applied forces and collisions
    private Water water;//the water the block may interact with
    private double mass;
    private double velocity = 0;//velocity in the y direction only
    private boolean userDragging = false;//dynamics are off if the user is dragging the block.

    public Block(String name, double dim, Water water, double x, double y, Color color, double mass, BlockEnvironment blockEnvironment) {
        super(name, x, y, dim, dim, dim, color);
        this.water = water;
        this.mass = mass;
        this.blockEnvironment = blockEnvironment;
    }

    //Dynamics computation for the block, all dynamics are in the vertical (y) direction.
    public void stepInTime(double dt) {
        if (!userDragging) {
            double force = getGravityForce() + getBuoyancyForce() + getAppliedForce();
            double accel = force / mass;
            velocity += accel * dt;
            velocity = velocity * getDragCoefficient();
            setY(getY() + velocity * dt);
            if (getY() <= getFloorY()) {
                setY(getFloorY());
                velocity = 0;
            }
        }
    }

    double getGravityForce() {
        return -9.8 * mass;
    }

    //According to Archimedes' principle,
    // "Any object, wholly or partly immersed in a fluid,
    // is buoyed up by a force equal to the weight of the fluid displaced by the object."
    double getBuoyancyForce() {
        double volumeDisplaced = getSubmergedVolume(water.getHeight());
        double massDisplaced = water.getDensity() * volumeDisplaced / 500;
        double weightDisplaced = massDisplaced * 9.8;
        return weightDisplaced;
    }

    public double getAppliedForce() {
        return blockEnvironment.getAppliedForce(this);
    }

    public double getDragCoefficient() {
        return 0.95;//todo: should use actual force model for friction?
    }

    //the bottom of the pool if over the pool, otherwise the ground level
    //todo: generalize for block stacking
    private double getFloorY() {
        return blockEnvironment.getFloorY(this);
    }

    public void setVelocity(double v) {
        this.velocity = v;
        notifyListeners();
    }

    public void setUserDragging(boolean userDragging) {
        this.userDragging = userDragging;
        setVelocity(0.0);
    }

    public double getSpeed() {
        return Math.abs(velocity);
    }

    private void setY(double v) {
        setPosition2D(getX(), v);
    }

    public double getMass() {
        return mass;
    }
}