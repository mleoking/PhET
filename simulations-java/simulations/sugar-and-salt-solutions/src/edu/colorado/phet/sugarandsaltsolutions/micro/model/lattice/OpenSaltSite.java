package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;

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
