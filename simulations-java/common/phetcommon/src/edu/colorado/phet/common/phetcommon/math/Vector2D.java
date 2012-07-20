// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * Vector2D represents an offset in (x,y) Cartesian coordinates.  This class has all the functionality of the parent
 * class (i.e. functions that do not change the state of this Vector2D and return an ImmutableVector2D) as well as
 * mutator functions.
 *
 * @author Sam Reid
 * @author Ron LeMaster
 */
public class Vector2D extends AbstractVector2D {
    private double x;
    private double y;

    public Vector2D() {
    }

    public Vector2D( AbstractVector2D v ) {
        this( v.getX(), v.getY() );
    }

    public Vector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public Vector2D( Point2D p ) {
        this( p.getX(), p.getY() );
    }

    public Vector2D( Point2D src, Point2D dst ) {
        this.x = dst.getX() - src.getX();
        this.y = dst.getY() - src.getY();
    }

    public Vector2D add( AbstractVector2D v ) {
        setX( getX() + v.getX() );
        setY( getY() + v.getY() );
        return this;
    }

    public Vector2D normalize() {
        double magnitude = getMagnitude();
        if ( magnitude == 0 ) {
            throw new UnsupportedOperationException( "Cannot normalize a zero-magnitude vector." );
        }
        return scale( 1.0 / magnitude );
    }

    public Vector2D scale( double scale ) {
        setX( getX() * scale );
        setY( getY() * scale );
        return this;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public void setY( double y ) {
        this.y = y;
    }

    // The reason that this seemingly redundant override exists is to make
    // this method public.
    public void setComponents( double x, double y ) {
        setX( x );
        setY( y );
    }

    public void setValue( ImmutableVector2D value ) {
        setComponents( value.getX(), value.getY() );
    }

    public void setMagnitudeAndAngle( double magnitude, double angle ) {
        setComponents( Math.cos( angle ) * magnitude, Math.sin( angle ) * magnitude );
    }

    public void setMagnitude( double magnitude ) {
        setMagnitudeAndAngle( magnitude, getAngle() );
    }

    public void setAngle( double angle ) {
        setMagnitudeAndAngle( getMagnitude(), angle );
    }

    public Vector2D subtract( AbstractVector2D v ) {
        setX( getX() - v.getX() );
        setY( getY() - v.getY() );
        return this;
    }

    public Vector2D rotate( double theta ) {
        double r = getMagnitude();
        double alpha = getAngle();
        double gamma = alpha + theta;
        double xPrime = r * Math.cos( gamma );
        double yPrime = r * Math.sin( gamma );
        this.setComponents( xPrime, yPrime );
        return this;
    }

    @Override public double getY() {
        return y;
    }

    @Override public double getX() {
        return x;
    }

    public static ImmutableVector2D createPolar( final double magnitude, final double angle ) {
        return ImmutableVector2D.createPolar( magnitude, angle );
    }
}