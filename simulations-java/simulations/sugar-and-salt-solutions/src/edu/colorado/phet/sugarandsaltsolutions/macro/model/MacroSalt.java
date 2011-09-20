// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Salt crystal
 *
 * @author Sam Reid
 */
public class MacroSalt extends MacroCrystal {

    //Create a salt crystal with the specified amount in g/mol
    public static final double molarMass = 58.4425;

    //Manually tuned to make it so that grains are small but it doesn't take too long to get the concentration bar to appear on the bar chart
    private static final double gramsPerGrain = 0.2;

    private static final double molesIn5Grams = gramsPerGrain / molarMass;

    public MacroSalt( ImmutableVector2D position, double volumePerMole ) {
        super( position, molesIn5Grams, volumePerMole );
    }
}