/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:12:53 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class Ramp extends SimpleObservable {
    double angle;
    double x0;
    double y0;
    double length;

    public Ramp() {
        angle = Math.PI / 16;
        x0 = 0;
        y0 = 0;
        length = 15;
    }

    public Point2D getOrigin() {
        return new Point2D.Double( x0, y0 );
    }

    public double getLength() {
        return length;
    }

    public Point2D getLocation( double distAlongRamp ) {
        AbstractVector2D vector = Vector2D.Double.parseAngleAndMagnitude( distAlongRamp, angle );
        return vector.getDestination( getOrigin() );
    }

    public Point2D getEndPoint() {
        return getLocation( length );
    }

    public void setAngle( double angle ) {
        this.angle = angle;
        notifyObservers();
    }

    public void setLength( double length ) {
        this.length = length;
        notifyObservers();
    }

    public double getAngle() {
        return angle;
    }
}
