// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Abstract base class for vector2d classes (mutable and immutable), see #3374
 *
 * @author Sam Reid
 */
public abstract class AbstractVector2D implements Serializable {

    //Test for equality
    @Override public boolean equals( Object obj ) {
        boolean result = true;
        if ( this.getClass() != obj.getClass() ) {
            result = false;
        }
        else {
            AbstractVector2D that = (AbstractVector2D) obj;
            result = this.getX() == that.getX() && this.getY() == that.getY();
        }
        return result;
    }

    @Override public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    public abstract double getY();

    public abstract double getX();

    public double getMagnitudeSq() {
        return getX() * getX() + getY() * getY();
    }

    public double getMagnitude() {
        return Math.sqrt( getMagnitudeSq() );
    }

    public double dot( AbstractVector2D v ) {
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
        return Math.atan2( getY(), getX() );
    }

    public Point2D.Double toPoint2D() {
        return new Point2D.Double( getX(), getY() );
    }

    /**
     * Gets the distance between the tip of this vector and the specified vector.
     * Performance is important here since this is in the inner loop in a many-particle calculation in sugar and salt solutions: WaterModel
     *
     * @param v the vector to get the distance to
     * @return the cartesian distance between the vectors
     */
    public double getDistance( AbstractVector2D v ) {
        double dx = this.getX() - v.getX();
        double dy = this.getY() - v.getY();
        return Math.sqrt( dx * dx + dy * dy );
    }

    public double getDistance( Point2D point ) {
        return getDistance( new ImmutableVector2D( point ) );
    }
}