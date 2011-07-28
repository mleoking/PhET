package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;

/**
 * A way to grow a sugar crystal.
 *
 * @author Sam Reid
 */
public class SucroseSite extends LatticeSite<Sucrose> {
    public SucroseSite( Sucrose component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice<Sucrose> grow( Lattice<Sucrose> lattice ) {
        Sucrose newSucrose = getOppositeComponent();
        return new SucroseLattice( new ImmutableList<Sucrose>( lattice.components, newSucrose ), new ImmutableList<Bond<Sucrose>>( lattice.bonds, new Bond<Sucrose>( component, newSucrose, type ) ) );
    }

    public Sucrose getOppositeComponent() {
        return new Sucrose();
    }
}