/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements.stepmotions;

import edu.colorado.phet.movingman.elements.Man;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 3:42:58 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public class OscMotion implements StepMotion {
    double k;//spring constant.
    double center = 0;
    double initialVelocity = 1;
    MotionState motionState;
    private double amplitude = 2;

    public OscMotion(MotionState motionState, double k) {
        this.motionState = motionState;
        this.k = k;
    }

    public double stepInTime(Man man, double dt) {
        if (Math.abs(man.getX()) < .01 && Math.abs(motionState.getVelocity()) < .01) {
            motionState.setVelocity(0);
            man.setX(amplitude);
        }
        //f=ma
        double acceleration = -k * (man.getX() - center);
        double vnew = motionState.getVelocity() + acceleration * dt;
        motionState.setVelocity(vnew);
        double xnew = man.getX() + vnew * dt;
        return xnew;
//        return 0;
    }

    public void setK(double k) {
        this.k = k;
    }
}
