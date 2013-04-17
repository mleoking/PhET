package edu.colorado.phet.common.phetcommon.math.vector;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 3D vector, with similar functionality to Vector2D
 *
 * @author Jonathan Olson
 */
public @EqualsAndHashCode(callSuper = false) @ToString class Vector3D extends AbstractVector3D {
    public final double x;
    public final double y;
    public final double z;

    // public instances so we don't need to duplicate these
    public static final Vector3D ZERO = new Vector3D();
    public static final Vector3D X_UNIT = new Vector3D( 1, 0, 0 );
    public static final Vector3D Y_UNIT = new Vector3D( 0, 1, 0 );
    public static final Vector3D Z_UNIT = new Vector3D( 0, 0, 1 );

    public Vector3D() { this( 0, 0, 0 ); }

    public Vector3D( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override public double getX() { return x; }

    @Override public double getY() { return y; }

    @Override public double getZ() { return z; }
}
