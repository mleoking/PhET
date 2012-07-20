// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

/**
 * ImmutableVector2D represents an (x,y) offset in Cartesian coordinates.
 * This class is immutable, which means that it cannot be modified.
 * There is a subclass Vector2D that adds mutable functionality.
 *
 * @author Ron LeMaster
 * @author Sam Reid
 */
public class ImmutableVector2D extends AbstractVector2D {
    public final double x;
    public final double y;

    //Immutable instance for zero so it doesn't need to be duplicated/re-instantiated in multiple places
    public static final ImmutableVector2D ZERO = new ImmutableVector2D();

    public ImmutableVector2D() {
        this( 0, 0 );
    }

    public ImmutableVector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public ImmutableVector2D( MutableVector2D v ) {
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

    /**
     * Create a new ImmutableVector2D based on the difference (final - initial) of the passed in vectors.
     * Note that the order of the arguments in (initial, final) even though the subtraction is (final - initial).
     * This is done for consistency with the pre-existing constructor ImmutableVector2D(Point2D,Point2D), and by the convention of passing in
     * the initial object first.
     *
     * @param initialPt starting point for the (final-initial) difference
     * @param finalPt   ending point for the (final-initial) difference
     */
    public ImmutableVector2D( ImmutableVector2D initialPt, ImmutableVector2D finalPt ) {
        this( finalPt.getX() - initialPt.getX(), finalPt.getY() - initialPt.getY() );
    }

    public ImmutableVector2D( Dimension2D v ) {
        this( v.getWidth(), v.getHeight() );
    }

    @Override public double getY() {
        return y;
    }

    @Override public double getX() {
        return x;
    }

    public static ImmutableVector2D createPolar( double radius, double angle ) {
        ImmutableVector2D vector = new ImmutableVector2D( Math.cos( angle ), Math.sin( angle ) );
        return vector.getScaledInstance( radius );
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

    public ImmutableVector2D negate() {
        return getScaledInstance( -1 );
    }

    public static void main( String[] args ) {
        System.out.println( new ImmutableVector2D( 1, 2 ) );
        System.out.println( new MutableVector2D( 1, 2 ) );
        System.out.println( new MutableVector2D( 1, 2 ) {{
            setX( 3 );
        }} );
        System.out.println( "0= " + new ImmutableVector2D( 0, 0 ).getDistance( new ImmutableVector2D( 0, 0 ) ) );
        System.out.println( "1= " + new ImmutableVector2D( 1, 0 ).getDistance( new ImmutableVector2D( 0, 0 ) ) );
        System.out.println( "2root2= " + new ImmutableVector2D( 0, 0 ).getDistance( new ImmutableVector2D( 1, 1 ) ) );
    }
}