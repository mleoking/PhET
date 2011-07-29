package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * Marker class to signify which compounds are crystals vs noncrystals.
 *
 * @author Sam Reid
 */
public class Crystal<T extends Particle> extends Compound<T> {

    //The spacing between components on the lattice
    private final double spacing;

    //The lattice, which determines the graph layout (topology) of the crystal
    public final Lattice<T> lattice;

    //Construct the compound from the specified lattice
    public Crystal( ImmutableVector2D position, double spacing, Lattice<T> lattice, double angle ) {
        super( position, angle );
        this.spacing = spacing;
        this.lattice = lattice;

        //Recursive method to traverse the graph and create particles
        fill( lattice.components.getFirst(), new ArrayList<T>(), ZERO, spacing );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( ZERO, 0.0 );
    }

    //Recursive method to traverse the graph and create particles at the right locations
    //TODO: Get rid of spacing argument, it is a field now
    private void fill( T component, ArrayList<T> handled, ImmutableVector2D relativePosition, double spacing ) {

        //Create and add sucrose molecules in the right relative locations

        //TODO: Set angle on the component instance
        constituents.add( new Constituent<T>( component, relativePosition ) );

        handled.add( component );
        ArrayList<Bond<T>> bonds = lattice.getBonds( component );
        for ( Bond<T> bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( bond.destination, handled, relativePosition.plus( getDelta( spacing, bond.type ).getRotatedInstance( angle ) ), spacing );
            }
        }
    }

    //Find the location of the specified component by recursively searching over the graph
    private ImmutableVector2D findPosition( T component, ArrayList<T> visited, ImmutableVector2D relativePosition, T seekComponent ) {
        if ( component == seekComponent ) {
            return relativePosition;
        }
        visited.add( component );
        ArrayList<Bond<T>> bonds = lattice.getBonds( component );
        for ( Bond<T> bond : bonds ) {
            if ( !visited.contains( bond.destination ) ) {
                ImmutableVector2D location = findPosition( bond.destination, visited, relativePosition.plus( getDelta( spacing, bond.type ).getRotatedInstance( angle ) ), seekComponent );
                if ( location != null ) {
                    return location;
                }
            }
        }
        return null;
    }

    //Determine a direction to move based on the bond type
    protected ImmutableVector2D getDelta( double spacing, BondType bondType ) {
        if ( bondType == BondType.LEFT ) { return new ImmutableVector2D( -spacing, 0 ); }
        else if ( bondType == BondType.RIGHT ) { return new ImmutableVector2D( spacing, 0 ); }
        else if ( bondType == BondType.UP ) { return new ImmutableVector2D( 0, spacing ); }
        else if ( bondType == BondType.DOWN ) { return new ImmutableVector2D( 0, -spacing ); }
        else { throw new RuntimeException( "Unknown bond type: " + bondType ); }
    }

    //Determine all of the available locations where an existing particle could be added
    public ArrayList<CrystalSite> getOpenSites() {
        return new ArrayList<CrystalSite>() {{
            for ( LatticeSite<T> site : lattice.getOpenSites() ) {
                add( toCrystalSite( site ) );
            }
        }};
    }

    //Convert a lattice site to a crystal site so a real particle can connect to the crystal
    private CrystalSite toCrystalSite( LatticeSite<T> site ) {
        ImmutableVector2D relativePosition = findPosition( lattice.components.getFirst(), new ArrayList<T>(), ZERO, site.component );

        //This is the position of the particle to be bonded to
        ImmutableVector2D bondingTarget = relativePosition.plus( position.get() );

        //Find the location adjacent to the bonding target
        ImmutableVector2D delta = getDelta( spacing, site.type ).getRotatedInstance( angle );

        return new CrystalSite( bondingTarget.plus( delta ), site.getOppositeComponent(), site );
    }

    public Lattice<T> growCrystal( CrystalSite crystalSite ) {
        return crystalSite.latticeSite.grow( lattice );
    }
}