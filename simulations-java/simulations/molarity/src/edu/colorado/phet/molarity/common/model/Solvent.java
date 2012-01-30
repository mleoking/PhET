// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.common.model;

import java.awt.Color;

import edu.colorado.phet.molarity.MolarityResources.Symbols;

/**
 * Model of a solvent, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solvent {

    // pure water
    //REVIEW: Why is water a class and not a field?
    public static class Water extends Solvent {
        public Water() {
            super( Symbols.WATER, new Color( 0xE0FFFF ) );
        }
    }

    public final String formula;
    public final Color color;

    public Solvent( String formula, Color color ) {
        this.formula = formula;
        this.color = color;
    }
}
