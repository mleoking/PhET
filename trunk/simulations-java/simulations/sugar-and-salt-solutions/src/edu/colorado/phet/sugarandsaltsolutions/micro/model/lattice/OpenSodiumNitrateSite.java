// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component.SodiumIon;

/**
 * Identifies open (available) bonding site in a Sodium Nitrate crystal
 *
 * @author Sam Reid
 */
public class OpenSodiumNitrateSite extends OpenSite<SodiumNitrateLattice> {
    public OpenSodiumNitrateSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public SodiumNitrateLattice grow( SodiumNitrateLattice lattice ) {
        Component newIon = ( component instanceof SodiumIon ) ? new Component.Nitrate() : new SodiumIon();
        return new SodiumNitrateLattice( new ImmutableList<Component>( lattice.components, newIon ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newIon, type ) ) );
    }
}
