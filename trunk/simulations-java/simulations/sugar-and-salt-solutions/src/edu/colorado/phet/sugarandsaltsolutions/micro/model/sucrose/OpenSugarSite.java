package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.OpenSite;

/**
 * A way to grow a sugar crystal.
 *
 * @author Sam Reid
 */
public class OpenSugarSite extends OpenSite {
    public OpenSugarSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice grow( Lattice lattice ) {
        Component newComponent = new SugarComponent();
        return new SugarLattice( new ImmutableList<Component>( lattice.components, newComponent ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newComponent, type ) ) );
    }
}
