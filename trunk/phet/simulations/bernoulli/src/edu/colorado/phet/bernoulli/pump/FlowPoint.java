package edu.colorado.phet.bernoulli.pump;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 9:53:22 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class FlowPoint {
    double pressure;
    double density;
    double velocity;
    double gravity; //P+pgh+1/2 p v^2 = constant.
    double height;

    public double getEnergy() {
        double speed = Math.abs( velocity );
        return pressure + density * gravity * height + .5 * density * speed * speed;
    }
}
