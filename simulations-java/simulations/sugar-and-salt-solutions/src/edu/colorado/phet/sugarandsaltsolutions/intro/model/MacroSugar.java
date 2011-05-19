// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Sugar crystal
 *
 * @author Sam Reid
 */
public class MacroSugar extends MacroCrystal {
    //Create a sugar crystal with the specified amount
    public static double molarMass = 342.23134;// g/mol
    private static final double gramsPerGrain = 1;
    private static double molesIn5Grams = gramsPerGrain / molarMass;

    public MacroSugar( ImmutableVector2D position ) {
        super( position, molesIn5Grams );
    }
}
