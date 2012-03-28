// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public class TerrainSample {
    private float elevation;
    private ImmutableVector2F textureCoordinates;
    private float randomElevationOffset = 0;

    public TerrainSample( float elevation, ImmutableVector2F textureCoordinates ) {
        this.elevation = elevation;
        this.textureCoordinates = textureCoordinates;
    }

    public void shiftWithTexture( ImmutableVector2F offset, TextureStrategy textureStrategy ) {
        setElevation( getElevation() + offset.y );
        setTextureCoordinates( getTextureCoordinates().plus( textureStrategy.mapTopDelta( offset ) ) );
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

    public float getRandomElevationOffset() {
        return randomElevationOffset;
    }

    public void setRandomElevationOffset( float randomElevationOffset ) {
        this.randomElevationOffset = randomElevationOffset;
    }
}
