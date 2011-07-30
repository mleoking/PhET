package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Marker class to signify which compounds are crystals vs noncrystals.
 *
 * @author Sam Reid
 */
public abstract class Crystal<T extends Particle> extends Compound<T> {

    //The spacing between components on the lattice
    public final double spacing;

    //Direction vectors (non-unit vectors) in the coordinate frame of the lattice, at the right spacing and angle
    protected final ImmutableVector2D NORTH;
    protected final ImmutableVector2D SOUTH;
    protected final ImmutableVector2D EAST;
    protected final ImmutableVector2D WEST;

    public final Random random = new Random();

    //Construct the compound from the specified lattice
    public Crystal( ImmutableVector2D position, double spacing, double angle ) {
        super( position, angle );
        this.spacing = spacing;

        //Update positions so the lattice position overwrites constituent particle positions
        updateConstituentLocations();

        NORTH = new ImmutableVector2D( 0, 1 ).times( spacing ).getRotatedInstance( angle );
        SOUTH = new ImmutableVector2D( 0, -1 ).times( spacing ).getRotatedInstance( angle );
        EAST = new ImmutableVector2D( 1, 0 ).times( spacing ).getRotatedInstance( angle );
        WEST = new ImmutableVector2D( -1, 0 ).times( spacing ).getRotatedInstance( angle );
    }

    //Create an instance that could bond with the specified original particle for purposes of growing crystals from scratch
    public abstract T createPartner( T original );

    //Grow the crystal for the specified number of steps
    public void grow( int numSteps ) {
        for ( int i = 0; i < numSteps; i++ ) {
            grow();
        }
    }

    //Grow the crystal randomly at one of the open sites
    public void grow() {
        if ( constituents.size() == 0 ) {
            addConstituent( new Constituent<T>( createSeed(), ImmutableVector2D.ZERO ) );
        }
        else {
            //find any particle that has open bonds
            ArrayList<OpenSite<T>> openSites = getOpenSites();

            if ( openSites.size() > 0 ) {
                addConstituent( openSites.get( random.nextInt( openSites.size() ) ).toConstituent() );
            }
            else {
                System.out.println( "Nowhere to bond!" );
            }
        }
    }

    //Determine all of the available locations where an existing particle could be added
    public ArrayList<OpenSite<T>> getOpenSites() {
        ArrayList<OpenSite<T>> bondingSites = new ArrayList<OpenSite<T>>();
        for ( final Constituent<T> constituent : new ArrayList<Constituent<T>>( constituents ) ) {
            for ( ImmutableVector2D direction : new ImmutableVector2D[] { NORTH, SOUTH, EAST, WEST } ) {
                ImmutableVector2D relativePosition = constituent.relativePosition.plus( direction );
                if ( !isOccupied( relativePosition ) ) {
                    T opposite = createPartner( constituent.particle );
                    ImmutableVector2D absolutePosition = relativePosition.plus( getPosition() );
                    opposite.setPosition( absolutePosition );
                    bondingSites.add( new OpenSite<T>( relativePosition, opposite.getShape(), new Function0<T>() {
                        public T apply() {
                            return createPartner( constituent.particle );
                        }
                    }, absolutePosition ) );
                }
            }
        }

        return bondingSites;
    }

    //Determine whether the specified location is available for bonding or already occupied by another particle
    private boolean isOccupied( ImmutableVector2D location ) {
        for ( Constituent<T> constituent : constituents ) {
            if ( constituent.relativePosition.minus( location ).getMagnitude() < 1E-12 ) {
                return true;
            }
        }
        return false;
    }

    //Create the first constituent particle in a crystal
    protected abstract T createSeed();
}