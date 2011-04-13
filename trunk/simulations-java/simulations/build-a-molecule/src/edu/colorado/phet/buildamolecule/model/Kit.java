// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Contains multiple buckets of different types of atoms
 */
public class Kit {
    private final List<Bucket> buckets;
    private final List<AtomModel> atoms = new LinkedList<AtomModel>(); // our master list of atoms (in and out of buckets)
    private final LewisDotModel lewisDotModel; // lewis-dot connections between atoms on the play area
    private PBounds availableKitBounds;

    public static final double BOND_DISTANCE_THRESHOLD = 200;
    public static final double BUCKET_PADDING = 50;

    public Kit( List<Bucket> buckets, PBounds availableKitBounds ) {
        this.buckets = buckets;
        this.availableKitBounds = availableKitBounds;

        lewisDotModel = new LewisDotModel();

        // keep track of all atoms in our kit
        for ( Bucket bucket : buckets ) {
            atoms.addAll( bucket.getAtoms() );

            for ( AtomModel atom : bucket.getAtoms() ) {
                atom.addListener( new AtomModel.Adapter() {
                    @Override
                    public void grabbedByUser( AtomModel atom ) {
                        if ( lewisDotModel.containsAtom( atom.getAtomInfo() ) ) {
                            lewisDotModel.removeAtom( atom.getAtomInfo() );
                        }
                    }

                    @Override
                    public void droppedByUser( AtomModel atom ) {
                        // dropped on kit, put it in a bucket
                        if ( Kit.this.getAvailableKitBounds().contains( atom.getPosition().toPoint2D() ) ) {
                            Bucket bucket = Kit.this.getBucketForAtomType( atom.getAtomInfo() );
                            bucket.addAtom( atom, true );
                        }
                        else {
                            // dropped in play area
                            lewisDotModel.addAtom( atom.getAtomInfo() );
                            attemptToBondAtom( atom );
                        }
                    }
                } );
            }
        }

        /*---------------------------------------------------------------------------*
        * bucket layout
        *----------------------------------------------------------------------------*/

        double kitY = availableKitBounds.getCenterY();
        double kitXCenter = availableKitBounds.getCenterX();

        double usedWidth = 0;

        // lays out all of the buckets from the left to right
        for ( int i = 0; i < buckets.size(); i++ ) {
            Bucket bucket = buckets.get( i );
            if ( i != 0 ) {
                usedWidth += BUCKET_PADDING;
            }
            bucket.setPosition( new ImmutableVector2D( usedWidth, kitY ) );
            usedWidth += bucket.getWidth();
        }

        // centers the buckets horizontally within the kit
        for ( Bucket bucket : buckets ) {
            bucket.setPosition( new ImmutableVector2D( bucket.getPosition().getX() - usedWidth / 2 + kitXCenter + bucket.getWidth() / 2, kitY ) );
        }
    }

    public boolean isContainedInBucket( AtomModel atom ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.containsAtom( atom ) ) {
                return true;
            }
        }
        return false;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public Bucket getBucketForAtomType( Atom atom ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.getAtomType().isSameTypeOfAtom( atom ) ) {
                return bucket;
            }
        }
        throw new RuntimeException( "Bucket not found for atom type: " + atom );//oh noes
    }

    public PBounds getAvailableKitBounds() {
        return availableKitBounds;
    }

    /*---------------------------------------------------------------------------*
    * model implementation
    *----------------------------------------------------------------------------*/

    /**
     * @param atom An atom to bond if it is close to other atoms
     * @return Success
     */
    private boolean attemptToBondAtom( AtomModel atom ) {
        OpenLocation bestLocation = null;
        double bestDistanceFromIdealLocation = Double.POSITIVE_INFINITY;
        Atom atomInfo = atom.getAtomInfo();
        for ( AtomModel otherAtom : atoms ) {
            if ( otherAtom == atom ) {
                continue;
            }
            if ( !isContainedInBucket( otherAtom ) ) {
                for ( LewisDotModel.Direction direction : lewisDotModel.getOpenDirections( otherAtom.getAtomInfo() ) ) {
                    OpenLocation location = new OpenLocation( otherAtom, direction );
                    double distance = atom.getPosition().getDistance( location.getIdealLocation( atomInfo ) );
                    if ( distance < bestDistanceFromIdealLocation ) {
                        bestLocation = location;
                        bestDistanceFromIdealLocation = distance;
                    }
                }
            }
        }
        if ( bestLocation == null || bestDistanceFromIdealLocation > BOND_DISTANCE_THRESHOLD ) {
            return false;
        }

        // we now will bond the atom
        lewisDotModel.bond( bestLocation.atom.getAtomInfo(), bestLocation.direction, atomInfo ); // model bonding
        atom.setDestination( bestLocation.getIdealLocation( atomInfo ) ); // cause the atom to move to the location
        return true;
    }

    private boolean canBond( AtomModel a, AtomModel b ) {
        return true;
    }

    private static class OpenLocation {
        public final AtomModel atom;
        public final LewisDotModel.Direction direction;

        private OpenLocation( AtomModel atom, LewisDotModel.Direction direction ) {
            this.atom = atom;
            this.direction = direction;
        }

        /**
         * @param otherAtom A type of atom that could be placed here
         * @return The location the atom should be placed
         */
        public ImmutableVector2D getIdealLocation( Atom otherAtom ) {
            return atom.getPosition().getAddedInstance( direction.getVector().getScaledInstance( atom.getRadius() + otherAtom.getRadius() ) );
        }
    }
}
