/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:45:47 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class Timer extends ModelElement {
    double time = 0;
    private double timerScale;

    public Timer(double timerScale) {
        this.timerScale = timerScale;
    }

    public void stepInTime(double dt) {
        time += dt * timerScale;
        updateObservers();
    }

    public double getTime() {
        return time;
    }

    public void reset() {
        this.time = 0;
        updateObservers();
    }

    public void setTime(double time) {
        this.time = time;
        updateObservers();
    }


}
