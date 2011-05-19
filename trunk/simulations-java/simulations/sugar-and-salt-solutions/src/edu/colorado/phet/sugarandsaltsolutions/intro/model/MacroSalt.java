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
    public static double molarMass = 58.4425;// g/mol
    //    private static final double gramsPerGrain = 1;
    private static final double gramsPerGrain = 5.85E-5;  //see http://wiki.answers.com/Q/If_a_typical_grain_of_salt_has_a_mass_of_0.3_mg_how_many_unit_cells_does_it_contain
    private static double molesIn5Grams = gramsPerGrain / molarMass;

    public MacroSalt( ImmutableVector2D position, double volumePerMole ) {
        super( position, molesIn5Grams, volumePerMole );
    }
}