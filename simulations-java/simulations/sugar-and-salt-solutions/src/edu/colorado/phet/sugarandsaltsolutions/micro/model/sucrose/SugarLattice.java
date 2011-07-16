package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.OpenSite;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType.*;

/**
 * Data structures and algorithms for creating and modeling a sugar crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SugarLattice extends Lattice {

    public SugarLattice() {
        super( new ImmutableList<Component>( new SugarComponent() ), new ImmutableList<Bond>() );
    }

    public SugarLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    //Find the available sites where a new component might be added
    //TODO: duplicated with salt lattice
    @Override protected ArrayList<OpenSite> getOpenSites() {
        ArrayList<OpenSite> openSites = new ArrayList<OpenSite>();
        for ( Component component : components ) {
            for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                testAddSite( openSites, component, getBonds( component ), bondType );
            }
        }
        return openSites;
    }

    @Override protected void testAddSite( ArrayList<OpenSite> openSites, Component component, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new OpenSugarSite( component, type ) );
        }
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new SugarLattice().grow( 100 );
        System.out.println( "sugarLattice = " + lattice );
    }
}