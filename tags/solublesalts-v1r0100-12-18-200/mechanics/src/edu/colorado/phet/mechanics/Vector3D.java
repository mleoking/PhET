/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mechanics;

import edu.colorado.phet.common.math.Vector2D;

/**
 * Vector3D
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
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

    public Vector3D( double x, double y, double z ) {
        super( NUM_DIMENSIONS );
        this.setX( x );
        this.setY( y );
        this.setZ( z );
        if( Double.isNaN( x ) )
        //throw new RuntimeException( "x was NaN" );
        {
            System.out.println( "Vector2D constructor: x was NaN" );
        }
        if( Double.isNaN( y ) )
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

    public Vector3D setComponents( double x, double y, double z ) {
        this.setX( x );
        this.setY( y );
        this.setZ( z );
        return this;
    };

    public Vector3D setComponents( Vector3D that ) {
        this.setComponents( that.getX(), that.getY(), that.getZ() );
        return this;
    };

    public double getX() {
        return getScalarAt( X );
    }

    public void setX( double x ) {
        setScalarAt( X, x );
    }

    public double getY() {
        return getScalarAt( Y );
    }

    public void setY( double y ) {
        setScalarAt( Y, y );
    }

    public double getZ() {
        return getScalarAt( Z );
    }

    public void setZ( double z ) {
        setScalarAt( Z, z );
    }

    public Vector3D add( Vector3D that ) {
        return (Vector3D)super.add( that, this );
    }

    public Vector3D normalize() {
        return (Vector3D)super.generalNormalize();
    }

    public Vector3D multiply( double scale ) {
        return (Vector3D)super.multiply( scale, this );
    }

    public Vector3D subtract( Vector3D that ) {
        return (Vector3D)super.subtract( that, this );
    }

    public Vector3D subtract( double x, double y, double z ) {
        Vector3D temp = new Vector3D( x, y, z );
        return this.subtract( temp );
    }

    public Vector3D normalVector() {
        if( true ) {
            throw new RuntimeException( "not implemented" );
        }
        return new Vector3D( this.getY(), -this.getX(), this.getZ() );
    }

    public Vector3D crossProduct( Vector3D that ) {
        Vector3D result = new Vector3D( this.getY() * that.getZ() - this.getZ() * that.getY(),
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
