// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSymbols;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A solvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solvent implements IFluid {

    /*
     * Pure water is a type of solvent.
     * Since Solvents are immutable, we could use one static instance on Water.
     * But that seems a bit precarious; if it's ever changed to mutable, it could make for some odd/difficult bugs.
     */
    public static class Water extends Solvent {
        public Water() {
            super( Strings.WATER, BLLSymbols.WATER, new Color( 0xE0FFFF ) );
        }
    }

    public final String name;
    public final String formula;
    private final Color color;

    public Solvent( String name, String formula, Color color ) {
        this.name = name;
        this.formula = formula;
        this.color = color;
    }

    public Color getFluidColor() {
        return color;
    }

    public void addFluidColorObserver( SimpleObserver observer ) {
        observer.update();
    }
}
