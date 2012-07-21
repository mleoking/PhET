// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;

/**
 * Heuristic material that is somewhat of a combination of the temperature and density views. It tries to keep the lightness/darkness
 * for temperature, and hue for temperature.
 */
public class CombinedMaterial implements EarthMaterial {
    private static final Color min = new Color( 64, 64, 64 );
    private static final Color max = new Color( 255, 64, 64 );

    public Color getColor( float density, float temperature, Vector2F position, float alpha ) {
        float tempValue = TemperatureMaterial.temperatureMap( temperature ).x;
        float densityValue = DensityMaterial.densityMap( density ).x;

        // HSV
        float hue = 0;
        float sat = tempValue;
        float value = densityValue / 2 + 0.5f;

        float c = value * sat;

        float m = value - c;

        return new Color( c + m, m, m, alpha );
    }

    public Color getMinColor() {
        return min;
    }

    public Color getMaxColor() {
        return max;
    }
}
