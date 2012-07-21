// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;

/**
 * A location on the surface of the earth with a specific elevation (and texture coordinates). X and Y values will be determined in the
 * Terrain object.
 */
public class TerrainSample {
    private float elevation;
    private Vector2F textureCoordinates;
    private float randomElevationOffset = 0;

    public TerrainSample( float elevation, Vector2F textureCoordinates ) {
        this.elevation = elevation;
        this.textureCoordinates = textureCoordinates;
    }

    public void shiftWithTexture( Vector2F offset, TextureStrategy textureStrategy ) {
        setElevation( getElevation() + offset.y );
        setTextureCoordinates( getTextureCoordinates().plus( textureStrategy.mapTopDelta( offset ) ) );
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation( float elevation ) {
        this.elevation = elevation;
    }

    public Vector2F getTextureCoordinates() {
        return textureCoordinates;
    }

    public void setTextureCoordinates( Vector2F textureCoordinates ) {
        this.textureCoordinates = textureCoordinates;
    }

    public float getRandomElevationOffset() {
        return randomElevationOffset;
    }

    public void setRandomElevationOffset( float randomElevationOffset ) {
        this.randomElevationOffset = randomElevationOffset;
    }
}
