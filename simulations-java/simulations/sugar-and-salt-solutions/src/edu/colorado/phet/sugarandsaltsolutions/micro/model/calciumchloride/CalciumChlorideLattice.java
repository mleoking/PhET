// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.*;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType.*;

/**
 * Data structures and algorithms for creating and modeling a calcium chloride crystal lattice,
 * using the same lattice structure as in the soluble salts sim.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class CalciumChlorideLattice extends Lattice {

    public CalciumChlorideLattice() {
        //Seed with a chloride since we need 2:1 ratio of chloride to calcium
        super( new ImmutableList<Component>( new Component.ChlorideIon() ), new ImmutableList<Bond>() );
    }

    public CalciumChlorideLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<OpenSite> openSites, Component component, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new CalciumChlorideSite( component, type ) );
        }
    }

    //Find the available sites where a new component might be added
    @Override protected ArrayList<OpenSite> getOpenSites() {
        ArrayList<OpenSite> openSites = new ArrayList<OpenSite>();
        for ( Component component : components ) {

            //Calcium bonds in all 4 directions
            if ( component instanceof Component.CalciumIon ) {
                for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                    testAddSite( openSites, component, getBonds( component ), bondType );
                }
            }
            //Chloride only bonds in the same direction as it came from
            //It only forms 2 bonds instead of 4 like Calcium
            //So if it already has 2 bonds, then it is full
            //If it only has 1 bond, then the other bond must be on the opposite side
            else if ( component instanceof Component.ChlorideIon ) {
                ArrayList<Bond> bonds = getBonds( component );
                if ( bonds.size() == 2 ) {
                    //do nothing, already fully bonded
                }
                else if ( bonds.size() == 1 ) {
                    //add one bond on the opposite side
                    openSites.add( new CalciumChlorideSite( component, bonds.get( 0 ).type.reverse() ) );
                }
                else {
                    //No bonds (it is the only ion in the lattice), so can go any direction
                    for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                        testAddSite( openSites, component, getBonds( component ), bondType );
                    }
                }
            }
        }
        return openSites;
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new CalciumChlorideLattice().grow( 100 );

        System.out.println( "lattice = " + lattice );
    }
}