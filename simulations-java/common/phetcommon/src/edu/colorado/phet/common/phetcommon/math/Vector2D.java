package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * Vector2D represents an offset in (x,y) cartesian coordinates.  This class has all the functionality of the parent
 * class (i.e. functions that do not change the state of this vector2D and return an ImmutableVector2D) as well as
 * mutator functions.
 *
 * @author Sam Reid
 * @author Ron LeMaster
 */
public class Vector2D extends ImmutableVector2D {
    public Vector2D() {
    }

    public Vector2D( ImmutableVector2D v ) {
        this( v.getX(), v.getY() );
    }

    public Vector2D( double x, double y ) {
        super( x, y );
    }

    public Vector2D( Point2D p ) {
        super( p );
    }

    public Vector2D( Point2D src, Point2D dst ) {
        super( src, dst );
    }

    public Vector2D add( ImmutableVector2D v ) {
        setX( getX() + v.getX() );
        setY( getY() + v.getY() );
        return this;
    }

    public Vector2D normalize() {
        double length = getMagnitude();
        if ( length == 0 ) {
            throw new RuntimeException( "Cannot normalize a zero-magnitude vector." );
        }
        return scale( 1.0 / length );
    }

    public Vector2D scale( double scale ) {
        setX( getX() * scale );
        setY( getY() * scale );
        return this;
    }

    public void setX( double x ) {
        super.setX( x );
    }

    public void setY( double y ) {
        super.setY( y );
    }

    public void setComponents( double x, double y ) {
        super.setComponents( x, y );
    }

    public void setMagnitudeAndAngle( double magnitude, double angle ) {
        setComponents( Math.cos( angle ) * magnitude, Math.sin( angle ) * magnitude );
    }

    public Vector2D subtract( ImmutableVector2D that ) {
        setX( getX() - that.getX() );
        setY( getY() - that.getY() );
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

    public String toString() {
        return "Vector2D.Double[" + getX() + ", " + getY() + "]";
    }
}