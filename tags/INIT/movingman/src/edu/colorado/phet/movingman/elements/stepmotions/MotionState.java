/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements.stepmotions;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 11:39:24 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public class MotionState {
    double velocity;
    boolean started = false;

    public double getVelocity() {
        return velocity;
    }

    public void reset() {
        started = false;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

}
