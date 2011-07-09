package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Ion.SodiumIon;

/**
 * An open site represents a location within a lattice where a new ion could be added.  This is used in lattice growing.
 *
 * @author Sam Reid
 */
public class OpenSite {
    private final Ion ion;
    private final BondType type;

    public OpenSite( Ion ion, BondType type ) {
        this.ion = ion;
        this.type = type;
    }

    //Create a new lattice that has expanded from this open site
    public SaltLattice grow( SaltLattice saltLattice ) {
        Ion newIon = ( ion instanceof SodiumIon ) ? new Ion.ChlorideIon() : new SodiumIon();
        return new SaltLattice( new ImmutableList<Ion>( saltLattice.ions, newIon ), new ImmutableList<Bond>( saltLattice.bonds, new Bond( ion, newIon, type ) ) );
    }
}
