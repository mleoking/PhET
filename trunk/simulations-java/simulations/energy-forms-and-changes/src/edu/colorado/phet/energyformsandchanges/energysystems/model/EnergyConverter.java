// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.List;

import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

/**
 * Base class for energy converters, i.e. model elements that take energy from
 * a source and convert it to something else (such as mechanical to electrical)
 * and then supply it to an energy user.
 *
 * @author John Blanco
 */
public abstract class EnergyConverter extends EnergySystemElement {

    protected EnergyConverter( Image iconImage, List<ModelElementImage> images ) {
        super( iconImage, images );
    }

    public abstract Energy stepInTime( double dt, Energy incomingEnergy );

    /**
     * Get the energy chunks that this source wants to transfer to the next
     * energy system element.  Reading clears the list.
     *
     * @return list of energy chunks to transfer.
     */
    public abstract List<EnergyChunk> extractOutgoingEnergyChunks();

    /**
     * Inject a list of energy chunks into this energy system element.  Once
     * injected, it is the system's responsibility to move, convert, and
     * otherwise manage them.
     *
     * @param energyChunks
     */
    public abstract void injectEnergyChunks( List<EnergyChunk> energyChunks );
}
