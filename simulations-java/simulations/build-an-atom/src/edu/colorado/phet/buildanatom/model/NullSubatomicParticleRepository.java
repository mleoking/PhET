// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.model;

import java.util.ArrayList;

//DOC and more importantly, why is this instantiated and assigned to a final variable?
/**
 * A null repository for subatomic particles.  This is always empty.
 *
 * @author John Blanco
 */
public class NullSubatomicParticleRepository implements SubatomicParticleRepository {

    public void addElectron( Electron electron ) {
        System.err.println("Error - Attempt to add electron to null repository, ignoring.");
    }

    public void addElectronCollection( ArrayList<Electron> electrons ) {
        System.err.println("Error - Attempt to add electron collection to null repository, ignoring.");
    }

    public void addNeutron( Neutron neutron ) {
        System.err.println("Error - Attempt to add neutron to null repository, ignoring.");
    }

    public void addNeutronCollection( ArrayList<Neutron> neutrons ) {
        System.err.println("Error - Attempt to add neutron collection to null repository, ignoring.");
    }

    public void addProton( Proton proton ) {
        System.err.println("Error - Attempt to add proton to null repository, ignoring.");
    }

    public void addProtonCollection( ArrayList<Proton> protons ) {
        System.err.println("Error - Attempt to add proton collection to null repository, ignoring.");
    }

    public Electron getElectron() {
        return null;
    }

    public Neutron getNeutron() {
        return null;
    }

    public Proton getProton() {
        return null;
    }
}
