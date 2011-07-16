package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.OpenSite;

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
