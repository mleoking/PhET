/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.model;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 9:33:07 PM
 * Copyright (c) May 18, 2003 by Sam Reid
 */
public class Message extends ModelElement {
    private MessageData m;
    double speed;

    public Message( MessageData m, double speed ) {
        this.m = m;
        this.speed = speed;
    }

    public void stepInTime( double dt ) {
        double x = m.getX();
        if( x < 100 ) {
            x = 100;
        }
        else if( x > 400 ) {
            x = 100;
        }
        else {
            x += speed * dt;
        }
        m.setX( x );
        setChanged();
        notifyObservers();
//        double y=Math.sin(x/10);
//        y*=100;
//        y+=200;
//        m.setY((int)y);
    }

    public void setY( double y ) {
        m.setY( y );
        setChanged();
        notifyObservers();
    }

    public double getY() {
        return m.getY();
    }

    public double getX() {
        return m.getX();
    }
}
