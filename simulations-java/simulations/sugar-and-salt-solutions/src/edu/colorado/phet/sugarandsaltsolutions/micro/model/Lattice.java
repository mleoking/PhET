package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * A lattice represents a set of components (may be elements or molecules) and the bonds between them, used as a blueprint
 * for creating a crystal.  Hence this is just a topological construct.  To actually place particles at specific positions, you need a crystal.
 * TODO: Graph creation does not prevent particles from being placed in the same location (reached by 2 different paths)
 *
 * @author Sam Reid
 */
public abstract class Lattice<T> {

    //List of components in the lattice (e.g. ions), the vertices in the graph representation
    public final ImmutableList<T> components;

    //List of bonds between vertices (e.g., ions) in the graph, these are the edges in the graph representation
    public final ImmutableList<Bond<T>> bonds;

    //Create a Lattice with the specified components and bonds
    public Lattice( ImmutableList<T> components, ImmutableList<Bond<T>> bonds ) {
        this.components = components;
        this.bonds = bonds;
    }

    @Override public String toString() {
        return "ions: " + components.toString() + ", bonds: " + bonds;
    }

    //Find the available sites where a new component might be added
    protected abstract ArrayList<LatticeSite<T>> getOpenSites();

    //Check to see whether the adjacent site is available, if so, add it to the list of open sites for potential bonding
    protected abstract void testAddSite( ArrayList<LatticeSite<T>> latticeSites, T component, ArrayList<Bond<T>> bonds, BondType type );

    //Determine whether the list contains a bond of the specified type
    protected boolean containsBondType( ArrayList<Bond<T>> bonds, BondType type ) {
        for ( Bond bond : bonds ) {
            if ( bond.type == type ) {
                return true;
            }
        }
        return false;
    }

    //Find all of the bonds originating at the source component, reverses bonds if necessary so the specified component is the source
    public ArrayList<Bond<T>> getBonds( T component ) {
        ArrayList<Bond<T>> ionBonds = new ArrayList<Bond<T>>();
        for ( Bond<T> bond : bonds ) {
            if ( bond.source == component ) {
                ionBonds.add( bond );
            }
            else if ( bond.destination == component ) {
                ionBonds.add( bond.reverse() );
            }
        }
        return ionBonds;
    }

    //Create a random salt lattice with the specified number of vertices
    public Lattice<T> grow( int numVertices ) {
        Random random = new Random();

        Lattice<T> lattice = this;
        //Iterative algorithm to grow the salt lattice
        for ( int i = 0; i < numVertices; i++ ) {
            lattice = lattice.grow( random );
        }

        //Take the components of the grown lattice for this instance
        return lattice;
    }

    //Create a new Lattice with an additional (new) component
    protected Lattice<T> grow( Random random ) {
        //Randomly choose an open site for expansion
        ArrayList<LatticeSite<T>> sites = getOpenSites();
        LatticeSite selected = sites.get( random.nextInt( sites.size() ) );

        //Grow at the selected open site
        return selected.grow( this );
    }

    //Count the number of components of the specified type to help ensure that lattices have a perfect 1:1 or 2:1 ratio
    public int count( Class<? extends T> c ) {
        int count = 0;
        for ( T component : components ) {
            if ( c.isInstance( component ) ) {
                count++;
            }
        }
        return count;
    }

    public abstract Lattice<T> drop( T component );

    public boolean contains( final T particle ) {
        return components.contains( particle ) || bonds.contains( new Function1<Bond<T>, Boolean>() {
            public Boolean apply( Bond<T> tBond ) {
                return tBond.contains( particle );
            }
        } );
    }
}