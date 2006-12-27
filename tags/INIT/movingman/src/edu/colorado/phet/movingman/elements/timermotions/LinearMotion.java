package edu.colorado.phet.movingman.elements.timermotions;

import edu.colorado.phet.movingman.elements.TimerMotion;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 3, 2003
 * Time: 11:44:57 PM
 * To change this template use Options | File Templates.
 */
public class LinearMotion implements TimerMotion {
    double x0;
    private double speed;

    public LinearMotion(double x0, double speed) {
        this.x0 = x0;
        this.speed = speed;
    }

    public double getPosition(double time) {
        return speed * time + x0;
    }

    public void setInitialPosition(double x) {
        this.x0 = x;
    }

    public void setVelocity(double v) {
        this.speed = v;
    }

}