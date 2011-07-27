package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SucroseComponent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.OpenSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SquareLattice;

/**
 * Data structures and algorithms for creating and modeling a sugar crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SucroseLattice extends SquareLattice<SucroseLattice> {

    public SucroseLattice() {
        super( new ImmutableList<Component>( new SucroseComponent() ), new ImmutableList<Bond>() );
    }

    public SucroseLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<OpenSite<SucroseLattice>> openSites, Component component, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new SucroseSite( component, type ) );
        }
    }
}