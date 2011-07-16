// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.OpenSite;

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
