/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.model.AutomatedObservable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:25:22 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class Man extends AutomatedObservable {
    private double x;
    private double x0;
    private boolean grabbed = false;
    private double velocity;

    public Man( double x ) {
        this.x0 = x;
        this.x = x;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity( double velocity ) {
        this.velocity = velocity;
        System.out.println( "SET velocity = " + velocity );
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void setGrabbed( boolean grabbed ) {
        this.grabbed = grabbed;
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
        updateObservers();
    }

    public void reset() {
        this.x = x0;
        setVelocity( 0 );
        updateObservers();
    }
}
