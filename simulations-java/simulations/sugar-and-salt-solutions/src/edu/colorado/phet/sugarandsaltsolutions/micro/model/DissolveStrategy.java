// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * Strategy rule to use for dissolving the crystals
 *
 * @author Sam Reid
 */
public interface DissolveStrategy {
    void dissolve( ItemList<? extends Crystal> crystals, Crystal crystal, ItemList<Particle> freeParticles, ObservableProperty<Boolean> unsaturated );
}