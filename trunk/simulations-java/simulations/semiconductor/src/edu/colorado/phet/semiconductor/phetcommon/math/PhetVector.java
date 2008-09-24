/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * Represents an immutable 2-D vector.
 */
public class PhetVector {
    double x;
    double y;

    public PhetVector() {
        this( 0, 0 );
    }

    public PhetVector( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public PhetVector( Point2D pt ) {
        this( pt.getX(), pt.getY() );
    }

    public String toString() {
        return "x=" + x + ", y=" + y;
    }

    public PhetVector getAddedInstance( double x, double y ) {
        return new PhetVector( this.x + x, this.y + y );
    }

    public PhetVector getAddedInstance( PhetVector pv ) {
        return new PhetVector( this.x + pv.x, this.y + pv.y );
    }

    public boolean equals( Object obj ) {
        if ( !( obj instanceof PhetVector ) ) {
            return false;
        }
        PhetVector b = (PhetVector) obj;
        return b.getX() == getX() && b.getY() == getY();
    }

    public PhetVector getScaledInstance( double scale ) {
        return new PhetVector( x * scale, y * scale );
    }

    public PhetVector getInstanceForMagnitude( double magnitude ) {
        double currentMag = getMagnitude();
        return getScaledInstance( magnitude / currentMag );
    }

    public double getMagnitude() {
        return Math.sqrt( x * x + y * y );
    }

    public PhetVector getNormalizedInstance() {
        double mag = getMagnitude();
        return new PhetVector( x / mag, y / mag );
    }

    public double getAngle() {
        return Math.atan2( y, x );
    }

    public PhetVector getNormalVector() {
        double ang = getAngle() + Math.PI / 2;
        return new PhetVector( Math.cos( ang ), Math.sin( ang ) );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public PhetVector getSubtractedInstance( PhetVector vector ) {
        return new PhetVector( this.x - vector.x, this.y - vector.y );
    }

    public PhetVector getSubtractedInstance( double x, double y ) {
        return new PhetVector( this.x - x, this.y - y );
    }

    public Point2D.Double toPoint2D() {
        return new Point2D.Double( x, y );
    }

}
