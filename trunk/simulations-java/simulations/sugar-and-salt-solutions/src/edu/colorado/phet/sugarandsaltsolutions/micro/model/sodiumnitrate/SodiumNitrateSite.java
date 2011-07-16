// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.*;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;

/**
 * Identifies open (available) bonding site in a Sodium Nitrate crystal
 *
 * @author Sam Reid
 */
public class SodiumNitrateSite extends OpenSite<SodiumNitrateLattice> {
    public SodiumNitrateSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public SodiumNitrateLattice grow( SodiumNitrateLattice lattice ) {
        Component newIon = ( component instanceof SodiumIon ) ? new Component.Nitrate() : new SodiumIon();
        return new SodiumNitrateLattice( new ImmutableList<Component>( lattice.components, newIon ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newIon, type ) ) );
    }
}
