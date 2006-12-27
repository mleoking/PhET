/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.physics2d.rotation;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 4:11:01 PM
 * Copyright (c) Jun 12, 2003 by Sam Reid
 */
public class ConstantTorque implements Torque {
    private double t;

    public ConstantTorque(double t) {
        this.t = t;
    }

    public double getTorque() {
        return t;
    }
}
