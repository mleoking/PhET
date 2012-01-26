// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

/**
 * A location with specific temp/density information, that can move over time
 */
public class SamplePoint {
    private ImmutableVector3F position;
    private float temperature;
    private float density;
    private ImmutableVector2F textureCoordinates;

    public SamplePoint( ImmutableVector3F position, float temperature, float density, ImmutableVector2F textureCoordinates ) {
        this.position = position;
        this.temperature = temperature;
        this.density = density;
        this.textureCoordinates = textureCoordinates;
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
}
