// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * @author Sam Reid
 */
public class MediumColorFactory {
    //Maps index of refraction to color
    public static Color getColor( double indexOfRefractionForRed ) {
        if ( indexOfRefractionForRed < BendingLightModel.WATER.index() ) {
            double ratio = new Function.LinearFunction( 1.0, BendingLightModel.WATER.index(), 0, 1 ).evaluate( indexOfRefractionForRed );
            return colorBlend( BendingLightModel.AIR_COLOR, BendingLightModel.WATER_COLOR, ratio );
        }
        else if ( indexOfRefractionForRed < BendingLightModel.GLASS.index() ) {
            double ratio = new Function.LinearFunction( BendingLightModel.WATER.index(), BendingLightModel.GLASS.index(), 0, 1 ).evaluate( indexOfRefractionForRed );
            return colorBlend( BendingLightModel.WATER_COLOR, BendingLightModel.GLASS_COLOR, ratio );
        }
        else if ( indexOfRefractionForRed < BendingLightModel.DIAMOND.index() ) {
            double ratio = new Function.LinearFunction( BendingLightModel.GLASS.index(), BendingLightModel.DIAMOND.index(), 0, 1 ).evaluate( indexOfRefractionForRed );
            return colorBlend( BendingLightModel.GLASS_COLOR, BendingLightModel.DIAMOND_COLOR, ratio );
        }
        else {
            return BendingLightModel.DIAMOND_COLOR;
        }
    }

    private static Color colorBlend( Color a, Color b, double ratio ) {
        return new Color(
                clamp( (int) ( ( (float) a.getRed() ) * ( 1 - ratio ) + ( (float) b.getRed() ) * ratio ) ),
                clamp( (int) ( ( (float) a.getGreen() ) * ( 1 - ratio ) + ( (float) b.getGreen() ) * ratio ) ),
                clamp( (int) ( ( (float) a.getBlue() ) * ( 1 - ratio ) + ( (float) b.getBlue() ) * ratio ) ),
                clamp( (int) ( ( (float) a.getAlpha() ) * ( 1 - ratio ) + ( (float) b.getAlpha() ) * ratio ) )
        );
    }

    private static int clamp( int value ) {
        if ( value < 0 ) { return 0; }
        else if ( value > 255 ) { return 255; }
        else { return value; }
    }
}
