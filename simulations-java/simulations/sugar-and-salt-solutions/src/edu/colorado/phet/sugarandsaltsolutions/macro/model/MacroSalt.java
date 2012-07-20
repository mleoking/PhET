// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Salt crystal
 *
 * @author Sam Reid
 */
public class MacroSalt extends MacroCrystal {

    //Create a salt crystal with the specified amount in g/mol
    public static final double molarMass = 58.4425;

    //Manually tuned to make it so that grains are small but it doesn't take too long to get the concentration bar to appear on the bar chart
    //Making this number bigger won't change the size of the salt grain, but will change how fast the concentration goes up as salt is shaken in
    private static final double gramsPerGrain = 0.2;

    private static final double molesIn5Grams = gramsPerGrain / molarMass;

    public MacroSalt( Vector2D position, double volumePerMole ) {
        super( position, molesIn5Grams, volumePerMole );
    }
}