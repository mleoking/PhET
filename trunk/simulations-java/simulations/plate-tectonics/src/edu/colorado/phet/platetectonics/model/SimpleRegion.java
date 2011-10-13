// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import com.jme3.math.Vector2f;

/**
 * A region that has a well defined top and bottom
 */
public abstract class SimpleRegion extends Region {

    private final Vector2f[] top;
    private final Vector2f[] bottom;

    protected SimpleRegion( Type type, Vector2f[] top, Vector2f[] bottom ) {
        super( type );
        this.top = top;
        this.bottom = bottom;
    }

    @Override public Vector2f[] getBoundary() {
        int numVertices = top.length + bottom.length;
        Vector2f[] vertices = new Vector2f[numVertices];
        System.arraycopy( top, 0, vertices, 0, top.length );
        for ( int i = 0; i < bottom.length; i++ ) {
            vertices[numVertices - i - 1] = bottom[i];
        }
        return vertices;
    }
}
