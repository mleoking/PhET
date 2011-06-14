// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.*;

/**
 * For determining the colors of different mediums as a function of characteristic index of refraction.
 *
 * @author Sam Reid
 */
public class MediumColorFactory {
    public static final Color AIR_COLOR = Color.white;
    public static final Color WATER_COLOR = new Color( 198, 226, 246 );
    public static final Color GLASS_COLOR = new Color( 171, 169, 212 );
    public static final Color DIAMOND_COLOR = new Color( 78, 79, 164 );

    //Maps index of refraction to color using linear functions
    public static Color getColor( double indexForRed ) {
        //Precompute to improve readability below
        final double waterIndexForRed = WATER.getIndexOfRefractionForRedLight();
        final double glassIndexForRed = GLASS.getIndexOfRefractionForRedLight();
        final double diamondIndexForRed = DIAMOND.getIndexOfRefractionForRedLight();

        //Find out what region the index of refraction lies in, and linearly interpolate between adjacent medium colors
        if ( indexForRed < waterIndexForRed ) {
            double ratio = new Function.LinearFunction( 1.0, waterIndexForRed, 0, 1 ).evaluate( indexForRed );
            return colorBlend( AIR_COLOR, WATER_COLOR, ratio );
        }
        else {
            if ( indexForRed < glassIndexForRed ) {
                double ratio = new Function.LinearFunction( waterIndexForRed, glassIndexForRed, 0, 1 ).evaluate( indexForRed );
                return colorBlend( WATER_COLOR, GLASS_COLOR, ratio );
            }
            else {
                if ( indexForRed < diamondIndexForRed ) {
                    double ratio = new Function.LinearFunction( glassIndexForRed, diamondIndexForRed, 0, 1 ).evaluate( indexForRed );
                    return colorBlend( GLASS_COLOR, DIAMOND_COLOR, ratio );
                }
                else {
                    return DIAMOND_COLOR;
                }
            }
        }
    }

    //Blend colors a and b with the specified amount of "b" to use between 0 and 1
    private static Color colorBlend( Color a, Color b, double ratio ) {
        return new Color(
                clamp( (int) ( ( (float) a.getRed() ) * ( 1 - ratio ) + ( (float) b.getRed() ) * ratio ) ),
                clamp( (int) ( ( (float) a.getGreen() ) * ( 1 - ratio ) + ( (float) b.getGreen() ) * ratio ) ),
                clamp( (int) ( ( (float) a.getBlue() ) * ( 1 - ratio ) + ( (float) b.getBlue() ) * ratio ) ),
                clamp( (int) ( ( (float) a.getAlpha() ) * ( 1 - ratio ) + ( (float) b.getAlpha() ) * ratio ) )
        );
    }

    //Make sure light doesn't go outside of the 0..255 bounds
    private static int clamp( int value ) {
        if ( value < 0 ) { return 0; }
        else if ( value > 255 ) { return 255; }
        else { return value; }
    }
}
