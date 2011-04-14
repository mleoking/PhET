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
                                recycleAtomIntoBuckets( atom.getAtomInfo() );
                            }
                        }
                        else {
                            // dropped in play area
                            if ( isAtomInPlay( atom.getAtomInfo() ) ) {
                                attemptToBondMolecule( getMoleculeStructure( atom ) );
                            }
                            else {
                                addAtomToPlay( atom );
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

    /**
     * Takes an atom that was in a bucket and hooks it up within our structural model. It allocates a molecule for the
     * atom, and then attempts to bond with it.
     *
     * @param atom An atom to add into play
     */
    private void addAtomToPlay( final AtomModel atom ) {
        // add the atoms to our models
        MoleculeStructure moleculeStructure = new MoleculeStructure() {{
            addAtom( atom.getAtomInfo() );
        }};
        molecules.add( moleculeStructure );

        // attempt to bond
        attemptToBondMolecule( moleculeStructure );
    }

    /**
     * Takes an atom, invalidates the structural bonds it may have, and puts it in the correct bucket
     *
     * @param atom The atom to recycle
     */
    private void recycleAtomIntoBuckets( Atom atom ) {
        lewisDotModel.breakBondsOfAtom( atom );
        Bucket bucket = Kit.this.getBucketForAtomType( atom );
        bucket.addAtom( getAtomModel( atom ), true );
    }

    /**
     * Recycles an entire molecule by invalidating its bonds and putting its atoms into their respective buckets
     *
     * @param molecule The molecule to recycle
     */
    private void recycleMoleculeIntoBuckets( MoleculeStructure molecule ) {
        for ( Atom atom : molecule.getAtoms() ) {
            recycleAtomIntoBuckets( atom );
        }
        molecules.remove( molecule );
    }

    /**
     * Bonds one atom to another, and handles the corresponding structural changes between molecules.
     *
     * @param a       An atom A
     * @param dirAtoB The direction from A that the bond will go in (for lewis-dot structure)
     * @param b       An atom B
     */
    private void bond( AtomModel a, LewisDotModel.Direction dirAtoB, AtomModel b ) {
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

        // TODO: remove following dev testing checks and debugging statements. ONLY after testing molecule structure comparison
        assert ( getMoleculeStructure( a ) == getMoleculeStructure( b ) );
        MoleculeStructure molecule = getMoleculeStructure( a );
        for ( CompleteMolecule completeMolecule : CompleteMolecule.COMPLETE_MOLECULES ) {
            if ( molecule.isEquivalent( completeMolecule.getMoleculeStructure() ) ) {
                System.out.println( "You made: " + completeMolecule.getCommonName() );
            }
        }
    }

    /**
     * @param moleculeStructure A molecule that should attempt to bind to other atoms / molecules
     * @return Success
     */
    private boolean attemptToBondMolecule( MoleculeStructure moleculeStructure ) {
        BondingOption bestLocation = null;
        double bestDistanceFromIdealLocation = Double.POSITIVE_INFINITY;
        for ( Atom atomInfo : moleculeStructure.getAtoms() ) {
            AtomModel atom = getAtomModel( atomInfo );
            for ( AtomModel otherAtom : atoms ) {
                if ( otherAtom == atom || !canBond( atom, otherAtom ) ) {
                    continue;
                }
                if ( !isContainedInBucket( otherAtom ) ) {
                    for ( LewisDotModel.Direction direction : lewisDotModel.getOpenDirections( otherAtom.getAtomInfo() ) ) {
                        BondingOption location = new BondingOption( otherAtom, direction, atom );
                        double distance = atom.getPosition().getDistance( location.getIdealLocation() );
                        if ( distance < bestDistanceFromIdealLocation ) {
                            bestLocation = location;
                            bestDistanceFromIdealLocation = distance;
                        }
                    }
                }
            }
        }
        if ( bestLocation == null || bestDistanceFromIdealLocation > BOND_DISTANCE_THRESHOLD ) {
            return false;
        }

        // cause all atoms in the molecule to move to that location
        ImmutableVector2D delta = bestLocation.getIdealLocation().getSubtractedInstance( bestLocation.b.getPosition() );
        for ( Atom atomInMolecule : getMoleculeStructure( bestLocation.b ).getAtoms() ) {
            AtomModel atomModel = getAtomModel( atomInMolecule );
            atomModel.setDestination( atomModel.getPosition().getAddedInstance( delta ) );
        }

        // we now will bond the atom
        bond( bestLocation.a, bestLocation.direction, bestLocation.b ); // model bonding
        return true;
    }

    private boolean canBond( AtomModel a, AtomModel b ) {
        return getMoleculeStructure( a ) != getMoleculeStructure( b );
    }

    /**
     * A bond option from A to B. B would be moved to the location near A to bond.
     */
    private static class BondingOption {
        public final AtomModel a;
        public final LewisDotModel.Direction direction;
        public final AtomModel b;

        private BondingOption( AtomModel a, LewisDotModel.Direction direction, AtomModel b ) {
            this.a = a;
            this.direction = direction;
            this.b = b;
        }

        /**
         * @return The location the atom should be placed
         */
        public ImmutableVector2D getIdealLocation() {
            return a.getPosition().getAddedInstance( direction.getVector().getScaledInstance( a.getRadius() + b.getRadius() ) );
        }
    }
}
