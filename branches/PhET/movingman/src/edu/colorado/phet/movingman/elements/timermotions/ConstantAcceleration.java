package edu.colorado.phet.movingman.elements.timermotions;

import edu.colorado.phet.movingman.elements.TimerMotion;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 3, 2003
 * Time: 11:44:57 PM
 * To change this template use Options | File Templates.
 */
public class ConstantAcceleration implements TimerMotion {
    double x0;
    private double acceleration;

    public ConstantAcceleration(double x0, double acceleration) {
        this.x0 = x0;
        this.acceleration = acceleration;
    }

    public double getPosition(double time) {
        return .5 * acceleration * time * time + x0;
    }

    public void setInitialPosition(double x) {
        this.x0 = x;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

}
