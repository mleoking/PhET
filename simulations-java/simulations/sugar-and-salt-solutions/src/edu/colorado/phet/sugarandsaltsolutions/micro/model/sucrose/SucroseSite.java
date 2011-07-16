package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.*;

/**
 * A way to grow a sugar crystal.
 *
 * @author Sam Reid
 */
public class SucroseSite extends OpenSite {
    public SucroseSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice grow( Lattice lattice ) {
        Component newComponent = new SucroseComponent();
        return new SucroseLattice( new ImmutableList<Component>( lattice.components, newComponent ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newComponent, type ) ) );
    }
}
