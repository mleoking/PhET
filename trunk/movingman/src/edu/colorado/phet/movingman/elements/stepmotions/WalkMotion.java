/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements.stepmotions;

import edu.colorado.phet.movingman.elements.Man;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 3:38:26 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public class WalkMotion implements StepMotion {
    double vel = 0;
    MotionState ms;
//    boolean started=false;

    public WalkMotion(MotionState ms) {
        this.ms = ms;
    }

    public double stepInTime(Man man, double dt) {
        double newPosition = man.getX() + vel * dt;
//        started=true;
        ms.setVelocity(vel);
        return newPosition;
    }

    public void setVelocity(double vel) {
        this.vel = vel;
    }
}
