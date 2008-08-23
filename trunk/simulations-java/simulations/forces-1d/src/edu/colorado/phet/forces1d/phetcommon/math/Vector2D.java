/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.forces1d.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * Vector2D
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface Vector2D extends AbstractVector2D {

    Vector2D add( AbstractVector2D v );

    Vector2D scale( double scale );

    Vector2D subtract( AbstractVector2D that );

    Vector2D rotate( double theta );

    void setX( double x );

    void setY( double y );

    void setComponents( double x, double y );

    Vector2D normalize();

    public static class Double extends AbstractVector2D.Double implements Vector2D {
        public Double() {
        }

        public Double( Vector2D v ) {
            this( v.getX(), v.getY() );
        }

        public Double( double x, double y ) {
            super( x, y );
        }

        public Double( Point2D p ) {
            super( p );
        }

        public Double( Point2D src, Point2D dst ) {
            super( src, dst );
        }

        public Vector2D add( AbstractVector2D v ) {
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
            setX( x );
            setY( y );
        }

        public Vector2D subtract( AbstractVector2D that ) {
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

    public static class Float extends AbstractVector2D.Float implements Vector2D {

        public Float() {
        }

        public Float( Vector2D.Double v ) {
            this( (float) v.getX(), (float) v.getY() );
        }

        public Float( float x, float y ) {
            super( x, y );
        }

        public Float( double x, double y ) {
            super( (float) x, (float) y );
        }

        public Float( Point2D src, Point2D dst ) {
            super( src, dst );
        }

        public Vector2D add( AbstractVector2D v ) {
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

        public void setX( float x ) {
            super.setX( x );
        }

        public void setY( float y ) {
            super.setY( y );
        }

        public void setX( double x ) {
            super.setX( x );
        }

        public void setY( double y ) {
            super.setY( y );
        }

        public void setComponents( float x, float y ) {
            setX( x );
            setY( y );
        }

        public void setComponents( double x, double y ) {
            setX( x );
            setY( y );
        }

        public Vector2D subtract( AbstractVector2D that ) {
            setX( getX() - that.getX() );
            setY( getY() - that.getY() );
            return this;
        }

        public Vector2D rotate( double theta ) {
            double r = getMagnitude();
            double alpha = getAngle();
            double gamma = alpha + theta;
            double xPrime = r * Math.sin( gamma );
            double yPrime = r * Math.cos( gamma );
            this.setComponents( xPrime, yPrime );
            return this;
        }

        public String toString() {
            return "Vector2D.Double[" + getX() + ", " + getY() + "]";
        }
    }

}
