// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public class TerrainSamplePoint {
    private float elevation;
    private ImmutableVector2F textureCoordinates;

    public TerrainSamplePoint( float elevation, ImmutableVector2F textureCoordinates ) {
        this.elevation = elevation;
        this.textureCoordinates = textureCoordinates;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation( float elevation ) {
        this.elevation = elevation;
    }

    public ImmutableVector2F getTextureCoordinates() {
        return textureCoordinates;
    }

    public void setTextureCoordinates( ImmutableVector2F textureCoordinates ) {
        this.textureCoordinates = textureCoordinates;
    }
}
