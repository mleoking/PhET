// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.util;

import com.jme3.math.Vector3f;

/**
 * Bounds, generalized to a 3D bounding box (cuboid)
 */
public class Bounds3D {
    private final float x;
    private final float y;
    private final float z;
    private final float width;
    private final float height;
    private final float depth;

    public Bounds3D( float x, float y, float z, float width, float height, float depth ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public static Bounds3D fromMinMax( float minX, float maxX, float minY, float maxY, float minZ, float maxZ ) {
        return new Bounds3D( minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ );
    }

    public float getDepth() {
        return depth;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
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

    public float getMinX() {
        return x;
    }

    public float getMinY() {
        return y;
    }

    public float getMinZ() {
        return z;
    }

    public float getMaxX() {
        return x + width;
    }

    public float getMaxY() {
        return y + height;
    }

    public float getMaxZ() {
        return z + depth;
    }

    public Vector3f getPosition() {
        return new Vector3f( x, y, z );
    }

    public Vector3f getExtent() {
        return new Vector3f( x + width, y + height, z + depth );
    }

    public Vector3f getCenter() {
        return getPosition().add( getExtent() ).mult( 0.5f );
    }
}
