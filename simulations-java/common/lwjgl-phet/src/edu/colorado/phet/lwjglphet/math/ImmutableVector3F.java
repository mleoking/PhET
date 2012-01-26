// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * 3D vector, with similar functionality to ImmutableVector2F
 *
 * @author Jonathan Olson
 */
public class ImmutableVector3F {
    // treat these as immutable, however in certain circumstances performance may dictate otherwise
    public float x;
    public float y;
    public float z;

    // public instances so we don't need to duplicate these
    public static final ImmutableVector3F ZERO = new ImmutableVector3F();
    public static final ImmutableVector3F X_UNIT = new ImmutableVector3F( 1, 0, 0 );
    public static final ImmutableVector3F Y_UNIT = new ImmutableVector3F( 0, 1, 0 );
    public static final ImmutableVector3F Z_UNIT = new ImmutableVector3F( 0, 0, 1 );

    public ImmutableVector3F() {
        this( 0, 0, 0 );
    }

    public ImmutableVector3F( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public ImmutableVector3F plus( ImmutableVector3F v ) {
        return new ImmutableVector3F( x + v.x, y + v.y, z + v.z );
    }

    public ImmutableVector3F minus( ImmutableVector3F v ) {
        return new ImmutableVector3F( x - v.x, y - v.y, z - v.z );
    }

    public ImmutableVector3F times( float s ) {
        return new ImmutableVector3F( x * s, y * s, z * s );
    }

    // component-wise multiplication
    public ImmutableVector3F componentTimes( ImmutableVector3F v ) {
        return new ImmutableVector3F( x * v.x, y * v.y, z * v.z );
    }

    public float magnitude() {
        return (float) Math.sqrt( x * x + y * y + z * z );
    }

    public float magnitudeSquared() {
        return x * x + y * y + z * z;
    }

    public ImmutableVector3F normalized() {
        float mag = magnitude();
        return new ImmutableVector3F( x / mag, y / mag, z / mag );
    }

    public ImmutableVector3F negated() {
        return new ImmutableVector3F( -x, -y, -z );
    }

    // Cross product
    public ImmutableVector3F cross( ImmutableVector3F v ) {
        return new ImmutableVector3F(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public float dot( ImmutableVector3F v ) {
        return x * v.x + y * v.y + z * v.z;
    }

    // The angle between this vector and "v", in radians
    public float angleBetween( ImmutableVector3F v ) {
        return (float) Math.acos( MathUtil.clamp( -1, normalized().dot( v.normalized() ), 1 ) );
    }

    // The angle between this vector and "v", in degrees
    public float angleBetweenInDegrees( ImmutableVector3F v ) {
        return angleBetween( v ) * 180 / (float) Math.PI;
    }

    @Override public String toString() {
        return "ImmutableVector3d[" + x + "," + y + "," + z + "]";
    }

    @Override public int hashCode() {
        return Double.toHexString( x ).hashCode() + 31 * ( Double.toHexString( y ).hashCode() + 31 * Double.toHexString( z ).hashCode() );
    }

    @Override public boolean equals( Object obj ) {
        // equality broken if we decide to compare instances of this class with subclasses that override equals
        if ( obj instanceof ImmutableVector3F ) {
            ImmutableVector3F v = (ImmutableVector3F) obj;
            return x == v.x && y == v.y && z == v.z;
        }
        else {
            return false;
        }
    }
}
