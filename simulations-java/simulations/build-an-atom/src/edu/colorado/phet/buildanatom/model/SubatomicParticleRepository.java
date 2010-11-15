/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.util.ArrayList;

/**
 * This interface represents a repository of subatomic particles, i.e.
 * protons, neutrons, and electrons.
 *
 * @author John Blanco
 */
public interface SubatomicParticleRepository {

    Proton getProton();
    void addProton( Proton proton );
    void addProtonCollection( ArrayList<Proton> protons );

    Neutron getNeutron();
    void addNeutron( Neutron neutron );
    void addNeutronCollection( ArrayList<Neutron> neutrons );

    Electron getElectron();
    void addElectron( Electron electron );
    void addElectronCollection( ArrayList<Electron> electrons );
}
