// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * ImmutableVector2D represents an (x,y) offset in Cartesian coordinates.
 * This class is immutable, which means that it cannot be modified.
 * There is a subclass Vector2D that adds mutable functionality.
 *
 * @author Ron LeMaster
 * @author Sam Reid
 */
public class ImmutableVector2D implements Serializable {
    private double x;
    private double y;

    public ImmutableVector2D() {
        this( 0, 0 );
    }

    public ImmutableVector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public ImmutableVector2D( Vector2D v ) {
        this( v.getX(), v.getY() );
    }

    public ImmutableVector2D( ImmutableVector2D v ) {
        this( v.getX(), v.getY() );
    }

    public ImmutableVector2D( Point2D p ) {
        this( p.getX(), p.getY() );
    }

    public ImmutableVector2D( Point2D initialPt, Point2D finalPt ) {
        this( finalPt.getX() - initialPt.getX(), finalPt.getY() - initialPt.getY() );
    }

    public ImmutableVector2D( Dimension2D v ) {
        this( v.getWidth(), v.getHeight() );
    }

    @Override
    public boolean equals( Object obj ) {
        boolean result = true;
        if ( this.getClass() != obj.getClass() ) {
            result = false;
        }
        else {
            ImmutableVector2D that = (ImmutableVector2D) obj;
            result = this.getX() == that.getX() && this.getY() == that.getY();
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll( ".*\\.", "" ) + "[" + x + ", " + y + "]";
    }

    public ImmutableVector2D getAddedInstance( ImmutableVector2D v ) {
        return getAddedInstance( v.getX(), v.getY() );
    }

    public ImmutableVector2D getAddedInstance( Dimension2D delta ) {
        return getAddedInstance( delta.getWidth(), delta.getHeight() );
    }

    public ImmutableVector2D getAddedInstance( double x, double y ) {
        return new ImmutableVector2D( getX() + x, getY() + y );
    }

    public ImmutableVector2D getScaledInstance( double scale ) {
        return new ImmutableVector2D( getX() * scale, getY() * scale );
    }

    public ImmutableVector2D getNormalVector() {
        return new ImmutableVector2D( y, -x );
    }

    public ImmutableVector2D getNormalizedInstance() {
        double magnitude = getMagnitude();
        if ( magnitude == 0 ) {
            throw new UnsupportedOperationException( "Cannot normalize a zero-magnitude vector." );
        }
        return new ImmutableVector2D( getX() / magnitude, getY() / magnitude );
    }

    public ImmutableVector2D getSubtractedInstance( double x, double y ) {
        return new ImmutableVector2D( getX() - x, getY() - y );
    }

    public ImmutableVector2D getSubtractedInstance( ImmutableVector2D v ) {
        return getSubtractedInstance( v.getX(), v.getY() );
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public double getMagnitudeSq() {
        return getX() * getX() + getY() * getY();
    }

    public double getMagnitude() {
        return Math.sqrt( getMagnitudeSq() );
    }

    //The following setter methods are protected so that clients of ImmutableVector2D won't be able to mutate the object
    //But so that subclasses such as Vector2D (which is mutable) will be able to change the data without reallocating objects, while
    //sharing code.

    protected void setComponents( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    protected void setX( double x ) {
        this.x = x;
    }

    protected void setY( double y ) {
        this.y = y;
    }

    public double dot( ImmutableVector2D v ) {
        double result = 0;
        result += this.getX() * v.getX();
        result += this.getY() * v.getY();
        return result;
    }

    /**
     * Returns the angle of the vector. The angle will be between -pi and pi.
     *
     * @return the angle of the vector
     */
    public double getAngle() {
        return Math.atan2( y, x );
    }

    public ImmutableVector2D getInstanceOfMagnitude( double magnitude ) {
        return getScaledInstance( magnitude / getMagnitude() );
    }

    public Point2D.Double toPoint2D() {
        return new Point2D.Double( x, y );
    }

    public double getCrossProductScalar( ImmutableVector2D v ) {
        return ( this.getMagnitude() * v.getMagnitude() * Math.sin( this.getAngle() - v.getAngle() ) );
    }

    public Point2D.Double getDestination( Point2D startPt ) {
        return new Point2D.Double( startPt.getX() + getX(), startPt.getY() + getY() );
    }

    public ImmutableVector2D getRotatedInstance( double angle ) {
        return parseAngleAndMagnitude( getMagnitude(), getAngle() + angle );
    }

    public double getDistance( ImmutableVector2D immutableVector2D ) {
        return getSubtractedInstance( immutableVector2D ).getMagnitude();
    }

    public double getDistance( Point2D point ) {
        return getDistance( new ImmutableVector2D( point ) );
    }

    public static ImmutableVector2D parseAngleAndMagnitude( double r, double angle ) {
        ImmutableVector2D vector = new ImmutableVector2D( Math.cos( angle ), Math.sin( angle ) );
        return vector.getScaledInstance( r );
    }

    public ImmutableVector2D plus( double x, double y ) {
        return getAddedInstance( x, y );
    }

    public ImmutableVector2D plus( ImmutableVector2D v ) {
        return getAddedInstance( v );
    }

    public ImmutableVector2D plus( Dimension2D v ) {
        return getAddedInstance( v );
    }

    public ImmutableVector2D minus( ImmutableVector2D v ) {
        return getSubtractedInstance( v );
    }

    public ImmutableVector2D minus( double x, double y ) {
        return getSubtractedInstance( x, y );
    }

    public ImmutableVector2D times( double scale ) {
        return getScaledInstance( scale );
    }

    public static void main( String[] args ) {
        System.out.println( new ImmutableVector2D( 1, 2 ) );
        System.out.println( new Vector2D( 1, 2 ) );
        System.out.println( new Vector2D( 1, 2 ) {{
            setX( 3 );
        }} );
    }

    public ImmutableVector2D negate() {
        return getScaledInstance( -1 );
    }
}
