package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

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
            for ( ImmutableVector2D direction : getPossibleDirections( constituent ) ) {
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

    //This method gets the possible directions in which the crystal can be grown from the given constituent.
    //The default implementation here is a dense square lattice, like a checkerboard
    //This method is overrideable so that other crystal types like CaCl2 can specify their own topology
    //This may not generalize to non-square lattice topologies, but is sufficient for all currently requested crystal types for sugar and salt solutions
    public ImmutableVector2D[] getPossibleDirections( Constituent<T> constituent ) {
        return new ImmutableVector2D[] { NORTH, SOUTH, EAST, WEST };
    }

    //Determine whether the specified location is available for bonding or already occupied by another particle
    public boolean isOccupied( ImmutableVector2D location ) {
        for ( Constituent<T> constituent : constituents ) {
            if ( constituent.relativePosition.minus( location ).getMagnitude() < 1E-12 ) {
                return true;
            }
        }
        return false;
    }

    //Create the first constituent particle in a crystal
    protected abstract T createSeed();

    //From the compound's constituents, choose one near the edge that would be good to release as part of a dissolving process
    public Constituent<T> getConstituentToDissolve( final Rectangle2D waterBounds ) {

        //Only consider particles that are completely submerged because it would be incorrect for particles outside of the fluid to suddenly disassociate from the crystal
        ItemList<Constituent<T>> c = new ItemList<Constituent<T>>() {{
            for ( Constituent<T> constituent : constituents ) {
                if ( waterBounds.contains( constituent.particle.getShape().getBounds2D() ) ) {
                    add( constituent );
                }
            }
        }};

        //Select only particles that don't separate the crystal, this will ensure that particles leave from the edges and do not leave particles floating in the air
        c = c.filterList( new Function1<Constituent<T>, Boolean>() {
            public Boolean apply( Constituent<T> constituent ) {
                return !connectsCompound( constituent );
            }
        } );

        //Try to select a particle that maintains the ion balance within the crystal.
        //This is done by removing a constituent that is in the majority according to the formula ratio
        final Class<? extends Particle> majorityType = getMajorityType();
        if ( majorityType != null ) {
            c = c.filterList( new Function1<Constituent<T>, Boolean>() {
                public Boolean apply( Constituent<T> constituent ) {
                    return majorityType.isInstance( constituent.particle );
                }
            } );
        }

        //Sort by y-values to choose the highest particle
        Collections.sort( c, new Comparator<Constituent>() {
            public int compare( Constituent o1, Constituent o2 ) {
                return Double.compare( o1.particle.getPosition().getY(), o2.particle.getPosition().getY() );
            }
        } );
        if ( c.isEmpty() ) {
            return null;
        }
        else {
            return c.get( c.size() - 1 );
        }
    }

    //Determine whether removing the specified constituent would disconnect the crystal; if so, then it shouldn't be removed during dissolving
    //Instead, we should dissolve a particle from near the edge, which doesn't connect the crystal
    //TODO: implement this method
    private boolean connectsCompound( Constituent<T> constituent ) {
        return false;
    }

    //Returns the type of particle that is in the minority (according to the formula ratio), so that it can be removed during dissolving
    protected abstract Class<? extends Particle> getMajorityType();

    //Determine the majority type for a 1:1 formula ratio such as NaCl
    //Return null if no clear majority (i.e. a tie)
    public Class<? extends Particle> getMajorityType( Class<? extends Particle> a, Class<? extends Particle> b ) {
        int numA = count( a );
        int numB = count( b );
        if ( numA > numB ) {
            return a;
        }
        else if ( numB > numA ) {
            return b;
        }
        else {
            return null;
        }
    }
}