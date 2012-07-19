// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.CrustModel;

public class TemperatureMaterial implements EarthMaterial {
    private static final int width = 256;
    private static final int height = 256;

    private static final Color min = new Color( 64, 64, 64 );
    private static final Color max = new Color( 255, 64, 64 );

    public static ImmutableVector2F temperatureMap( float temperature ) {
        float minTemp = CrustModel.ZERO_CELSIUS;
        float maxTemp = 1500;
        float maxMaxTemp = CrustModel.ZERO_CELSIUS + 6100 + 300;

//        float tempRatio = ( temperature - minTemp ) / ( maxTemp - minTemp );
//        float x;
//        float constant = 1300;
//        if ( temperature <= 1300 ) {
//            x = 100f + ( 1f - tempRatio ) * 155f;
//        }
//        else {
//            float start = 100f + ( 1f - ( 1300 - minTemp ) / ( maxTemp - minTemp ) ) * 155f;
//            float end = 50f;
//            float ratio = ( temperature - 1300 ) / ( maxMaxTemp - 1300 );
//            x = start + ( end - start ) * ratio;
//        }
//        x = (float) MathUtil.clamp( 0.0, x / 255.0, 1.0 ); // clamp it in the normal range
        float x = ( temperature - minTemp ) / ( maxMaxTemp - minTemp );
        x = (float) Math.pow( x, 0.4f );
        x = (float) MathUtil.clamp( 0.08, x, 0.95 );
        return new ImmutableVector2F( x, 0.5f );

//        float minDensityToShow = 2500;
//        float maxDensityToShow = CrustModel.CENTER_DENSITY;
//        float v = 1 - ( density - minDensityToShow ) / ( maxDensityToShow - minDensityToShow );
//        return new ImmutableVector2F( v, 0.5f );
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
