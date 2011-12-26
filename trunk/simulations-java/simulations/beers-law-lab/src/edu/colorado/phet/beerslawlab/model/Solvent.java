// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A solvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solvent implements IFluid {

    public final String name;
    public final String formula;
    private final Color fluidColor;

    public Solvent( String name, String formula, Color fluidColor ) {
        this.name = name;
        this.formula = formula;
        this.fluidColor = fluidColor;
    }

    public Color getFluidColor() {
        return fluidColor;
    }

    public void addFluidColorObserver( SimpleObserver observer ) {
        observer.update();
    }
}
