package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Marker class to signify which compounds are crystals vs noncrystals.
 *
 * @author Sam Reid
 */
public class Crystal<T extends Particle> extends Compound<T> {

    private double spacing;
    public Lattice<T> lattice;

    //Construct the compound from the specified lattice
    public Crystal( ImmutableVector2D position, double spacing, Lattice<T> lattice ) {
        super( position );
        this.spacing = spacing;
        this.lattice = lattice;

//        for ( Component c : lattice.components ) {
//            constituents.add( new Constituent<T>( c.constructLatticeConstituent( angle ), ) )
//        }

        //Recursive method to traverse the graph and create particles
        fill( lattice.components.getFirst(), new ArrayList<T>(), new ImmutableVector2D(), spacing );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles at the right locations
    private void fill( T component, ArrayList<T> handled, ImmutableVector2D relativePosition, double spacing ) {

        //Create and add sucrose molecules in the right relative locations

        //TODO: Set angle on the component instance
        constituents.add( new Constituent<T>( component, relativePosition ) );

        handled.add( component );
        ArrayList<Bond<T>> bonds = lattice.getBonds( component );
        for ( Bond<T> bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( bond.destination, handled, relativePosition.plus( getDelta( spacing, bond ).getRotatedInstance( angle ) ), spacing );
            }
        }
    }

    //Determine a direction to move based on the bond type
    protected ImmutableVector2D getDelta( double spacing, Bond bond ) {
        if ( bond.type == BondType.LEFT ) { return new ImmutableVector2D( -spacing, 0 ); }
        else if ( bond.type == BondType.RIGHT ) { return new ImmutableVector2D( spacing, 0 ); }
        else if ( bond.type == BondType.UP ) { return new ImmutableVector2D( 0, spacing ); }
        else if ( bond.type == BondType.DOWN ) { return new ImmutableVector2D( 0, -spacing ); }
        else { throw new RuntimeException( "Unknown bond type: " + bond ); }
    }

    //Determine all of the available locations where an existing particle could be added
    public ArrayList<CrystalSite> getCrystalSites() {
        ArrayList<LatticeSite<T>> sites = lattice.getOpenSites();
        ArrayList<CrystalSite> crystalSites = new ArrayList<CrystalSite>();
        for ( LatticeSite<T> site : sites ) {
            crystalSites.add( toCrystalSite( site ) );
        }
        return crystalSites;
    }

    //Convert a lattice site to a crystal site so a real particle can connect to the crystal
    private CrystalSite toCrystalSite( LatticeSite<T> site ) {

        //TODO: implement
        return new CrystalSite( null, null );
    }
}