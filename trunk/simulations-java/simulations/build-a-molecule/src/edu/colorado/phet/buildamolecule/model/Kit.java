// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    private final Set<MoleculeStructure> molecules = new HashSet<MoleculeStructure>(); // molecule structures in the play area
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
                lewisDotModel.addAtom( atom.getAtomInfo() );
                atom.addListener( new AtomModel.Adapter() {
                    @Override
                    public void grabbedByUser( AtomModel atom ) {
                        // TODO: remove from bucket??? check for leaks here
                    }

                    @Override
                    public void droppedByUser( AtomModel atom ) {
                        // dropped on kit, put it in a bucket
                        if ( Kit.this.getAvailableKitBounds().contains( atom.getPosition().toPoint2D() ) ) {
                            if ( isAtomInPlay( atom.getAtomInfo() ) ) {
                                recycleMoleculeIntoBuckets( getMoleculeStructure( atom ) );
                            }
                            else {
                                recycleAtom( atom.getAtomInfo() );
                            }
                        }
                        else {
                            // dropped in play area
                            if ( getMoleculeStructure( atom ) == null ) {
                                addAtomToPlay( atom );
                            }
                            else {
                                // TODO: attempt to bond entire molecules at a time
                                attemptToBondAtom( atom );
                            }
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

    /**
     * Called when an atom is dragged, with the corresponding delta
     *
     * @param atom  Atom that was dragged
     * @param delta How far it was dragged (the delta)
     */
    public void atomDragged( AtomModel atom, ImmutableVector2D delta ) {
        // move our atom
        atom.setPositionAndDestination( atom.getPosition().getAddedInstance( delta ) );

        // move all other atoms in the molecule
        if ( isAtomInPlay( atom.getAtomInfo() ) ) {
            for ( Atom atomInMolecule : getMoleculeStructure( atom ).getAtoms() ) {
                if ( atom.getAtomInfo() == atomInMolecule ) {
                    continue;
                }
                AtomModel atomModel = getAtomModel( atomInMolecule );
                atomModel.setPositionAndDestination( atomModel.getPosition().getAddedInstance( delta ) );
            }
        }
    }

    /**
     * @param atom An atom
     * @return Is this atom registered in our molecule structures?
     */
    public boolean isAtomInPlay( Atom atom ) {
        return getMoleculeStructure( atom ) != null;
    }

    public MoleculeStructure getMoleculeStructure( AtomModel atom ) {
        return getMoleculeStructure( atom.getAtomInfo() );
    }

    public MoleculeStructure getMoleculeStructure( Atom atom ) {
        for ( MoleculeStructure molecule : molecules ) {
            for ( Atom otherAtom : molecule.getAtoms() ) {
                if ( otherAtom == atom ) {
                    return molecule;
                }
            }
        }
        return null;
    }

    public AtomModel getAtomModel( Atom atom ) {
        for ( AtomModel atomModel : atoms ) {
            if ( atomModel.getAtomInfo() == atom ) {
                return atomModel;
            }
        }
        throw new RuntimeException( "atom model not found" );
    }

    /*---------------------------------------------------------------------------*
    * model implementation
    *----------------------------------------------------------------------------*/

    private void addAtomToPlay( final AtomModel atom ) {
        // add the atoms to our models
        molecules.add( new MoleculeStructure() {{
            addAtom( atom.getAtomInfo() );
        }} );

        // attempt to bond
        attemptToBondAtom( atom );
    }

    private void recycleAtom( Atom atom ) {
        lewisDotModel.breakBondsOfAtom( atom );
        Bucket bucket = Kit.this.getBucketForAtomType( atom );
        bucket.addAtom( getAtomModel( atom ), true );
    }

    private void recycleMoleculeIntoBuckets( MoleculeStructure molecule ) {
        for ( Atom atom : molecule.getAtoms() ) {
            recycleAtom( atom );
        }
        molecules.remove( molecule );
    }

    private void bond( AtomModel a, LewisDotModel.Direction dirAtoB, AtomModel b ) {
        System.out.println( "Bonding " + a + " to " + b + ", dir: " + dirAtoB );
        lewisDotModel.bond( a.getAtomInfo(), dirAtoB, b.getAtomInfo() );
        MoleculeStructure molA = getMoleculeStructure( a );
        MoleculeStructure molB = getMoleculeStructure( b );
        if ( molA == molB ) {
            // same molecule already, so just bind together the atoms
            molA.addBond( a.getAtomInfo(), b.getAtomInfo() );
            System.out.println( "WARNING: loop or other invalid structure detected in a molecule" );
        }
        else {
            molecules.remove( molA );
            molecules.remove( molB );
            molecules.add( MoleculeStructure.bondTogether( molA, molB, a.getAtomInfo(), b.getAtomInfo() ) );
        }
    }

    /**
     * @param atom An atom to bond if it is close to other atoms
     * @return Success
     */
    private boolean attemptToBondAtom( AtomModel atom ) {
        OpenLocation bestLocation = null;
        double bestDistanceFromIdealLocation = Double.POSITIVE_INFINITY;
        Atom atomInfo = atom.getAtomInfo();
        for ( AtomModel otherAtom : atoms ) {
            if ( otherAtom == atom || !canBond( atom, otherAtom ) ) {
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

        // cause all atoms in the molecule to move to that location
        ImmutableVector2D delta = bestLocation.getIdealLocation( atomInfo ).getSubtractedInstance( atom.getPosition() );
        for ( Atom atomInMolecule : getMoleculeStructure( atom ).getAtoms() ) {
            System.out.println( "moving" );
            AtomModel atomModel = getAtomModel( atomInMolecule );
            atomModel.setDestination( atomModel.getPosition().getAddedInstance( delta ) );
        }

        // we now will bond the atom
        bond( bestLocation.atom, bestLocation.direction, atom ); // model bonding
        return true;
    }

    private boolean canBond( AtomModel a, AtomModel b ) {
        return getMoleculeStructure( a ) != getMoleculeStructure( b );
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
