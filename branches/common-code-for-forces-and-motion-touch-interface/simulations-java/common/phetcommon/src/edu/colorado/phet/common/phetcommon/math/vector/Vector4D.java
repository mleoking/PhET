// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math.vector;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 4D vector, with similar functionality to Vector3D
 *
 * @author Jonathan Olson
 */
public @EqualsAndHashCode(callSuper = false) @ToString class Vector4D extends AbstractVector4D {
    public final double x;
    public final double y;
    public final double z;
    public final double w;

    // public instances so we don't need to duplicate these
    public static final Vector4D ZERO = new Vector4D();
    public static final Vector4D X_UNIT = new Vector4D( 1, 0, 0, 0 );
    public static final Vector4D Y_UNIT = new Vector4D( 0, 1, 0, 0 );
    public static final Vector4D Z_UNIT = new Vector4D( 0, 0, 1, 0 );
    public static final Vector4D W_UNIT = new Vector4D( 0, 0, 0, 1 );

    public Vector4D() {
        this( 0, 0, 0 );
    }

    public Vector4D( Vector3D v ) {
        this( v.getX(), v.getY(), v.getZ() );
    }

    // default to w == 1
    public Vector4D( double x, double y, double z ) {
        this( x, y, z, 1 );
    }

    public Vector4D( double x, double y, double z, double w ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override public double getX() {
        return x;
    }

    @Override public double getY() {
        return y;
    }

    @Override public double getZ() {
        return z;
    }

    @Override public double getW() {
        return w;
    }
}
