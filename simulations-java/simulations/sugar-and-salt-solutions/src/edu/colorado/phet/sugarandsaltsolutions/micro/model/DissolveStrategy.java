// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

/**
 * Strategy rule to use for dissolving the crystals
 *
 * @author Sam Reid
 */
public interface DissolveStrategy {
    ArrayList<? extends Particle> dissolve( ItemList<? extends Crystal> crystals, Crystal crystal, double saturationPoint );
}
