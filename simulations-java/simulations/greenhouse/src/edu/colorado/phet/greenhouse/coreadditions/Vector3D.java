/**
 * Class: Vector3D
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 2, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions;

import edu.colorado.phet.greenhouse.phetcommon.math.PhysicalVector;
import edu.colorado.phet.greenhouse.phetcommon.math.Vector2D;

public class Vector3D extends PhysicalVector {

    public Vector3D() {
        this( 0, 0, 0 );
    }

    public Vector3D( Vector2D vector ) {
        this( vector.getX(), vector.getY(), 0 );
    }

    public Vector3D( Vector3D vector ) {
        this( vector.getX(), vector.getY(), vector.getZ() );
    }

    public Vector3D( float x, float y, float z ) {
        super( NUM_DIMENSIONS );
        this.setX( x );
        this.setY( y );
        this.setZ( z );
        if ( Double.isNaN( x ) )
        //throw new RuntimeException( "x was NaN" );
        {
            System.out.println( "Vector2D constructor: x was NaN" );
        }
        if ( Double.isNaN( y ) )
        //throw new RuntimeException( "Y was NaN" );
        {
            System.out.println( "Vector2D constructor: y was NaN" );
        }
    }

    public String toString() {
        java.util.Vector v = new java.util.Vector();
        v.add( new Double( this.getX() ) );
        v.add( new Double( this.getY() ) );
        v.add( new Double( this.getZ() ) );
        return v.toString();
    }

    public Vector3D setComponents( float x, float y, float z ) {
        this.setX( x );
        this.setY( y );
        this.setZ( z );
        return this;
    }

    ;

    public Vector3D setComponents( Vector3D that ) {
        this.setComponents( that.getX(), that.getY(), that.getZ() );
        return this;
    }

    ;

    public Vector3D setComponents( Vector2D that ) {
        this.setComponents( that.getX(), that.getY(), 0 );
        return this;
    }

    ;

    public float getX() {
        return getScalarAt( X );
    }

    public void setX( float x ) {
        setScalarAt( X, x );
    }

    public float getY() {
        return getScalarAt( Y );
    }

    public void setY( float y ) {
        setScalarAt( Y, y );
    }

    public float getZ() {
        return getScalarAt( Z );
    }

    public void setZ( float z ) {
        setScalarAt( Z, z );
    }

    public Vector3D add( Vector3D that ) {
        return (Vector3D) super.add( that, this );
    }

    public Vector3D normalize() {
        return (Vector3D) super.generalNormalize();
    }

    public Vector3D multiply( float scale ) {
        return (Vector3D) super.multiply( scale, this );
    }

    public Vector3D subtract( Vector3D that ) {
        return (Vector3D) super.subtract( that, this );
    }

    public Vector3D subtract( float x, float y, float z ) {
        Vector3D temp = new Vector3D( x, y, z );
        return this.subtract( temp );
    }

    public Vector3D normalVector() {
        if ( true ) {
            throw new RuntimeException( "not implemented" );
        }
        return new Vector3D( this.getY(), -this.getX(), this.getZ() );
    }

    public Vector3D crossProduct( Vector3D that ) {
        Vector3D result = new Vector3D(
                this.getY() * that.getZ() - this.getZ() * that.getY(),
                -this.getX() * that.getZ() + this.getZ() * that.getX(),
                this.getX() * that.getY() - this.getY() * that.getX() );
        return result;
    }

    //
    // Static fields and methods
    //
    private final static int X = 0;
    private final static int Y = 1;
    private final static int Z = 2;
    private final static int NUM_DIMENSIONS = 3;

}
