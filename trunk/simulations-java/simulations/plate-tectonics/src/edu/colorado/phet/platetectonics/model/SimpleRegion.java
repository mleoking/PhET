// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

/**
 * A region that has a well defined top and bottom
 */
public abstract class SimpleRegion extends Region {

    private final ImmutableVector2F[] top;
    private final ImmutableVector2F[] bottom;

    protected SimpleRegion( Type type, ImmutableVector2F[] top, ImmutableVector2F[] bottom ) {
        super( type );
        this.top = top;
        this.bottom = bottom;
    }

    @Override public ImmutableVector2F[] getBoundary() {
        int numVertices = top.length + bottom.length;
        ImmutableVector2F[] vertices = new ImmutableVector2F[numVertices];
        System.arraycopy( top, 0, vertices, 0, top.length );
        for ( int i = 0; i < bottom.length; i++ ) {
            vertices[numVertices - i - 1] = bottom[i];
        }
        return vertices;
    }
}
