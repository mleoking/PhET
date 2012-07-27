// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;

//REVIEW doc the semantics of "front" and "top"
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

    public Vector2F mapTop( Vector2F position ) {
        return position.times( getTopScale() );
    }

    public Vector2F mapTopDelta( Vector2F vector ) {
        return vector.times( getTopScale() );
    }

    public Vector2F mapFront( Vector2F position ) {
        return position.times( getFrontScale() );
    }

    public Vector2F mapFrontDelta( Vector2F vector ) {
        return vector.times( getFrontScale() );
    }
}
