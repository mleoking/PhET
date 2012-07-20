// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import lombok.Data;

import java.awt.geom.Point2D;

/**
 * Vector2D represents an offset in (x,y) Cartesian coordinates.  This class has all the functionality of the parent
 * class (i.e. functions that do not change the state of this Vector2D and return an ImmutableVector2D) as well as
 * mutator functions.  Uses Lombok to generate equals/hashcode/toString.
 *
 * @author Sam Reid
 * @author Ron LeMaster
 */
public @Data class MutableVector2D extends AbstractVector2D {
    private double x;
    private double y;

    public MutableVector2D() {}

    public MutableVector2D( AbstractVector2D v ) { this( v.getX(), v.getY() ); }

    public MutableVector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public MutableVector2D( Point2D p ) { this( p.getX(), p.getY() ); }

    public MutableVector2D( Point2D src, Point2D dst ) {
        this.x = dst.getX() - src.getX();
        this.y = dst.getY() - src.getY();
    }

    public MutableVector2D add( AbstractVector2D v ) {
        setX( getX() + v.getX() );
        setY( getY() + v.getY() );
        return this;
    }

    public MutableVector2D normalize() {
        double magnitude = getMagnitude();
        if ( magnitude == 0 ) {
            throw new UnsupportedOperationException( "Cannot normalize a zero-magnitude vector." );
        }
        return scale( 1.0 / magnitude );
    }

    public MutableVector2D scale( double scale ) {
        setX( getX() * scale );
        setY( getY() * scale );
        return this;
    }

    public void setX( double x ) { this.x = x; }

    public void setY( double y ) { this.y = y; }

    // The reason that this seemingly redundant override exists is to make
    // this method public.
    public void setComponents( double x, double y ) {
        setX( x );
        setY( y );
    }

    public void setValue( Vector2D value ) { setComponents( value.getX(), value.getY() ); }

    public void setMagnitudeAndAngle( double magnitude, double angle ) { setComponents( Math.cos( angle ) * magnitude, Math.sin( angle ) * magnitude ); }

    public void setMagnitude( double magnitude ) { setMagnitudeAndAngle( magnitude, getAngle() ); }

    public void setAngle( double angle ) { setMagnitudeAndAngle( getMagnitude(), angle ); }

    public MutableVector2D subtract( AbstractVector2D v ) {
        setX( getX() - v.getX() );
        setY( getY() - v.getY() );
        return this;
    }

    public MutableVector2D rotate( double theta ) {
        double r = getMagnitude();
        double alpha = getAngle();
        double gamma = alpha + theta;
        double xPrime = r * Math.cos( gamma );
        double yPrime = r * Math.sin( gamma );
        this.setComponents( xPrime, yPrime );
        return this;
    }

    @Override public double getY() { return y; }

    @Override public double getX() { return x; }

    public static Vector2D createPolar( final double magnitude, final double angle ) { return Vector2D.createPolar( magnitude, angle ); }

    public static void main( String[] args ) {
        MutableVector2D v = new MutableVector2D( 0, 0 );
        System.out.println( "v = " + v );
        System.out.println( "v.hashCode() = " + v.hashCode() );
        MutableVector2D b = new MutableVector2D( 1, 2 );
        MutableVector2D c = new MutableVector2D( 0, 0 );
        System.out.println( "v.equals( b ) = " + v.equals( b ) + " (should be false)" );
        System.out.println( "v.equals( c ) = " + v.equals( c ) + " (should be true)" );
    }
}