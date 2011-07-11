package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component.SodiumIon;

/**
 * Data structures and algorithms for creating and modeling a salt crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SaltLattice extends SquareLattice {

    public SaltLattice() {
        super( new ImmutableList<Component>( new SodiumIon() ), new ImmutableList<Bond>() );
    }

    public SaltLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<OpenSite> openSites, Component component, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new OpenSaltSite( component, type ) );
        }
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new SaltLattice().grow( 100 );
        System.out.println( "saltLattice = " + lattice );
    }
}