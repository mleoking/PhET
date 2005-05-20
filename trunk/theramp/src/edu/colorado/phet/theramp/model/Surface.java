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

public abstract class Surface extends SimpleObservable {
    private double angle;
    private double x0;
    private double y0;
    private double length;

    public Surface( double angle, double length ) {
        this( angle, length, 0, 0 );
    }

    public Surface( double angle, double length, double x0, double y0 ) {
        this.angle = angle;
        this.x0 = x0;
        this.y0 = y0;
        this.length = length;
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

    public double getHeight() {
        return length * Math.sin( angle );
    }

    public abstract Surface copyState();

    public void setState( Surface state ) {
        setAngle( state.angle );
        this.x0 = state.x0;
        this.y0 = state.y0;
        this.length = state.length;
        //todo notify observers
    }

    public abstract void applyBoundaryConditions( RampModel rampModel, Block block );
}
