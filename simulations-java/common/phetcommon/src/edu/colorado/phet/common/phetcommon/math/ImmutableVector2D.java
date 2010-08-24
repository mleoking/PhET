package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;

/**
* Created by IntelliJ IDEA.
* User: Sam
* Date: Aug 24, 2010
* Time: 5:57:21 AM
* To change this template use File | Settings | File Templates.
*/
public class ImmutableVector2D implements AbstractVector2DInterface {
    private double x;
    private double y;

    public ImmutableVector2D() {
        this( 0, 0 );
    }

    public ImmutableVector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public ImmutableVector2D( Vector2DInterface v ) {
        this( v.getX(), v.getY() );
    }

    public ImmutableVector2D( AbstractVector2DInterface v ) {
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
            AbstractVector2DInterface that = (AbstractVector2DInterface) obj;
            result = this.getX() == that.getX() && this.getY() == that.getY();
        }
        return result;
    }

    public String toString() {
        return "AbstractVector2D.Double[" + x + ", " + y + "]";
    }

    public AbstractVector2DInterface getAddedInstance( AbstractVector2DInterface v ) {
        return getAddedInstance( v.getX(), v.getY() );
    }

    public AbstractVector2DInterface getAddedInstance( double x, double y ) {
        return new ImmutableVector2D( getX() + x, getY() + y );
    }

    public AbstractVector2DInterface getScaledInstance( double scale ) {
        return new ImmutableVector2D( getX() * scale, getY() * scale );
    }

    public AbstractVector2DInterface getNormalVector() {
        return new ImmutableVector2D( y, -x );
    }

    public AbstractVector2DInterface getNormalizedInstance() {
        double mag = getMagnitude();
        if ( mag == 0 ) {
            throw new RuntimeException( "Cannot normalize a zero-magnitude vector." );
        }
        return new ImmutableVector2D( getX() / mag, getY() / mag );
    }

    public AbstractVector2DInterface getSubtractedInstance( double x, double y ) {
        return new ImmutableVector2D( getX() - x, getY() - y );
    }

    public AbstractVector2DInterface getSubtractedInstance( AbstractVector2DInterface v ) {
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

    public double dot( AbstractVector2DInterface that ) {
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

    public AbstractVector2DInterface getInstanceOfMagnitude( double magnitude ) {
        return getScaledInstance( magnitude / getMagnitude() );
    }

    public Point2D toPoint2D() {
        return new Point2D.Double( x, y );
    }

    public double getCrossProductScalar( AbstractVector2DInterface v ) {
        return ( this.getMagnitude() * v.getMagnitude() * Math.sin( this.getAngle() - v.getAngle() ) );
    }

    public Point2D getDestination( Point2D startPt ) {
        return new Point2D.Double( startPt.getX() + getX(), startPt.getY() + getY() );
    }

    public AbstractVector2DInterface getRotatedInstance( double angle ) {
        return parseAngleAndMagnitude( getMagnitude(), getAngle() + angle );
    }

    public static AbstractVector2DInterface parseAngleAndMagnitude( double r, double angle ) {
        AbstractVector2DInterface vector = new ImmutableVector2D( Math.cos( angle ), Math.sin( angle ) );
        return vector.getScaledInstance( r );
    }
}
