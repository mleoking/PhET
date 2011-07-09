package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Random;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.SaltLattice.BondType.*;

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
        SaltLattice lattice = new SaltLattice();
        for ( int i = 0; i < numVertices; i++ ) {
            lattice = lattice.grow( random );
        }
        this.ions = lattice.ions;
        this.bonds = lattice.bonds;
    }

    //Create a SaltLattice with the specified ions and bonds
    public SaltLattice( ImmutableList<Ion> ions, ImmutableList<Bond> bonds ) {
        this.ions = ions;
        this.bonds = bonds;
    }

    static class Ion {
    }

    static class SodiumIon extends Ion {
        @Override public String toString() {
            return "Na";
        }
    }

    static class ChlorideIon extends Ion {
        @Override public String toString() {
            return "Cl";
        }
    }

    public static class Bond {
        public final Ion source;
        public final Ion destination;
        public final BondType type;

        Bond( Ion source, Ion destination, BondType type ) {
            this.source = source;
            this.destination = destination;
            this.type = type;
        }

        public Bond reverse() {
            return new Bond( destination, source, type.reverse() );
        }

        @Override public String toString() {
            return source + " --" + type + "--> " + destination;
        }
    }

    @Override public String toString() {
        return "ions: " + ions.toString() + ", bonds: " + bonds;
    }

    static abstract class BondType {
        static final BondType UP = new BondType() {
            @Override public BondType reverse() {
                return DOWN;
            }

            @Override public String toString() {
                return "up";
            }
        };
        static final BondType DOWN = new BondType() {
            @Override public BondType reverse() {
                return UP;
            }

            @Override public String toString() {
                return "down";
            }
        };
        static final BondType LEFT = new BondType() {
            @Override public BondType reverse() {
                return RIGHT;
            }

            @Override public String toString() {
                return "left";
            }
        };
        static final BondType RIGHT = new BondType() {
            @Override public BondType reverse() {
                return LEFT;
            }

            @Override public String toString() {
                return "right";
            }
        };

        public abstract BondType reverse();
    }

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

    private ArrayList<OpenSite> getOpenSites() {
        ArrayList<OpenSite> openSites = new ArrayList<OpenSite>();
        for ( Ion ion : ions ) {
            ArrayList<Bond> bonds = getBonds( ion );
            testAddSite( openSites, ion, bonds, UP );
            testAddSite( openSites, ion, bonds, DOWN );
            testAddSite( openSites, ion, bonds, LEFT );
            testAddSite( openSites, ion, bonds, RIGHT );
        }
        return openSites;
    }

    private void testAddSite( ArrayList<OpenSite> openSites, Ion ion, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new OpenSite( ion, type ) );
        }
    }

    private boolean containsBondType( ArrayList<Bond> bonds, BondType type ) {
        for ( Bond bond : bonds ) {
            if ( bond.type == type ) {
                return true;
            }
        }
        return false;
    }

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

    private static class OpenSite {
        private final Ion ion;
        private final BondType type;

        public OpenSite( Ion ion, BondType type ) {
            this.ion = ion;
            this.type = type;
        }

        public SaltLattice grow( SaltLattice saltLattice ) {
            Ion newIon = ( ion instanceof SodiumIon ) ? new ChlorideIon() : new SodiumIon();
            return new SaltLattice( new ImmutableList<Ion>( saltLattice.ions, newIon ), new ImmutableList<Bond>( saltLattice.bonds, new Bond( ion, newIon, type ) ) );
        }
    }

    public static void main( String[] args ) {
        SaltLattice lattice = new SaltLattice( 10 );
        System.out.println( "saltLattice = " + lattice );
    }
}