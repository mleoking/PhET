// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.Color;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public interface EarthMaterial {
    public ImmutableVector2F getTextureCoordinates( float density, float temperature, ImmutableVector2F position );

    public Color getMinColor();

    public Color getMaxColor();
}
