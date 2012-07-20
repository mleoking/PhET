// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.math;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;

/**
 * ImmutableVector2F represents an (x,y) offset in Cartesian coordinates.
 * ImmutableVector2F is meant to be a higher-performance and more float-compatible version
 * of ImmutableVector2D
 * This class is immutable, which means that it cannot be modified.
 *
 * @author Ron LeMaster
 * @author Sam Reid
 */
public class ImmutableVector2F implements Serializable {
    // treated as immutable, but public for performance needs and convenience with getters
    public float x;
    public float y;

    //Immutable instance for zero so it doesn't need to be duplicated/re-instantiated in multiple places
    public static final ImmutableVector2F ZERO = new ImmutableVector2F();

    public static final ImmutableVector2F X_UNIT = new ImmutableVector2F( 1, 0 );
    public static final ImmutableVector2F Y_UNIT = new ImmutableVector2F( 0, 1 );

    public ImmutableVector2F() {
        this( 0, 0 );
    }

    public ImmutableVector2F( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    public ImmutableVector2F( double x, double y ) {
        this( (float) x, (float) y );
    }

    public ImmutableVector2F( ImmutableVector2F v ) {
        this( v.getX(), v.getY() );
    }

    public ImmutableVector2F( Point2D p ) {
        this( (float) p.getX(), (float) p.getY() );
    }

    public ImmutableVector2F( Point2D initialPt, Point2D finalPt ) {
        this( (float) ( finalPt.getX() - initialPt.getX() ), (float) ( finalPt.getY() - initialPt.getY() ) );
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
    public ImmutableVector2F( ImmutableVector2F initialPt, ImmutableVector2F finalPt ) {
        this( finalPt.getX() - initialPt.getX(), finalPt.getY() - initialPt.getY() );
    }

    public ImmutableVector2F( Dimension2D v ) {
        this( (float) v.getWidth(), (float) v.getHeight() );
    }

    @Override
    public boolean equals( Object obj ) {
        boolean result = true;
        if ( this.getClass() != obj.getClass() ) {
            result = false;
        }
        else {
            ImmutableVector2F that = (ImmutableVector2F) obj;
            result = this.getX() == that.getX() && this.getY() == that.getY();
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll( ".*\\.", "" ) + "[" + x + ", " + y + "]";
    }

    public ImmutableVector2F getAddedInstance( ImmutableVector2F v ) {
        return getAddedInstance( v.getX(), v.getY() );
    }

    public ImmutableVector2F getAddedInstance( Dimension2D delta ) {
        return getAddedInstance( (float) delta.getWidth(), (float) delta.getHeight() );
    }

    public ImmutableVector2F getAddedInstance( float x, float y ) {
        return new ImmutableVector2F( getX() + x, getY() + y );
    }

    public ImmutableVector2F getScaledInstance( float scale ) {
        return new ImmutableVector2F( getX() * scale, getY() * scale );
    }

    public ImmutableVector2F getNormalVector() {
        return new ImmutableVector2F( y, -x );
    }

    public ImmutableVector2F getNormalizedInstance() {
        float magnitude = getMagnitude();
        if ( magnitude == 0 ) {
            throw new UnsupportedOperationException( "Cannot normalize a zero-magnitude vector." );
        }
        return new ImmutableVector2F( getX() / magnitude, getY() / magnitude );
    }

    public ImmutableVector2F getSubtractedInstance( float x, float y ) {
        return new ImmutableVector2F( getX() - x, getY() - y );
    }

    public ImmutableVector2F getSubtractedInstance( ImmutableVector2F v ) {
        return getSubtractedInstance( v.getX(), v.getY() );
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public float getMagnitudeSq() {
        return getX() * getX() + getY() * getY();
    }

    public float getMagnitude() {
        return (float) Math.sqrt( getMagnitudeSq() );
    }

    public ImmutableVector2F normalized() {
        return times( 1 / getMagnitude() );
    }

    //The following setter methods are protected so that clients of ImmutableVector2D won't be able to mutate the object
    //But so that subclasses such as Vector2D (which is mutable) will be able to change the data without reallocating objects, while
    //sharing code.

    protected void setComponents( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    protected void setX( float x ) {
        this.x = x;
    }

    protected void setY( float y ) {
        this.y = y;
    }

    public float dot( ImmutableVector2F v ) {
        float result = 0;
        result += this.getX() * v.getX();
        result += this.getY() * v.getY();
        return result;
    }

    /**
     * Returns the angle of the vector. The angle will be between -pi and pi.
     *
     * @return the angle of the vector
     */
    public float getAngle() {
        return (float) Math.atan2( y, x );
    }

    public ImmutableVector2F getInstanceOfMagnitude( float magnitude ) {
        return getScaledInstance( magnitude / getMagnitude() );
    }

    public Point2D.Double toPoint2D() {
        return new Point2D.Double( x, y );
    }

    public float getCrossProductScalar( ImmutableVector2F v ) {
        return (float) ( this.getMagnitude() * v.getMagnitude() * Math.sin( this.getAngle() - v.getAngle() ) );
    }

    public Point2D.Double getDestination( Point2D startPt ) {
        return new Point2D.Double( startPt.getX() + getX(), startPt.getY() + getY() );
    }

    public ImmutableVector2F getRotatedInstance( float angle ) {
        return createPolar( getMagnitude(), getAngle() + angle );
    }

    /**
     * Gets the distance between the tip of this vector and the specified vector.
     * Performance is important here since this is in the inner loop in a many-particle calculation in sugar and salt solutions: WaterModel
     *
     * @param v the vector to get the distance to
     * @return the cartesian distance between the vectors
     */
    public float getDistance( ImmutableVector2F v ) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        return (float) Math.sqrt( dx * dx + dy * dy );
    }

    public float getDistance( Point2D point ) {
        return getSubtractedInstance( (float) point.getX(), (float) point.getY() ).getMagnitude();
    }

    public static ImmutableVector2F createPolar( float radius, float angle ) {
        ImmutableVector2F vector = new ImmutableVector2F( (float) Math.cos( angle ), (float) Math.sin( angle ) );
        return vector.getScaledInstance( radius );
    }

    public ImmutableVector2F plus( float x, float y ) {
        return getAddedInstance( x, y );
    }

    public ImmutableVector2F plus( ImmutableVector2F v ) {
        return getAddedInstance( v );
    }

    public ImmutableVector2F plus( Dimension2D v ) {
        return getAddedInstance( v );
    }

    public ImmutableVector2F minus( ImmutableVector2F v ) {
        return getSubtractedInstance( v );
    }

    public ImmutableVector2F minus( float x, float y ) {
        return getSubtractedInstance( x, y );
    }

    public ImmutableVector2F times( float scale ) {
        return getScaledInstance( scale );
    }

    public static void main( String[] args ) {
        System.out.println( new ImmutableVector2F( 1, 2 ) );
        System.out.println( new MutableVector2D( 1, 2 ) );
        System.out.println( new MutableVector2D( 1, 2 ) {{
            setX( 3 );
        }} );
        System.out.println( "0= " + new ImmutableVector2F( 0, 0 ).getDistance( new ImmutableVector2F( 0, 0 ) ) );
        System.out.println( "1= " + new ImmutableVector2F( 1, 0 ).getDistance( new ImmutableVector2F( 0, 0 ) ) );
        System.out.println( "2root2= " + new ImmutableVector2F( 0, 0 ).getDistance( new ImmutableVector2F( 1, 1 ) ) );
    }

    public ImmutableVector2F negate() {
        return getScaledInstance( -1 );
    }

    //Transform this ImmutableVector2D into a Dimension2D
    public Dimension2D toDimension() {
        return new Dimension2DDouble( x, y );
    }
}