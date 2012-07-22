package edu.colorado.phet.common.phetcommon.math.vector;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 3D vector, with similar functionality to Vector2F
 *
 * @author Jonathan Olson
 */
public @EqualsAndHashCode(callSuper = false) @ToString class Vector3F extends AbstractVector3F {
    public final float x;
    public final float y;
    public final float z;

    // public instances so we don't need to duplicate these
    public static final Vector3F ZERO = new Vector3F();
    public static final Vector3F X_UNIT = new Vector3F( 1, 0, 0 );
    public static final Vector3F Y_UNIT = new Vector3F( 0, 1, 0 );
    public static final Vector3F Z_UNIT = new Vector3F( 0, 0, 1 );

    public Vector3F() { this( 0, 0, 0 ); }

    public Vector3F( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3F( double x, double y, double z ) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    @Override public float getX() { return x; }

    @Override public float getY() { return y; }

    @Override public float getZ() { return z; }
}
