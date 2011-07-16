package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.*;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;

/**
 * Identifies open (available) bonding site in a SaltLattice
 *
 * @author Sam Reid
 */
public class SodiumChlorideSite extends OpenSite<SodiumChlorideLattice> {
    public SodiumChlorideSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public SodiumChlorideLattice grow( SodiumChlorideLattice lattice ) {
        Component newIon = ( component instanceof SodiumIon ) ? new Component.ChlorideIon() : new SodiumIon();
        return new SodiumChlorideLattice( new ImmutableList<Component>( lattice.components, newIon ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newIon, type ) ) );
    }
}
