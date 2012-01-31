// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.model;

import java.awt.Color;

import edu.colorado.phet.molarity.MolarityResources.Symbols;

/**
 * Model of a solvent, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solvent {

    /*
     * Pure water is a type of solvent.
     * Since Solvents are immutable, we could use one static instance on Water.
     * But that seems a bit precarious; if it's ever changed to mutable, it could make for some odd/difficult bugs.
     */
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
