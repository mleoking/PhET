/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:45:47 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class Timer extends ModelElement {
    private double time = 0;
    private String name;
    private double timerScale;

    public Timer( String name, double timerScale ) {
        this.name = name;
        this.timerScale = timerScale;
    }

    public void stepInTime( double dt ) {
        time += dt * timerScale;
//        System.out.println( "this = " + this+", stepping in time" );
        updateObservers();
    }

    public String toString() {
        return "Timer, name=" + name;
    }

    public double getTime() {
        return time;
    }

    public void reset() {
        this.time = 0;
        updateObservers();
    }

    public void setTime( double time ) {
        this.time = time;
        updateObservers();
    }


}
