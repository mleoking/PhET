// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.util.immutable;

import lombok.Data;

import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Immutable vector 2D.  Provides a convenient and consistent way of accessing x & y, for use in the immutable models in this sim.
 * Uses Lombok to generate equals/hash code for use in other immutable objects.
 *
 * @author Sam Reid
 */
@Data public class Vector2D {
    public final double x;
    public final double y;
    public static final Vector2D ZERO = new Vector2D( 0, 0 );

    //Have to provide required args constructor because we provide auxiliary constructors
    public Vector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public Vector2D( Vector2D v ) {this( v.x, v.y );}

    public Vector2D( Point2D p ) { this( p.getX(), p.getY() ); }

    public Vector2D( ImmutableVector2D p ) { this( p.getX(), p.getY() ); }

    public Vector2D( Point2D end, Point2D start ) { this( end.getX() - start.getX(), end.getY() - start.getY() ); }

    public Vector2D times( double s ) { return new Vector2D( x * s, y * s ); }

    public ImmutableVector2D toImmutableVector2D() { return new ImmutableVector2D( x, y ); }

    public Vector2D plus( double dx, double dy ) { return new Vector2D( x + dx, y + dy ); }

    public Vector2D plus( Vector2D v ) { return new Vector2D( x + v.x, y + v.y ); }

    public Vector2D minus( Vector2D v ) { return new Vector2D( x - v.x, y - v.y ); }

    public Vector2D getInstanceOfMagnitude( double magnitude ) { return times( magnitude / getMagnitude() ); }

    public double getMagnitude() { return Math.sqrt( x * x + y * y ); }

    public double distance( Vector2D v ) {
        double dx = this.x - v.x;
        double dy = this.y - v.y;
        return Math.sqrt( dx * dx + dy * dy );
    }

    public Vector2D plus( Dimension2D delta ) { return new Vector2D( x + delta.getWidth(), y + delta.getHeight() ); }

    public Line2D.Double lineTo( final double x, final double y ) { return new Line2D.Double( this.x, this.y, x, y ); }

    public Line2D.Double lineTo( final Vector2D end ) { return lineTo( end.x, end.y ); }

    public Point2D toPoint2D() { return new Point2D.Double( x, y ); }

    public UnitVector2D normalize() { return new UnitVector2D( this ); }

    public Vector2D rotate( final double angle ) { return polar( getMagnitude(), getAngle() + angle ); }

    //Use this method if you need to be very concise
    public static Vector2D v( double x, double y ) { return new Vector2D( x, y ); }

    //Returns the angle of the vector. The angle will be between -pi and pi.
    public double getAngle() { return Math.atan2( y, x ); }

    public static Vector2D polar( double radius, double angle ) { return new Vector2D( cos( angle ) * radius, sin( angle ) * radius ); }

    //A unit vector
    public static final class UnitVector2D extends Vector2D {

        //Private so we can guaranteed that it is only constructed as a unit vector
        public UnitVector2D( Vector2D vector2D ) {
            super( vector2D.getInstanceOfMagnitude( 1.0 ) );
        }

        public UnitVector2D( final double x, final double y ) {
            this( new Vector2D( x, y ) );
        }
    }
}