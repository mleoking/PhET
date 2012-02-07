// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Interface implemented by all fluids.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IFluid {

    public Color getFluidColor();

    public void addFluidColorObserver( SimpleObserver observer );
}
