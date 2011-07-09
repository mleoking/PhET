package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Feasibility test for simplified salt lattice construction and modeling.
 *
 * @author Sam Reid
 */
public class SaltLattice {
    private ImmutableList<SodiumIon> sodiumList = new ImmutableList<SodiumIon>();
    private ImmutableList<ChlorideIon> chlorideList = new ImmutableList<ChlorideIon>();
    private ImmutableList<Bond> bonds = new ImmutableList<Bond>();

    public SaltLattice() {
        this( new ImmutableList<SodiumIon>(), new ImmutableList<ChlorideIon>(), new ImmutableList<Bond>() );
    }

    public SaltLattice( ImmutableList<SodiumIon> sodiumList, ImmutableList<ChlorideIon> chlorideList, ImmutableList<Bond> bonds ) {
        this.sodiumList = sodiumList;
        this.chlorideList = chlorideList;
        this.bonds = bonds;
    }

    static class SodiumIon {
    }

    static class ChlorideIon {
    }

    static class Bond {
        static final Bond UP = new Bond();
        static final Bond DOWN = new Bond();
        static final Bond LEFT = new Bond();
        static final Bond RIGHT = new Bond();
    }

    public static void main( String[] args ) {
        final Random random = new Random();
        new SaltLattice().grow( random );
    }

    private SaltLattice grow( Random random ) {
        if ( sodiumList.size() == 0 && chlorideList.size() == 0 ) {
            return new SaltLattice( new ImmutableList<SodiumIon>( new SodiumIon() ), new ImmutableList<ChlorideIon>(), new ImmutableList<Bond>() );
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
        return new ArrayList<OpenSite>();
    }

    private class OpenSite {
        public SaltLattice grow( SaltLattice saltLattice ) {
            return new SaltLattice();
        }
    }
}