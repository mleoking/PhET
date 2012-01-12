package edu.colorado.phet.common.phetcommon.math;

/**
 * 3D vector, with similar functionality to ImmutableVector2D
 *
 * @author Jonathan Olson
 */
public class ImmutableVector3D {
    private double x;
    private double y;
    private double z;

    // public instances so we don't need to duplicate these
    public static final ImmutableVector3D ZERO = new ImmutableVector3D();
    public static final ImmutableVector3D X_UNIT = new ImmutableVector3D( 1, 0, 0 );
    public static final ImmutableVector3D Y_UNIT = new ImmutableVector3D( 0, 1, 0 );
    public static final ImmutableVector3D Z_UNIT = new ImmutableVector3D( 0, 0, 1 );

    public ImmutableVector3D() {
        this( 0, 0, 0 );
    }

    public ImmutableVector3D( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public ImmutableVector3D plus( ImmutableVector3D v ) {
        return new ImmutableVector3D( x + v.x, y + v.y, z + v.z );
    }

    public ImmutableVector3D minus( ImmutableVector3D v ) {
        return new ImmutableVector3D( x - v.x, y - v.y, z - v.z );
    }

    public ImmutableVector3D times( double s ) {
        return new ImmutableVector3D( x * s, y * s, z * s );
    }

    // component-wise multiplication
    public ImmutableVector3D componentTimes( ImmutableVector3D v ) {
        return new ImmutableVector3D( x * v.x, y * v.y, z * v.z );
    }

    public double magnitude() {
        return Math.sqrt( x * x + y * y + z * z );
    }

    public ImmutableVector3D normalized() {
        double mag = magnitude();
        return new ImmutableVector3D( x / mag, y / mag, z / mag );
    }

    public ImmutableVector3D negated() {
        return new ImmutableVector3D( -x, -y, -z );
    }

    // Cross product
    public ImmutableVector3D cross( ImmutableVector3D v ) {
        return new ImmutableVector3D(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public double dot( ImmutableVector3D v ) {
        return x * v.x + y * v.y + z * v.z;
    }

    // The angle between this vector and "v", in radians
    public double angleBetween( ImmutableVector3D v ) {
        return Math.acos( MathUtil.clamp( -1, normalized().dot( v.normalized() ), 1 ) );
    }

    // The angle between this vector and "v", in degrees
    public double angleBetweenInDegrees( ImmutableVector3D v ) {
        return angleBetween( v ) * 180 / Math.PI;
    }

    @Override public String toString() {
        return "ImmutableVector3d[" + x + "," + y + "," + z + "]";
    }

    @Override public int hashCode() {
        return Double.toHexString( x ).hashCode() + 31 * ( Double.toHexString( y ).hashCode() + 31 * Double.toHexString( z ).hashCode() );
    }

    @Override public boolean equals( Object obj ) {
        // equality broken if we decide to compare instances of this class with subclasses that override equals
        if ( obj instanceof ImmutableVector3D ) {
            ImmutableVector3D v = (ImmutableVector3D) obj;
            return x == v.x && y == v.y && z == v.z;
        }
        else {
            return false;
        }
    }
}
