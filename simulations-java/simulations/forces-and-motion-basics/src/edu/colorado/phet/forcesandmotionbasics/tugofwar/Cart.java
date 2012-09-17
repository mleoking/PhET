// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

/**
 * @author Sam Reid
 */
public class Cart {
    private static final double CART_WEIGHT = 1;
    private double velocity = 0;
    private double position = 0;

    public Cart() {
    }

    public double getPosition() {
        return position;
    }

    public void stepInTime( final double dt, double force ) {
        double acceleration = force / CART_WEIGHT;

        velocity = velocity + acceleration * dt;
        position = position + velocity * dt;
    }
}