// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.List;

import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

/**
 * Base class for energy users, i.e. model elements that take energy from
 * an energy converter and do something with it, such as producing light or
 * heat.
 *
 * @author John Blanco
 */
public abstract class EnergyUser extends EnergySystemElement {

    protected EnergyUser( Image iconImage, List<ModelElementImage> images ) {
        super( iconImage, images );
    }

    public abstract void stepInTime( double dt, Energy incomingEnergy );

    /**
     * Inject a list of energy chunks into this energy system element.  Once
     * injected, it is the system's responsibility to move, convert, and
     * otherwise manage them.
     *
     * @param energyChunks
     */
    public abstract void injectEnergyChunks( List<EnergyChunk> energyChunks );
}
