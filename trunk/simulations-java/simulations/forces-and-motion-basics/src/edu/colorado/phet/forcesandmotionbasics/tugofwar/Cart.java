// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

/**
 * @author Sam Reid
 */
public class Cart {
    public final double weight = 1000;       //in newtons, weighs a little more than a big puller
    public static final boolean dev = false;
    private static final double DEBUG_SPEED = dev ? 5 : 1;
    private double velocity = 0;
    private double position = 0;

    public Cart() {
    }

    public double getPosition() {
        return position;
    }

    public void stepInTime( double dt, double acceleration ) {
        dt = dt * 20 * DEBUG_SPEED;//speed up time because the masses of the characters give everything too much momentum
        velocity = velocity + acceleration * dt;
        position = position + velocity * dt;
    }

    public void restart() {
        velocity = 0.0;
        position = 0.0;
    }
}