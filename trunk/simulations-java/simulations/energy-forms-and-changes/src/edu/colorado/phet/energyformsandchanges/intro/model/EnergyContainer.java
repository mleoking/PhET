// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.util.ObserverList;

/**
 * @author John Blanco
 */
public interface EnergyContainer {
    ObserverList<EnergyChunk> getEnergyChunkList();
}
