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
    public Lattice<? extends Lattice> lattice;

    //Construct the compound from the specified lattice
    public Crystal( ImmutableVector2D position, double spacing, Lattice<? extends Lattice> lattice ) {
        super( position );
        this.spacing = spacing;
        this.lattice = lattice;

//        for ( Component c : lattice.components ) {
//            constituents.add( new Constituent<T>( c.constructLatticeConstituent( angle ), ) )
//        }

        //Recursive method to traverse the graph and create particles
        fill( lattice.components.getFirst(), new ArrayList<Component>(), new ImmutableVector2D(), spacing );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles at the right locations
    private void fill( Component component, ArrayList<Component> handled, ImmutableVector2D relativePosition, double spacing ) {

        //Create and add sucrose molecules in the right relative locations
        constituents.add( new Constituent<T>( (T) component.constructLatticeConstituent( angle ), relativePosition ) );

        handled.add( component );
        ArrayList<Bond> bonds = lattice.getBonds( component );
        for ( Bond bond : bonds ) {
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
        ArrayList<? extends LatticeSite<? extends Lattice>> sites = lattice.getOpenSites();
        ArrayList<CrystalSite> crystalSites = new ArrayList<CrystalSite>();
        for ( LatticeSite<? extends Lattice> site : sites ) {
            crystalSites.add( toCrystalSite( site ) );
        }
        return crystalSites;
    }

    //Convert a lattice site to a crystal site so a real particle can connect to the crystal
    private CrystalSite toCrystalSite( LatticeSite<? extends Lattice> site ) {

        //TODO: implement
        return new CrystalSite( null, null );
    }
}