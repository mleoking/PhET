// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.*;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public interface EarthMaterial {
    public Color getColor( float density, float temperature, ImmutableVector2F position, float alpha );

    public Color getMinColor();

    public Color getMaxColor();
}
