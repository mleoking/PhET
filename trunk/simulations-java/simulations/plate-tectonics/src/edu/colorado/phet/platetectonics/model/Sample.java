// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

/**
 * A location with specific temp/density information that can move over time.
 */
public class Sample {
    private ImmutableVector3F position;
    private float temperature;
    private float density;
    private ImmutableVector2F textureCoordinates;

    // records the random elevation offset that was used at this point
    private float randomTerrainOffset = 0;

    public Sample( ImmutableVector3F position, float temperature, float density, ImmutableVector2F textureCoordinates ) {
        this.position = position;
        this.temperature = temperature;
        this.density = density;
        this.textureCoordinates = textureCoordinates;
    }

    // moves both the position AND the texture coordinates, using the offset to modify both
    public void shiftWithTexture( ImmutableVector3F offset, TextureStrategy textureStrategy ) {
        // change the position
        setPosition( getPosition().plus( offset ) );

        // and the relevant texture coordinates
        setTextureCoordinates( getTextureCoordinates().plus(
                textureStrategy.mapFrontDelta( new ImmutableVector2F( offset.x, offset.y ) ) ) );
    }

    public ImmutableVector3F getPosition() {
        return position;
    }

    public void setPosition( ImmutableVector3F position ) {
        this.position = position;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature( float temperature ) {
        this.temperature = temperature;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity( float density ) {
        this.density = density;
    }

    public ImmutableVector2F getTextureCoordinates() {
        return textureCoordinates;
    }

    public void setTextureCoordinates( ImmutableVector2F textureCoordinates ) {
        this.textureCoordinates = textureCoordinates;
    }

    public float getRandomTerrainOffset() {
        return randomTerrainOffset;
    }

    public void setRandomTerrainOffset( float randomTerrainOffset ) {
        this.randomTerrainOffset = randomTerrainOffset;
    }
}
