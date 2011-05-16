// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Salt crystal
 *
 * @author Sam Reid
 */
public class MacroSalt extends MacroCrystal {
    //Create a salt crystal with 1E-6 moles of salt
    //KL: I like how the concentration goes up by 0.01 mol/L when you add one grain of solute. But I can see students using this to compare salt and sugar, so we may need to use a different grain value for each. Let's try 0.05 mol for salt and 0.01 mol for sugar.
    public MacroSalt( ImmutableVector2D position ) {
        super( position, 0.05 );
    }
}