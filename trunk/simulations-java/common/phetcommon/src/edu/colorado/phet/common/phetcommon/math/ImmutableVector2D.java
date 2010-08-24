package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;

/**
* Created by IntelliJ IDEA.
* User: Sam
* Date: Aug 24, 2010
* Time: 5:57:21 AM
* To change this template use File | Settings | File Templates.
*/
public class ImmutableVector2D{
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

    public String toString() {
        return "AbstractVector2D.Double[" + x + ", " + y + "]";
    }

    public ImmutableVector2D getAddedInstance( ImmutableVector2D v ) {
        return getAddedInstance( v.getX(), v.getY() );
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
        double mag = getMagnitude();
        if ( mag == 0 ) {
            throw new RuntimeException( "Cannot normalize a zero-magnitude vector." );
        }
        return new ImmutableVector2D( getX() / mag, getY() / mag );
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

    protected void setX( double x ) {
        this.x = x;
    }

    protected void setY( double y ) {
        this.y = y;
    }

    public double dot( ImmutableVector2D that ) {
        double result = 0;
        result += this.getX() * that.getX();
        result += this.getY() * that.getY();
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

    public Point2D toPoint2D() {
        return new Point2D.Double( x, y );
    }

    public double getCrossProductScalar( ImmutableVector2D v ) {
        return ( this.getMagnitude() * v.getMagnitude() * Math.sin( this.getAngle() - v.getAngle() ) );
    }

    public Point2D getDestination( Point2D startPt ) {
        return new Point2D.Double( startPt.getX() + getX(), startPt.getY() + getY() );
    }

    public ImmutableVector2D getRotatedInstance( double angle ) {
        return parseAngleAndMagnitude( getMagnitude(), getAngle() + angle );
    }

    public static ImmutableVector2D parseAngleAndMagnitude( double r, double angle ) {
        ImmutableVector2D vector = new ImmutableVector2D( Math.cos( angle ), Math.sin( angle ) );
        return vector.getScaledInstance( r );
    }
}
