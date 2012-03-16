// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * $D vector, with similar functionality to ImmutableVector3F
 *
 * @author Jonathan Olson
 */
public class ImmutableVector4F {
    private float x;
    private float y;
    private float z;
    private float w;

    // public instances so we don't need to duplicate these
    public static final ImmutableVector4F ZERO = new ImmutableVector4F();
    public static final ImmutableVector4F X_UNIT = new ImmutableVector4F( 1, 0, 0, 0 );
    public static final ImmutableVector4F Y_UNIT = new ImmutableVector4F( 0, 1, 0, 0 );
    public static final ImmutableVector4F Z_UNIT = new ImmutableVector4F( 0, 0, 1, 0 );
    public static final ImmutableVector4F W_UNIT = new ImmutableVector4F( 0, 0, 0, 1 );

    public ImmutableVector4F() {
        this( 0, 0, 0 );
    }

    public ImmutableVector4F( ImmutableVector3F v ) {
        this( v.getX(), v.getY(), v.getZ() );
    }

    // default to w == 1
    public ImmutableVector4F( float x, float y, float z ) {
        this( x, y, z, 1 );
    }

    public ImmutableVector4F( float x, float y, float z, float w ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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

    public float getW() {
        return w;
    }

    public ImmutableVector4F plus( ImmutableVector4F v ) {
        return new ImmutableVector4F( x + v.x, y + v.y, z + v.z, w + v.w );
    }

    public ImmutableVector4F minus( ImmutableVector4F v ) {
        return new ImmutableVector4F( x - v.x, y - v.y, z - v.z, w - v.w );
    }

    public ImmutableVector4F times( float s ) {
        return new ImmutableVector4F( x * s, y * s, z * s, w * s );
    }

    // component-wise multiplication
    public ImmutableVector4F componentTimes( ImmutableVector4F v ) {
        return new ImmutableVector4F( x * v.x, y * v.y, z * v.z, w * v.w );
    }

    public float magnitude() {
        return (float) Math.sqrt( x * x + y * y + z * z + w * w );
    }

    // ignore the w component
    public ImmutableVector3F to3F() {
        return new ImmutableVector3F( x, y, z );
    }

    public ImmutableVector4F normalized() {
        float mag = magnitude();
        return new ImmutableVector4F( x / mag, y / mag, z / mag, w / mag );
    }

    public ImmutableVector4F negated() {
        return new ImmutableVector4F( -x, -y, -z, -w );
    }

    public float dot( ImmutableVector4F v ) {
        return x * v.x + y * v.y + z * v.z + w * v.w;
    }

    // The angle between this vector and "v", in radians
    public float angleBetween( ImmutableVector4F v ) {
        return (float) Math.acos( MathUtil.clamp( -1, normalized().dot( v.normalized() ), 1 ) );
    }

    // The angle between this vector and "v", in degrees
    public float angleBetweenInDegrees( ImmutableVector4F v ) {
        return angleBetween( v ) * 180 / (float) Math.PI;
    }

    @Override public String toString() {
        return "ImmutableVector4F[" + x + "," + y + "," + z + "," + w + "]";
    }

    @Override public int hashCode() {
        return Double.toHexString( x ).hashCode() + 31 * ( Double.toHexString( y ).hashCode() + 31 * ( Double.toHexString( z ).hashCode() + 31 * Double.toHexString( w ).hashCode() ) );
    }

    @Override public boolean equals( Object obj ) {
        // equality broken if we decide to compare instances of this class with subclasses that override equals
        if ( obj instanceof ImmutableVector4F ) {
            ImmutableVector4F v = (ImmutableVector4F) obj;
            return x == v.x && y == v.y && z == v.z;
        }
        else {
            return false;
        }
    }
}
