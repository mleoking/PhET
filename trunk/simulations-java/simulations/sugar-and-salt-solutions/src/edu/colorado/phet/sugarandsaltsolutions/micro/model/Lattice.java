package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * A lattice represents a set of components (may be elements or molecules) and the bonds between them.
 * TODO: Graph creation does not prevent particles from being placed in the same location (reached by 2 different paths)
 * <p/>
 * //Generify to obtain the self type, see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6479372
 * //or http://passion.forco.de/content/emulating-self-types-using-java-generics-simplify-fluent-api-implementation
 *
 * @author Sam Reid
 */
public abstract class Lattice<T extends Lattice<T>> {
    //List of ions in the salt lattice graph, these are the vertices in the graph representation
    public final ImmutableList<Component> components;
    //List of bonds between ions in the graph, these are the edges in the graph representation
    public final ImmutableList<Bond> bonds;

    //Create a Lattice with the specified components and bonds
    public Lattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        this.components = components;
        this.bonds = bonds;
    }

    @Override public String toString() {
        return "ions: " + components.toString() + ", bonds: " + bonds;
    }

    //Find the available sites where a new component might be added
    protected abstract ArrayList<OpenSite<T>> getOpenSites();

    //Check to see whether the adjacent site is available, if so, add it to the list of open sites for potential bonding
    protected abstract void testAddSite( ArrayList<OpenSite<T>> openSites, Component component, ArrayList<Bond> bonds, BondType type );

    //Determine whether the list contains a bond of the specified type
    protected boolean containsBondType( ArrayList<Bond> bonds, BondType type ) {
        for ( Bond bond : bonds ) {
            if ( bond.type == type ) {
                return true;
            }
        }
        return false;
    }

    //Find all of the bonds originating at the source component, reverses bonds if necessary so the specified component is the source
    public ArrayList<Bond> getBonds( Component component ) {
        ArrayList<Bond> ionBonds = new ArrayList<Bond>();
        for ( Bond bond : bonds ) {
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
    public Lattice grow( int numVertices ) {
        Random random = new Random();

        Lattice lattice = this;
        //Iterative algorithm to grow the salt lattice
        for ( int i = 0; i < numVertices; i++ ) {
            lattice = lattice.grow( random );
        }

        //Take the components of the grown lattice for this instance
        return lattice;
    }

    //Create a new Lattice with an additional (new) component
    protected Lattice grow( Random random ) {
        //Randomly choose an open site for expansion
        ArrayList<OpenSite<T>> sites = getOpenSites();
        OpenSite selected = sites.get( random.nextInt( sites.size() ) );

        //Grow at the selected open site
        return selected.grow( this );
    }
}