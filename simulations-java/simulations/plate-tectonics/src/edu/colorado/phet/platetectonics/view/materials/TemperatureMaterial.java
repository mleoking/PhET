// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.CrustModel;

/**
 * Displays gray for "cool" and red for "warm", with a blend in-between
 */
public class TemperatureMaterial implements EarthMaterial {
    private static final Color min = new Color( 64, 64, 64 );
    private static final Color max = new Color( 255, 64, 64 );

    public static ImmutableVector2F temperatureMap( float temperature ) {
        float minTemp = CrustModel.ZERO_CELSIUS;
        float maxMaxTemp = CrustModel.ZERO_CELSIUS + 6100 + 300;

        float x = ( temperature - minTemp ) / ( maxMaxTemp - minTemp );
        x = (float) Math.pow( x, 0.4f );
        x = (float) MathUtil.clamp( 0.08, x, 0.95 );
        return new ImmutableVector2F( x, 0.5f );
    }

    public Color getColor( float density, float temperature, ImmutableVector2F position, float alpha ) {
        float value = temperatureMap( temperature ).x;
        return new Color( 0.25f + 0.75f * value, 0.25f, 0.25f, alpha );
    }

    public Color getMinColor() {
        return min;
    }

    public Color getMaxColor() {
        return max;
    }
}
