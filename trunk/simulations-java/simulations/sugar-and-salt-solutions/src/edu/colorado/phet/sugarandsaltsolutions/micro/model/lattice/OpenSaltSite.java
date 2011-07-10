package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component.SodiumIon;

/**
 * Identifies open (available) bonding site in a SaltLattice
 *
 * @author Sam Reid
 */
public class OpenSaltSite extends OpenSite<SaltLattice> {
    public OpenSaltSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public SaltLattice grow( SaltLattice lattice ) {
        Component newIon = ( component instanceof SodiumIon ) ? new Component.ChlorideIon() : new SodiumIon();
        return new SaltLattice( new ImmutableList<Component>( lattice.components, newIon ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newIon, type ) ) );
    }
}
