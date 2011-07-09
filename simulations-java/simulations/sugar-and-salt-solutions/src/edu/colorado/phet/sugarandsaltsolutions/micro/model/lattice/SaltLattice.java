package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Ion.SodiumIon;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType.*;

/**
 * Data structures and algorithms for creating and modeling a salt crystal lattice.  Instances are immutable.
 * TODO: Graph creation does not prevent particles from being placed in the same location (reached by 2 different paths)
 *
 * @author Sam Reid
 */
public class SaltLattice {

    //List of ions in the salt lattice graph, these are the vertices in the graph representation
    public final ImmutableList<Ion> ions;

    //List of bonds between ions in the graph, these are the edges in the graph representation
    public final ImmutableList<Bond> bonds;

    //Create an empty SaltLattice
    public SaltLattice() {
        this( new ImmutableList<Ion>(), new ImmutableList<Bond>() );
    }

    //Create a random salt lattice with the specified number of vertices
    public SaltLattice( int numVertices ) {
        Random random = new Random();

        //Iterative algorithm to grow the salt lattice
        SaltLattice lattice = new SaltLattice();
        for ( int i = 0; i < numVertices; i++ ) {
            lattice = lattice.grow( random );
        }

        //Take the components of the grown lattice for this instance
        this.ions = lattice.ions;
        this.bonds = lattice.bonds;
    }

    //Create a SaltLattice with the specified ions and bonds
    public SaltLattice( ImmutableList<Ion> ions, ImmutableList<Bond> bonds ) {
        this.ions = ions;
        this.bonds = bonds;
    }

    @Override public String toString() {
        return "ions: " + ions.toString() + ", bonds: " + bonds;
    }

    //Create a new SaltLattice with a new ion
    private SaltLattice grow( Random random ) {
        if ( ions.size() == 0 ) {
            return new SaltLattice( new ImmutableList<Ion>( new SodiumIon() ), new ImmutableList<Bond>() );
        }
        else {
            //Randomly choose an open site for expansion
            ArrayList<OpenSite> sites = getOpenSites();
            OpenSite selected = sites.get( random.nextInt( sites.size() ) );

            //Grow at the selected open site
            return selected.grow( this );
        }
    }

    //Find the available sites where a new ion might be added
    private ArrayList<OpenSite> getOpenSites() {
        ArrayList<OpenSite> openSites = new ArrayList<OpenSite>();
        for ( Ion ion : ions ) {
            for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                testAddSite( openSites, ion, getBonds( ion ), bondType );
            }
        }
        return openSites;
    }

    //Check to see whether the adjacent site is available, if so, add it to the list of open sites
    private void testAddSite( ArrayList<OpenSite> openSites, Ion ion, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new OpenSite( ion, type ) );
        }
    }

    //Determine whether the list contains a bond of the specified type
    private boolean containsBondType( ArrayList<Bond> bonds, BondType type ) {
        for ( Bond bond : bonds ) {
            if ( bond.type == type ) {
                return true;
            }
        }
        return false;
    }

    //Find all of the bonds originating at the source ion, reverses bonds if necessary so the specified ion is the source
    public ArrayList<Bond> getBonds( Ion ion ) {
        ArrayList<Bond> ionBonds = new ArrayList<Bond>();
        for ( Bond bond : bonds ) {
            if ( bond.source == ion ) {
                ionBonds.add( bond );
            }
            else if ( bond.destination == ion ) {
                ionBonds.add( bond.reverse() );
            }
        }
        return ionBonds;
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        SaltLattice lattice = new SaltLattice( 10 );
        System.out.println( "saltLattice = " + lattice );
    }
}