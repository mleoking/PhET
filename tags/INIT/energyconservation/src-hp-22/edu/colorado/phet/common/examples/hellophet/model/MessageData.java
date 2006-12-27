/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.model;


/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 9:26:32 PM
 * Copyright (c) May 18, 2003 by Sam Reid
 */
public class MessageData {
    double x;
    private double y;

    public MessageData( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }
}
