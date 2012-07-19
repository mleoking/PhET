// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.*;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public class CombinedMaterial implements EarthMaterial {
    private static final int width = 256;
    private static final int height = 256;

    private static final Color min = new Color( 64, 64, 64 );
    private static final Color max = new Color( 255, 64, 64 );

    public Color getColor( float density, float temperature, ImmutableVector2F position, float alpha ) {
        float tempValue = TemperatureMaterial.temperatureMap( temperature ).x;
        float densityValue = DensityMaterial.densityMap( density ).x;

        // HSV
        float hue = 0;
        float sat = tempValue;
        float value = densityValue / 2 + 0.5f;

        float c = value * sat;

        float m = value - c;

        return new Color( c + m, m, m, alpha );

//        return new Color( 0.25f + 0.75f * value, 0.25f, 0.25f, 1f );
    }

    public Color getMinColor() {
        return min;
    }

    public Color getMaxColor() {
        return max;
    }
}
