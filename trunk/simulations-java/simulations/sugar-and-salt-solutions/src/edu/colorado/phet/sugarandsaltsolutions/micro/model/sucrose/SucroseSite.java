package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SucroseComponent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.OpenSite;

/**
 * A way to grow a sugar crystal.
 *
 * @author Sam Reid
 */
public class SucroseSite extends OpenSite<SucroseLattice> {
    public SucroseSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public SucroseLattice grow( SucroseLattice lattice ) {
        Component newComponent = new SucroseComponent();
        return new SucroseLattice( new ImmutableList<Component>( lattice.components, newComponent ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newComponent, type ) ) );
    }
}
