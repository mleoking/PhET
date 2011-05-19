// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Salt crystal
 *
 * @author Sam Reid
 */
public class MacroSalt extends MacroCrystal {
    //Create a salt crystal with the specified amount
    private static double saltMolarMass = 58.4425;// g/mol
    private static double molesIn5Grams = 5 / saltMolarMass;

    public MacroSalt( ImmutableVector2D position ) {
        super( position, molesIn5Grams );
    }
}