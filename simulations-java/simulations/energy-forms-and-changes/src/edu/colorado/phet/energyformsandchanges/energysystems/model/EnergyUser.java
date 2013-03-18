// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.ArrayList;
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

    protected List<EnergyChunk> incomingEnergyChunks = new ArrayList<EnergyChunk>();

    protected EnergyUser( Image iconImage ) {
        super( iconImage );
    }

    public abstract void stepInTime( double dt, Energy incomingEnergy );

    /**
     * Set up the energy chunks contained by this energy source as though they
     * have been full propagated through the system.
     *
     * @param incomingEnergyRate Incoming energy in joules/sec.
     */
    public abstract void preLoadEnergyChunks( double incomingEnergyRate );

    /**
     * Inject a list of energy chunks into this energy system element.  Once
     * injected, it is the system's responsibility to move, convert, and
     * otherwise manage them.
     *
     * @param energyChunks
     */
    public void injectEnergyChunks( List<EnergyChunk> energyChunks ) {
        incomingEnergyChunks.addAll( energyChunks );
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        incomingEnergyChunks.clear();
    }
}
