/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.util.ArrayList;

/**
 * This interface represents a repository of subatomic particles, i.e.
 * protons, neutrons, and electrons.
 *
 * @author John Blanco
 */
public interface SubatomicParticleRepository {

    /**
     * Get an electron from the repository.  The particle will be removed from
     * the repository.
     *
     * @return - particle reference if available, null of no such particles
     * available.
     */
    Proton getProton();
    void addProton( Proton proton );
    void addProtonCollection( ArrayList<Proton> protons );

    /**
     * Get an electron from the repository.  The particle will be removed from
     * the repository.
     *
     * @return - particle reference if available, null of no such particles
     * available.
     */
    Neutron getNeutron();
    void addNeutron( Neutron neutron );
    void addNeutronCollection( ArrayList<Neutron> neutrons );

    /**
     * Get an electron from the repository.  The particle will be removed from
     * the repository.
     *
     * @return - particle reference if available, null of no such particles
     * available.
     */
    Electron getElectron();
    void addElectron( Electron electron );
    void addElectronCollection( ArrayList<Electron> electrons );
}
