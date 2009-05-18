package edu.colorado.phet.densityjava.model;

import java.awt.*;

public class Block extends DensityModel.RectangularObject {
    private BlockEnvironment blockEnvironment;  //environment for computing applied forces and collisions
    private Water water;//the water the block may interact with
    private double mass;
    private double velocity = 0;
    private boolean userDragging = false;//dynamics are off if the user is dragging the block.

    public Block(String name, double dim, Water water, double x, double y, Color color, double mass, BlockEnvironment blockEnvironment) {
        super(name, x, y, dim, dim, dim, color);
        this.water = water;
        this.mass = mass;
        this.blockEnvironment = blockEnvironment;
    }

    public void stepInTime(double simulationTimeChange) {
        if (userDragging) {
            //setVelocity(0);
        } else {
            double force = getGravityForce() + getBuoyancyForce() + getAppliedForce() + getNormalForce();
            double accel = force / mass;
            velocity += accel * simulationTimeChange;
            velocity = velocity * getDragCoefficient();
            setPosition2D(getX(), getY() + velocity * simulationTimeChange);
            if (getY() <= getFloorY()) {
                setPosition2D(getX(), getFloorY());
                velocity = 0;
            }
        }
    }

    public double getAppliedForce() {
        return blockEnvironment.getAppliedForce(this);
    }

    public double getDragCoefficient() {
        return 0.95;
    }

    //the bottom of the pool if over the pool, otherwise the ground level
    //todo: generalize for block stacking
    private double getFloorY() {
        return blockEnvironment.getFloorY(this);
    }

    public double getNormalForce() {
        if (getY() <= getFloorY() && getGravityForce() + getBuoyancyForce() + getAppliedForce() < 0) {
            return -getGravityForce() - getBuoyancyForce() - getAppliedForce();
        } else return 0;
    }


    public void setVelocity(double v) {
        this.velocity = v;
        notifyListeners();
    }

    //According to Archimedes' principle,
    // "Any object, wholly or partly immersed in a fluid,
    // is buoyed up by a force equal to the weight of the fluid displaced by the object."
    private double getBuoyancyForce() {
        double volumeDisplaced = getSubmergedVolume(water.getHeight());
        double massDisplaced = water.getDensity() * volumeDisplaced / 500;
        double weightDisplaced = massDisplaced * 9.8;
        return weightDisplaced;
    }

    private double getGravityForce() {
        return -9.8 * mass;
    }

    public void setUserDragging(boolean userDragging) {
        this.userDragging = userDragging;
        setVelocity(0.0);
    }

    public double getSpeed() {
        return Math.abs(velocity);
    }

}