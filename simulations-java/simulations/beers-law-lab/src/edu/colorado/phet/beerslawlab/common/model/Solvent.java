// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.concentration.model.IFluid;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A solvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solvent implements IFluid {

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
