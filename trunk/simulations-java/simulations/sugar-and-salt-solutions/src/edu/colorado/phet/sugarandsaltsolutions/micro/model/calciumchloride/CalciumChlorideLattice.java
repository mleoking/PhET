// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.*;

/**
 * Data structures and algorithms for creating and modeling a calcium chloride crystal lattice,
 * using the same lattice structure as in the soluble salts sim.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class CalciumChlorideLattice extends SquareLattice {

    public CalciumChlorideLattice() {
        //Seed with a chloride since we need 2:1 ratio of chloride to calcium
        super( new ImmutableList<Component>( new Component.ChlorideIon() ), new ImmutableList<Bond>() );
    }

    public CalciumChlorideLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<OpenSite> openSites, Component component, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new OpenCalciumChlorideSite( component, type ) );
        }
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new CalciumChlorideLattice().grow( 100 );

        System.out.println( "lattice = " + lattice );
    }
}