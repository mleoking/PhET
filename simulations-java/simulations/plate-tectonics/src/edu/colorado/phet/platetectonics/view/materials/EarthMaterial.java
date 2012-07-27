// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;

/**
 * Base for a material (texture) for the cross-section of the Earth.
 */
public interface EarthMaterial {
    // main color based on density, temperature, etc.
    public Color getColor( float density, float temperature, Vector2F position, float alpha );

    //REVIEW define semantics of min and max colors in the base class context (I see what it means for specific subclasses, but only after investigating)
    // min/max colors used for the legend
    public Color getMinColor();

    public Color getMaxColor();
}
