// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;

/**
 * A location with specific temp/density information that can move over time.
 */
public class Sample {
    private Vector3F position;
    private float temperature;
    private float density;
    private Vector2F textureCoordinates;

    // records the random elevation offset that was used at this point
    private float randomTerrainOffset = 0;

    public Sample( Vector3F position, float temperature, float density, Vector2F textureCoordinates ) {
        this.position = position;
        this.temperature = temperature;
        this.density = density;
        this.textureCoordinates = textureCoordinates;
    }

    // moves both the position AND the texture coordinates, using the offset to modify both
    public void shiftWithTexture( Vector3F offset, TextureStrategy textureStrategy ) {
        // change the position
        setPosition( getPosition().plus( offset ) );

        // and the relevant texture coordinates
        setTextureCoordinates( getTextureCoordinates().plus(
                textureStrategy.mapFrontDelta( new Vector2F( offset.x, offset.y ) ) ) );
    }

    public Vector3F getPosition() {
        return position;
    }

    public void setPosition( Vector3F position ) {
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

    public Vector2F getTextureCoordinates() {
        return textureCoordinates;
    }

    public void setTextureCoordinates( Vector2F textureCoordinates ) {
        this.textureCoordinates = textureCoordinates;
    }

    public float getRandomTerrainOffset() {
        return randomTerrainOffset;
    }

    public void setRandomTerrainOffset( float randomTerrainOffset ) {
        this.randomTerrainOffset = randomTerrainOffset;
    }
}
