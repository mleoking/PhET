/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements.stepmotions;

import edu.colorado.phet.movingman.elements.Man;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 3:37:53 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public class AccelMotion implements StepMotion {
    double accel = 0;
    MotionState motionState;

    public AccelMotion(MotionState motionState) {
        this.motionState = motionState;
    }

    public AccelMotion(MotionState motionState, double accel) {
        this.motionState = motionState;
        this.accel = accel;
    }

    public double stepInTime(Man man, double dt) {
        double velocity = motionState.getVelocity() + accel * dt;
        motionState.setVelocity(velocity);
        double position = man.getX() + velocity * dt;
        return position;
    }

    public MotionState getMotionState() {
        return motionState;
    }

    public void setAcceleration(double accel) {
        this.accel = accel;
    }
}
