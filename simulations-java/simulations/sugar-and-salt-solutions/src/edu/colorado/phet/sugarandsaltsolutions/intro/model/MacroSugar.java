// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Sugar crystal
 *
 * @author Sam Reid
 */
public class MacroSugar extends MacroCrystal {
    //Create a sugar crystal with 1E-6 moles of sugar
    //KL: I like how the concentration goes up by 0.01 mol/L when you add one grain of solute. But I can see students using this to compare salt and sugar, so we may need to use a different grain value for each. Let's try 0.05 mol for salt and 0.01 mol for sugar.
    public MacroSugar( ImmutableVector2D position ) {
        super( position, 0.01 );
    }
}
