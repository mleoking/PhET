/**
 * Class: ImmutableVector2D
 * Package: edu.colorado.phet.common.math
 * Author: Another Guy
 * Date: May 20, 2004
 */
package edu.colorado.phet.common.math;

public interface Vector2D extends ImmutableVector2D {
    Vector2D add( ImmutableVector2D v );

    Vector2D scale( double scale );

    Vector2D subtract( ImmutableVector2D that );

    void setX( double x );

    void setY( double y );

    void setCoordinates( double x, double y );

    Vector2D normalize();

    public static class Double extends ImmutableVector2D.Double implements Vector2D {
        public Double() {
        }

        public Double( double x, double y ) {
            super( x, y );
        }

        public Vector2D add( ImmutableVector2D v ) {
            setX( getX() + v.getX() );
            setY( getY() + v.getY() );
            return this;
        }

        public Vector2D normalize() {
            double length = getMagnitude();
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

        public void setCoordinates( double x, double y ) {
            setX( x );
            setY( y );
        }

        public Vector2D subtract( ImmutableVector2D that ) {
            setX( getX() - that.getX() );
            setY( getY() - that.getY() );
            return this;
        }

        public String toString() {
            return "Vector2D.Double[" + getX() + ", " + getY() + "]";
        }
    }

}
