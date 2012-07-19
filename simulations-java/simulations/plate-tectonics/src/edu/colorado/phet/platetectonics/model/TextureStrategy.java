// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

/**
 * Basically a coordinate transform for texture coordinates
 */
public class TextureStrategy {
    private final float frontScale;

    public TextureStrategy( float frontScale ) {
        this.frontScale = frontScale;
    }

    public float getFrontScale() {
        return frontScale;
    }

    public float getTopScale() {
        return frontScale * 0.25f;
    }

    public ImmutableVector2F mapTop( ImmutableVector2F position ) {
        return position.times( getTopScale() );
    }

    public ImmutableVector2F mapTopDelta( ImmutableVector2F vector ) {
        return vector.times( getTopScale() );
    }

    public ImmutableVector2F mapFront( ImmutableVector2F position ) {
        return position.times( getFrontScale() );
    }

    public ImmutableVector2F mapFrontDelta( ImmutableVector2F vector ) {
        return vector.times( getFrontScale() );
    }
}
