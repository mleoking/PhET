/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.history;


/**
 * User: Sam Reid
 * Date: Jul 14, 2003
 * Time: 10:39:42 AM
 * Copyright (c) Jul 14, 2003 by Sam Reid
 */
public class EnergyDot {
    double x;
    double y;
    double time;
    double kineticEnergy;
    double potentialEnergy;
    private double height;
    private double speed;

    public EnergyDot( double x, double y, double time, double kineticEnergy, double potentialEnergy, double height, double speed ) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.kineticEnergy = kineticEnergy;
        this.potentialEnergy = potentialEnergy;
        this.height = height;
        this.speed = speed;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public double getTime() {
        return time;
    }

    public double getKineticEnergy() {
        return kineticEnergy;
    }

    public double getPotentialEnergy() {
        return potentialEnergy;
    }

    public double getHeight() {
        return height;
    }

    public double getSpeed() {
        return speed;
    }
}
