/**
 * Class: ImmutableVector2D
 * Package: edu.colorado.phet.common.math
 * Author: Another Guy
 * Date: May 20, 2004
 */
package edu.colorado.phet.common.math;

import java.awt.geom.Point2D;

public interface ImmutableVector2D {
    ImmutableVector2D getAddedInstance( ImmutableVector2D v );

    ImmutableVector2D getSubtractedInstance( ImmutableVector2D v );

    ImmutableVector2D getAddedInstance( double x, double y );

    ImmutableVector2D getSubtractedInstance( double x, double y );

    ImmutableVector2D getScaledInstance( double scale );

    ImmutableVector2D getNormalVector();

    ImmutableVector2D getNormalizedInstance();

    double getY();

    double getX();

    double getMagnitudeSq();

    double getMagnitude();

    double dot( ImmutableVector2D v );

    double getAngle();

    ImmutableVector2D getInstanceOfMagnitude( double magnitude );

    Point2D toPoint2D();

    double getCrossProductScalar( ImmutableVector2D v );

    public class Double implements ImmutableVector2D {
        private double x;
        private double y;

        public Double() {
        }

        public Double( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public Double( Vector2D v ) {
            this( v.getX(), v.getY() );
        }

        public Double( ImmutableVector2D v ) {
            this( v.getX(), v.getY() );
        }

        public Double( Point2D p ) {
            this( p.getX(), p.getY() );
        }

        public boolean equals( Object obj ) {
            boolean result = true;
            if( this.getClass() != obj.getClass() ) {
                result = false;
            }
            else {
                ImmutableVector2D that = (ImmutableVector2D)obj;
                result = this.getX() == that.getX() && this.getY() == that.getY();
            }
            return result;
        }

        public String toString() {
            return "ImmutableVector2D.Double[" + x + ", " + y + "]";
        }

        public ImmutableVector2D getAddedInstance( ImmutableVector2D v ) {
            return getAddedInstance( v.getX(), v.getY() );
        }

        public ImmutableVector2D getAddedInstance( double x, double y ) {
            return new Double( getX() + x, getY() + y );
        }

        public ImmutableVector2D getScaledInstance( double scale ) {
            return new Double( getX() * scale, getY() * scale );
        }

        public ImmutableVector2D getNormalVector() {
            return new Double( y, -x );
        }

        public ImmutableVector2D getNormalizedInstance() {
            double mag = getMagnitude();
            return new ImmutableVector2D.Double( getX() / mag, getY() / mag );
        }

        public ImmutableVector2D getSubtractedInstance( double x, double y ) {
            return new Double( getX() - x, getY() - y );
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

        public static ImmutableVector2D parseAngleAndMagnitude( double r, double angle ) {
            ImmutableVector2D vector = new ImmutableVector2D.Double( Math.cos( angle ), Math.sin( angle ) );
            return vector.getScaledInstance( r );
        }
    }


    public class Float implements ImmutableVector2D {
        private float x;
        private float y;

        public Float() {
        }

        public Float( float x, float y ) {
            this.x = x;
            this.y = y;
        }

        private Float( double x, double y ) {
            this.x = (float)x;
            this.y = (float)y;
        }

        public Float( Vector2D v ) {
            this( v.getX(), v.getY() );
        }

        public Float( ImmutableVector2D v ) {
            this( v.getX(), v.getY() );
        }

        public Float( Point2D p ) {
            this( p.getX(), p.getY() );
        }

        public boolean equals( Object obj ) {
            boolean result = true;
            if( this.getClass() != obj.getClass() ) {
                result = false;
            }
            else {
                ImmutableVector2D that = (ImmutableVector2D)obj;
                result = this.getX() == that.getX() && this.getY() == that.getY();
            }
            return result;
        }

        public String toString() {
            return "ImmutableVector2D.Float[" + x + ", " + y + "]";
        }

        public ImmutableVector2D getAddedInstance( ImmutableVector2D v ) {
            return getAddedInstance( (float)v.getX(), (float)v.getY() );
        }

        public ImmutableVector2D getAddedInstance( double x, double y ) {
            return new Float( getX() + x, getY() + y );
        }

        public ImmutableVector2D getScaledInstance( float scale ) {
            return new Float( getX() * scale, getY() * scale );
        }

        public ImmutableVector2D getNormalVector() {
            return new Float( y, -x );
        }

        public ImmutableVector2D getNormalizedInstance() {
            double mag = getMagnitude();
            return new ImmutableVector2D.Float( getX() / mag, getY() / mag );
        }

        public ImmutableVector2D getSubtractedInstance( double x, double y ) {
            return new Float( getX() - x, getY() - y );
        }

        public ImmutableVector2D getSubtractedInstance( ImmutableVector2D v ) {
            return getSubtractedInstance( (float)v.getX(), (float)v.getY() );
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

        protected void setX( float x ) {
            this.x = x;
        }

        protected void setY( float y ) {
            this.y = y;
        }

        public void setX( double x ) {
            this.x = (float)x;
        }

        public void setY( double y ) {
            this.y = (float)y;
        }


        public double dot( ImmutableVector2D that ) {
            double result = 0;
            result += this.getX() * that.getX();
            result += this.getY() * that.getY();
            return result;
        }

        public double getAngle() {
            return Math.atan2( y, x );
        }

        public ImmutableVector2D getScaledInstance( double scale ) {
            return new Float( getX() * scale, getY() * scale );
        }

        public ImmutableVector2D getInstanceOfMagnitude( double magnitude ) {
            return getScaledInstance( magnitude / getMagnitude() );
        }

        public Point2D toPoint2D() {
            return new Point2D.Float( x, y );
        }

        public double getCrossProductScalar( ImmutableVector2D v ) {
            return ( this.getMagnitude() * v.getMagnitude() * Math.sin( this.getAngle() - v.getAngle() ) );
        }

        public static ImmutableVector2D parseAngleAndMagnitude( double r, double angle ) {
            ImmutableVector2D vector = new ImmutableVector2D.Float( Math.cos( angle ), Math.sin( angle ) );
            return vector.getScaledInstance( r );
        }
    }
}
