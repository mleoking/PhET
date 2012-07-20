// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Sugar crystal
 *
 * @author Sam Reid
 */
public class MacroSugar extends MacroCrystal {

    //Create a sugar crystal with the specified amount in grams /mol
    public static final double molarMass = 342.23134;

    //Number of grams per grain of sugar, manually tuned to make it so that grains are small but it doesn't take too long to get the concentration bar to appear on the bar chart
    //Making this number bigger won't change the size of the salt grain, but will change how fast the concentration goes up as salt is shaken in
    private static final double gramsPerGrain = 0.4;

    private static final double molesIn5Grams = gramsPerGrain / molarMass;

    public MacroSugar( Vector2D position, double volumePerMole ) {
        super( position, molesIn5Grams, volumePerMole );
    }
}