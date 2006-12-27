/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.measuringtape;

import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Sep 16, 2003
 * Time: 10:01:06 PM
 * Copyright (c) Sep 16, 2003 by Sam Reid
 */
public class MeasuringTape extends SimpleObservable {
    double x0;
    double y0;
    double x1;
    double y1;

    public MeasuringTape( double x0, double y0, double x1, double y1 ) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public void translate( double dx, double dy ) {
        x0 += dx;
        y0 += dy;
        x1 += dx;
        y1 += dy;
        updateObservers();
    }

    public void translateSource( double dx, double dy ) {
        x0 += dx;
        y0 += dy;
        updateObservers();
    }

    public void translateDestination( double dx, double dy ) {
        x1 += dx;
        y1 += dy;
        updateObservers();
    }

    public double getAngle() {
        return Math.atan2( y1 - y0, x1 - x0 );
    }

    public double getMagnitude() {
        double a = x1 - x0;
        double b = y1 - y0;
        return Math.sqrt( a * a + b * b );
    }

    public double getStartX() {
        return x0;
    }

    public double getStartY() {
        return y0;
    }

    public double getEndX() {
        return x1;
    }

    public double getEndY() {
        return y1;
    }

}
