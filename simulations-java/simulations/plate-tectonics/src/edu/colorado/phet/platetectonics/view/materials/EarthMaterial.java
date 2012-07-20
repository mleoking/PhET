// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.*;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

/**
 * Base for a material (texture) for the cross-section of the Earth.
 */
public interface EarthMaterial {
    // main color based on density, temperature, etc.
    public Color getColor( float density, float temperature, ImmutableVector2F position, float alpha );

    // min/max colors used for the legend
    public Color getMinColor();

    public Color getMaxColor();
}
