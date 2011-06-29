package edu.colorado.phet.moleculeshapes.model;

public class ImmutableVector3D {
    private double x;
    private double y;
    private double z;

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

    public ImmutableVector3D cross( ImmutableVector3D v ) {
        return new ImmutableVector3D(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }
}
