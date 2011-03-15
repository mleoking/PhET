// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.interactiveisotope.model;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;

/**
 * This class represents the configuration of the nucleus of an atom.  It
 * contains the number of protons and neutrons, and implements a number of
 * methods for obtaining information about the nucleus.
 *
 * @author John Blanco
 */
public class ImmutableNucleus {

    private final int numProtons;
    private final int numNeutrons;

    public ImmutableNucleus( int protons, int neutrons ) {
        this.numProtons = protons;
        this.numNeutrons = neutrons;
    }

    public int getNumProtons() {
        return numProtons;
    }

    public int getNumNeutrons() {
        return numNeutrons;
    }

    @Override
    public boolean equals( Object o ) {
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ImmutableNucleus nucleusValue = (ImmutableNucleus) o;

        if ( numNeutrons != nucleusValue.numNeutrons ) {
            return false;
        }
        if ( numProtons != nucleusValue.numProtons ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = numProtons;
        hash = hash * 31 + numNeutrons;
        return hash;
    }

    public int getMassNumber() {
        return numProtons + numNeutrons;
    }

    @Override
    public String toString (){
        return new String( "protons: " + numProtons + ", neutrons: " + numNeutrons );
    }

    public String getName() {
        return AtomIdentifier.getName( numProtons );
    }

    public String getSymbol() {
        return AtomIdentifier.getSymbol( numProtons );
    }

    public boolean isStable() {
        return AtomIdentifier.isStable( toImmutableAtom() );
    }

    public double getAtomicMass() {
        return AtomIdentifier.getAtomicMass( toImmutableAtom() );
    }

    public double getNaturalAbundance() {
        return AtomIdentifier.getNaturalAbundance( toImmutableAtom() );
    }

    /**
     * Create a neutral immutable atom based on this nucleus' configuration.
     * This is needed because the ImmutableAtom class is widely used within
     * the simulation.
     * @return
     */
    public ImmutableAtom toImmutableAtom(){
        return new ImmutableAtom( numProtons, numNeutrons, numProtons );
    }
}
