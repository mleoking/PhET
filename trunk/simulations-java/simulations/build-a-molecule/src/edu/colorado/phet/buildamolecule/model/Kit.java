// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.buildamolecule.model.LewisDotModel.Direction;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.ModelAction;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.ModelComponent;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.ParameterKey;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.mkString;

/**
 * Contains multiple buckets of different types of atoms
 */
public class Kit {
    private final List<Bucket> buckets;
    private final List<Atom2D> atoms = new LinkedList<Atom2D>(); // our master list of atoms (in and out of buckets), but not ones in collection boxes
    private final List<Atom2D> atomsInCollectionBox = new LinkedList<Atom2D>(); // atoms in the collection box
    private LewisDotModel lewisDotModel; // lewis-dot connections between atoms on the play area
    private final Set<Molecule> molecules = new HashSet<Molecule>(); // molecule structures in the play area
    private final Set<Pair<Molecule, CollectionBox>> removedMolecules = new HashSet<Pair<Molecule, CollectionBox>>(); // molecule structures that were put into the collection box. kept for now, since modifying the reset behavior will be much easier if we retain this
    public final Property<Boolean> visible = new Property<Boolean>( false );
    public final Property<Boolean> hasMoleculesInBoxes = new Property<Boolean>( false ); // we record this so we know when the "reset kit" should be shown
    private LayoutBounds layoutBounds;

    private final List<MoleculeListener> moleculeListeners = new LinkedList<MoleculeListener>();

    public static final double BOND_DISTANCE_THRESHOLD = 200;
    public static final double BUCKET_PADDING = 50;
    public static final double INTER_MOLECULE_PADDING = 150;

    public Kit( final LayoutBounds layoutBounds, Bucket... buckets ) {
        this.buckets = new LinkedList<Bucket>( Arrays.asList( buckets ) );
        this.layoutBounds = layoutBounds;

        resetKit();

        // lays out the buckets correctly, and takes care of changing atom positions
        layoutBuckets( buckets );
    }

    public void resetKit() {
        // not resetting visible, since that is not handled by us
        hasMoleculesInBoxes.reset();

        // take molecules back from the collection boxes
//            for ( Pair<MoleculeStructure, CollectionBox> removedMolecule : removedMolecules ) {
//                removedMolecule._2.removeMolecule( removedMolecule._1 );
//            }

        // send out notifications for all removed molecules
        for ( Molecule molecule : new ArrayList<Molecule>( molecules ) ) {
            removeMolecule( molecule );

            SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.moleculeRemovedMisc,
                                                ParameterSet.parameterSet( ParameterKey.moleculeId, molecule.getMoleculeId() ) );
        }

        // put everything back in buckets
        atoms.addAll( atomsInCollectionBox );
        for ( Atom2D atom : atoms ) {
            // reset the actual atom
            atom.reset();

            // THEN place it so we overwrite its "bad" position and destination info
            getBucketForElement( atom.getElement() ).placeAtom( atom );
        }

        // if reset kit ignores collection boxes, add in other atoms that are equivalent to how the bucket started
        // NOTE: right now, the actual atom models move back to the buckets even though the virtual "molecule" says in the box. consider moving it!

        // wipe our internal state
        atoms.clear();
        atomsInCollectionBox.clear();
        lewisDotModel = new LewisDotModel();
        molecules.clear();
        removedMolecules.clear();

        // keep track of all atoms in our kit
        for ( Bucket bucket : buckets ) {
            atoms.addAll( bucket.getAtoms() );

            for ( Atom2D atom : bucket.getAtoms() ) {
                lewisDotModel.addAtom( atom );
            }
        }
    }

    private void layoutBuckets( Bucket[] buckets ) {
        double kitY = getAvailableKitBounds().getCenterY() - 20;
        double kitXCenter = getAvailableKitBounds().getCenterX();

        double usedWidth = 0;

        // lays out all of the buckets from the left to right
        for ( int i = 0; i < this.buckets.size(); i++ ) {
            Bucket bucket = this.buckets.get( i );
            if ( i != 0 ) {
                usedWidth += BUCKET_PADDING;
            }
            bucket.setPosition( new Point2D.Double( usedWidth, kitY ) );
            usedWidth += bucket.getWidth();
        }

        // centers the buckets horizontally within the kit
        for ( Bucket bucket : buckets ) {

            // also note: this moves the atoms also!
            bucket.setPosition( new Point2D.Double( bucket.getPosition().getX() - usedWidth / 2 + kitXCenter + bucket.getWidth() / 2, kitY ) );
        }
    }

    public void show() {
        visible.set( true );
    }

    public void hide() {
        visible.set( false );
    }

    public boolean isContainedInBucket( Atom2D atom ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.containsParticle( atom ) ) {
                return true;
            }
        }
        return false;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public List<Atom2D> getAtoms() {
        return atoms;
    }

    public Bucket getBucketForElement( Element element ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.getElement().isSameElement( element ) ) {
                return bucket;
            }
        }
        throw new RuntimeException( "Bucket not found for element: " + element );//oh noes
    }

    public PBounds getAvailableKitBounds() {
        return layoutBounds.getAvailableKitBounds();
    }

    public PBounds getAvailablePlayAreaBounds() {
        return layoutBounds.getAvailablePlayAreaBounds();
    }

    /**
     * Called when an atom is dropped within either the play area OR the kit area. This will NOT be called for molecules
     * dropped into the collection area successfully
     *
     * @param atom The dropped atom.
     */
    public void atomDropped( Atom2D atom ) {
        // dropped on kit, put it in a bucket
        boolean wasInPlay = isAtomInPlay( atom );
        boolean droppedInKitArea = getAvailableKitBounds().contains( atom.getPosition().toPoint2D() );
        SimSharingManager.sendModelMessage( ModelComponent.atom, ModelComponentTypes.modelElement, ModelAction.atomDropped,
                                            ParameterSet.parameterSet( ParameterKey.atomWasInKitArea, !wasInPlay )
                                                    .with( ParameterKey.atomDroppedInKitArea, droppedInKitArea )
                                                    .with( ParameterKey.atomId, atom.getId() )
                                                    .with( ParameterKey.atomElement, atom.getSymbol() ) );
        if ( droppedInKitArea ) {
            if ( wasInPlay ) {
                recycleMoleculeIntoBuckets( getMolecule( atom ) );
            }
            else {
                recycleAtomIntoBuckets( atom, true ); // animate
            }
        }
        else {
            // dropped in play area
            if ( wasInPlay ) {
                attemptToBondMolecule( getMolecule( atom ) );
                separateMoleculeDestinations();
            }
            else {
                addAtomToPlay( atom );
            }
        }
    }

    /**
     * Called when an atom is dragged, with the corresponding delta
     *
     * @param atom  Atom that was dragged
     * @param delta How far it was dragged (the delta)
     */
    public void atomDragged( Atom2D atom, Vector2D delta ) {
        // move our atom
        atom.translatePositionAndDestination( delta );

        // move all other atoms in the molecule
        if ( isAtomInPlay( atom ) ) {
            for ( Atom2D atomInMolecule : getMolecule( atom ).getAtoms() ) {
                if ( atom == atomInMolecule ) {
                    continue;
                }
                atomInMolecule.translatePositionAndDestination( delta );
            }
        }
    }

    /**
     * Called when a molecule is dragged (successfully) into a collection box
     *
     * @param molecule The molecule
     * @param box      Its collection box
     */
    public void moleculePutInCollectionBox( Molecule molecule, CollectionBox box ) {
        System.out.println( "You have collected: " + box.getMoleculeType().getCommonName() );
        hasMoleculesInBoxes.set( true );
        removeMolecule( molecule );
        for ( Atom2D atomModel : molecule.getAtoms() ) {
            atoms.remove( atomModel );
            atomsInCollectionBox.add( atomModel );
            atomModel.visible.set( false );
        }
        box.addMolecule( molecule );
        removedMolecules.add( new Pair<Molecule, CollectionBox>( molecule, box ) );
    }

    /**
     * @param atom An atom
     * @return Is this atom registered in our molecule structures?
     */
    public boolean isAtomInPlay( Atom atom ) {
        return getMolecule( atom ) != null;
    }

    public Molecule getMolecule( Atom atom ) {
        for ( Molecule molecule : molecules ) {
            for ( Atom otherAtom : molecule.getAtoms() ) {
                if ( otherAtom == atom ) {
                    return molecule;
                }
            }
        }
        return null;
    }

    /**
     * Breaks apart a molecule into separate atoms that remain in the play area
     *
     * @param molecule The molecule to break
     */
    public void breakMolecule( Molecule molecule ) {
        List<Molecule> createdMolecules = new ArrayList<Molecule>();

        removeMolecule( molecule );
        for ( final Atom2D atom : molecule.getAtoms() ) {
            lewisDotModel.breakBondsOfAtom( atom );
            Molecule newMolecule = new Molecule() {{
                addAtom( atom );
            }};
            addMolecule( newMolecule );
            createdMolecules.add( molecule );
        }
        separateMoleculeDestinations();

        SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.moleculeBroken, new ParameterSet()
                .with( ParameterKey.moleculeDestroyed, molecule.getMoleculeId() )
                .with( ParameterKey.moleculesCreated, mkString( createdMolecules, new Function1<Molecule, String>() {
                    public String apply( Molecule molecule ) {
                        return Integer.toString( molecule.getMoleculeId() );
                    }
                }, "," ) ) );
    }

    /**
     * Breaks a bond between two atoms in a molecule.
     *
     * @param a Atom A
     * @param b Atom B
     */
    public void breakBond( Atom2D a, Atom2D b ) {
        // get our old and new molecule structures
        Molecule oldMolecule = getMolecule( a );
        List<Molecule> newMolecules = MoleculeStructure.getMoleculesFromBrokenBond( oldMolecule, oldMolecule.getBond( a, b ), new Molecule(), new Molecule() );

        // break the bond in our lewis dot model
        lewisDotModel.breakBond( a, b );

        // remove the old one, add the new ones (firing listeners)
        removeMolecule( oldMolecule );
        for ( Molecule molecule : newMolecules ) {
            addMolecule( molecule );
        }

        SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.bondBroken, new ParameterSet()
                .with( ParameterKey.moleculeDestroyed, oldMolecule.getMoleculeId() )
                .with( ParameterKey.moleculesCreated, mkString( newMolecules, new Function1<Molecule, String>() {
                    public String apply( Molecule molecule ) {
                        return Integer.toString( molecule.getMoleculeId() );
                    }
                }, "," ) )
                .with( ParameterKey.bondAtomA, a.getId() )
                .with( ParameterKey.bondAtomB, b.getId() ) );

        // push the new separate molecules away
        separateMoleculeDestinations();
    }

    public void addMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.add( listener );
    }

    public void removeMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.remove( listener );
    }

    public LewisDotModel.Direction getBondDirection( Atom a, Atom b ) {
        return lewisDotModel.getBondDirection( a, b );
    }

    public boolean hasAtomsOutsideOfBuckets() {
        return !molecules.isEmpty() || hasMoleculesInBoxes.get();
    }

    public static interface MoleculeListener {
        public void addedMolecule( Molecule molecule );

        public void removedMolecule( Molecule molecule );
    }

    public static class MoleculeAdapter implements MoleculeListener {
        public void addedMolecule( Molecule molecule ) {
        }

        public void removedMolecule( Molecule molecule ) {
        }
    }

    /*---------------------------------------------------------------------------*
    * model implementation
    *----------------------------------------------------------------------------*/

    private void addMolecule( Molecule molecule ) {
        molecules.add( molecule );
        for ( MoleculeListener listener : moleculeListeners ) {
            listener.addedMolecule( molecule );
        }

        if ( SimSharingManager.getInstance().isEnabled() ) {
            // somewhat of a state-dump

            // sort the atom list, so it's extremely easy to compare whether they are the same or not
            ArrayList<Atom2D> sortedAtoms = new ArrayList<Atom2D>( molecule.getAtoms() );
            Collections.sort( sortedAtoms, new Comparator<Atom2D>() {
                public int compare( Atom2D a, Atom2D b ) {
                    return Double.compare( a.hashCode(), b.hashCode() );
                }
            } );

            ParameterSet parameters = new ParameterSet()
                    .with( ParameterKey.moleculeId, molecule.getMoleculeId() )
                    .with( ParameterKey.atomIds, mkString( sortedAtoms, new Function1<Atom2D, String>() {
                        public String apply( Atom2D atom2D ) {
                            return atom2D.getId();
                        }
                    }, "," ) )
                    .with( ParameterKey.bonds, mkString( molecule.getBonds(), new Function1<Bond<Atom2D>, String>() {
                        public String apply( Bond<Atom2D> bond ) {
                            return bond.a.getId() + "-" + bond.b.getId();
                        }
                    }, "," ) )
                    .with( ParameterKey.moleculeSerial2, molecule.toSerial2() )
                    .with( ParameterKey.moleculeGeneralFormula, molecule.getGeneralFormula() );

            // check it against complete molecules, so we can confirm any information
            CompleteMolecule completeMolecule = molecule.getMatchingCompleteMolecule();
            parameters = parameters.with( ParameterKey.moleculeIsCompleteMolecule, completeMolecule != null );
            if ( completeMolecule != null ) {
                parameters = parameters
                        .with( ParameterKey.completeMoleculeMolecularFormula, completeMolecule.getMolecularFormula() )
                        .with( ParameterKey.completeMoleculeCommonName, completeMolecule.getCommonName() )
                        .with( ParameterKey.completeMoleculeSerial2, completeMolecule.toSerial2() )
                        .with( ParameterKey.completeMoleculeCID, completeMolecule.getCID() );
            }

            SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.moleculeAdded, parameters );
        }
    }

    private void removeMolecule( Molecule molecule ) {
        molecules.remove( molecule );
        for ( MoleculeListener listener : moleculeListeners ) {
            listener.removedMolecule( molecule );
        }

        SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.moleculeRemoved,
                                            ParameterSet.parameterSet( ParameterKey.moleculeId, molecule.getMoleculeId() ) );
    }

    /**
     * Takes an atom that was in a bucket and hooks it up within our structural model. It allocates a molecule for the
     * atom, and then attempts to bond with it.
     *
     * @param atom An atom to add into play
     */
    private void addAtomToPlay( final Atom2D atom ) {
        // add the atoms to our models
        Molecule molecule = new Molecule() {{
            addAtom( atom );
        }};

        addMolecule( molecule );
        SimSharingManager.sendModelMessage( ModelComponent.atom, ModelComponentTypes.modelElement, ModelAction.atomAddedIntoPlay, new ParameterSet()
                .with( ParameterKey.atomId, atom.getId() )
                .with( ParameterKey.atomElement, atom.getSymbol() )
                .with( ParameterKey.moleculeId, molecule.getMoleculeId() ) );

        // attempt to bond
        attemptToBondMolecule( molecule );
    }

    /**
     * Takes an atom, invalidates the structural bonds it may have, and puts it in the correct bucket
     *
     * @param atom    The atom to recycle
     * @param animate Whether we should display animation
     */
    private void recycleAtomIntoBuckets( Atom2D atom, boolean animate ) {
        lewisDotModel.breakBondsOfAtom( atom );
        Bucket bucket = getBucketForElement( atom.getElement() );
        bucket.addParticleNearestOpen( atom, !animate );
    }

    /**
     * Recycles an entire molecule by invalidating its bonds and putting its atoms into their respective buckets
     *
     * @param molecule The molecule to recycle
     */
    private void recycleMoleculeIntoBuckets( Molecule molecule ) {
        for ( Atom2D atom : molecule.getAtoms() ) {
            recycleAtomIntoBuckets( atom, true );
        }
        removeMolecule( molecule );

        SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.moleculeRecycled,
                                            ParameterSet.parameterSet( ParameterKey.moleculeId, molecule.getMoleculeId() ) );
    }

    private PBounds padMoleculeBounds( PBounds bounds ) {
        double halfPadding = INTER_MOLECULE_PADDING / 2;
        return new PBounds( bounds.x - halfPadding, bounds.y - halfPadding, bounds.width + INTER_MOLECULE_PADDING, bounds.height + INTER_MOLECULE_PADDING );
    }

    /**
     * Update atom destinations so that separate molecules will be separated visually
     * <p/>
     * Returns all of the molecules that were pushed in this action
     */
    private Set<Molecule> separateMoleculeDestinations() {
        int maxIterations = 500;
        double pushAmount = 10; // how much to push two molecules away

        Set<Molecule> pushedMolecules = new HashSet<Molecule>();

        boolean foundOverlap = true;
        while ( foundOverlap && maxIterations-- >= 0 ) {
            foundOverlap = false;
            for ( Molecule a : molecules ) {
                PBounds aBounds = padMoleculeBounds( a.getDestinationBounds() );

                // push it away from the outsides
                if ( aBounds.getMinX() < getAvailablePlayAreaBounds().getMinX() ) {
                    a.shiftDestination( new Vector2D( getAvailablePlayAreaBounds().getMinX() - aBounds.getMinX(), 0 ) );
                    aBounds = padMoleculeBounds( a.getDestinationBounds() );
                }
                if ( aBounds.getMaxX() > getAvailablePlayAreaBounds().getMaxX() ) {
                    a.shiftDestination( new Vector2D( getAvailablePlayAreaBounds().getMaxX() - aBounds.getMaxX(), 0 ) );
                    aBounds = padMoleculeBounds( a.getDestinationBounds() );
                }
                if ( aBounds.getMinY() < getAvailablePlayAreaBounds().getMinY() ) {
                    a.shiftDestination( new Vector2D( 0, getAvailablePlayAreaBounds().getMinY() - aBounds.getMinY() ) );
                    aBounds = padMoleculeBounds( a.getDestinationBounds() );
                }
                if ( aBounds.getMaxY() > getAvailablePlayAreaBounds().getMaxY() ) {
                    a.shiftDestination( new Vector2D( 0, getAvailablePlayAreaBounds().getMaxY() - aBounds.getMaxY() ) );
                }

                // then separate it from other molecules
                for ( Molecule b : molecules ) {
                    if ( a.getMoleculeId() >= b.getMoleculeId() ) {
                        // this removes the case where a == b, and will make sure we don't run the following code twice for (a,b) and (b,a)
                        continue;
                    }
                    PBounds bBounds = padMoleculeBounds( b.getDestinationBounds() );
                    if ( aBounds.intersects( bBounds ) ) {
                        foundOverlap = true;

                        // try adding both. set should remove duplicates
                        pushedMolecules.add( a );
                        pushedMolecules.add( b );

                        // get perturbed centers. this is so that if two molecules have the exact same centers, we will push them away
                        Vector2D aCenter = new Vector2D( aBounds.getCenter2D() ).plus( Math.random() - 0.5, Math.random() - 0.5 );
                        Vector2D bCenter = new Vector2D( bBounds.getCenter2D() ).plus( Math.random() - 0.5, Math.random() - 0.5 );

                        // delta from center of A to center of B, scaled to half of our push amount.
                        Vector2D delta = bCenter.minus( aCenter ).normalized().times( pushAmount );

                        // how hard B should be pushed (A will be pushed (1-pushRatio)). Heuristic, power is to make the ratio not too skewed
                        // this is done so that heavier molecules will be pushed less, while lighter ones will be pushed more
                        double pushPower = 1;
                        double pushRatio = Math.pow( a.getApproximateMolecularWeight(), pushPower ) / ( Math.pow( a.getApproximateMolecularWeight(), pushPower ) + Math.pow( b.getApproximateMolecularWeight(), pushPower ) );

                        // push B by the pushRatio
                        b.shiftDestination( delta.times( pushRatio ) );

                        // push A the opposite way, by (1 - pushRatio)
                        Vector2D delta1 = delta.times( -1 * ( 1 - pushRatio ) );
                        a.shiftDestination( delta1 );

                        aBounds = padMoleculeBounds( a.getDestinationBounds() );
                    }
                }
            }
        }

        return pushedMolecules;
    }

    /**
     * Bonds one atom to another, and handles the corresponding structural changes between molecules.
     *
     * @param a       An atom A
     * @param dirAtoB The direction from A that the bond will go in (for lewis-dot structure)
     * @param b       An atom B
     */
    private void bond( Atom2D a, LewisDotModel.Direction dirAtoB, Atom2D b ) {
        lewisDotModel.bond( a, dirAtoB, b );
        Molecule molA = getMolecule( a );
        Molecule molB = getMolecule( b );
        if ( molA == molB ) {
            throw new RuntimeException( "WARNING: loop or other invalid structure detected in a molecule" );
        }

        Molecule newMolecule = MoleculeStructure.getCombinedMoleculeFromBond( molA, molB, a, b, new Molecule() );

        // sanity check and debugging information
        if ( !newMolecule.isValid() ) {
            System.out.println( "invalid molecule!" );
            System.out.println( "bonding: " + a.getSymbol() + "(" + a.hashCode() + "), " + dirAtoB + " "
                                + b.getSymbol() + " (" + b.hashCode() + ")" );
            System.out.println( "A" );
            System.out.println( molA.getDebuggingDump() );
            System.out.println( "B" );
            System.out.println( molB.getDebuggingDump() );
            System.out.println( "combined" );
            System.out.println( newMolecule.getDebuggingDump() );

            System.out.println( "found: " + newMolecule.isAllowedStructure() );

            // just exit out for now
            return;
        }

        removeMolecule( molA );
        removeMolecule( molB );
        addMolecule( newMolecule );

        /*---------------------------------------------------------------------------*
        * bonding diagnostics and sanity checks
        *----------------------------------------------------------------------------*/

        String serializedForm = getMolecule( a ).toSerial2();
        System.out.println( "created structure: " + serializedForm );
        SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.bonding, new ParameterSet()
                .with( ParameterKey.bondMoleculeDestroyedA, molA.getMoleculeId() )
                .with( ParameterKey.bondMoleculeDestroyedB, molB.getMoleculeId() )
                .with( ParameterKey.bondMoleculeCreated, newMolecule.getMoleculeId() )
                .with( ParameterKey.moleculeStructureDestroyedA, molA.toSerial2() )
                .with( ParameterKey.moleculeStructureDestroyedB, molB.toSerial2() )
                .with( ParameterKey.moleculeStructureCreated, newMolecule.toSerial2() )
                .with( ParameterKey.bondAtomA, a.getId() )
                .with( ParameterKey.bondAtomB, b.getId() )
                .with( ParameterKey.bondDirection, dirAtoB.toString() ) );
        Molecule struc = getMolecule( a );
        if ( struc.getAtoms().size() > 2 ) {
            for ( Bond<Atom2D> bond : struc.getBonds() ) {
                if ( bond.a.hasSameElement( bond.b ) && bond.a.getSymbol().equals( "H" ) ) {
                    System.out.println( "WARNING: Hydrogen bonded to another hydrogen in a molecule which is not diatomic hydrogen" );
                }
            }
        }

        assert ( getMolecule( a ) == getMolecule( b ) );
    }

    private Molecule getPossibleMoleculeStructureFromBond( Atom2D a, Atom2D b ) {
        Molecule molA = getMolecule( a );
        Molecule molB = getMolecule( b );
        assert ( molA != molB );

        return MoleculeStructure.getCombinedMoleculeFromBond( molA, molB, a, b, new Molecule() );
    }

    /**
     * @param molecule A molecule that should attempt to bind to other atoms / molecules
     * @return Success
     */
    private boolean attemptToBondMolecule( Molecule molecule ) {
        BondingOption bestLocation = null;
        double bestDistanceFromIdealLocation = Double.POSITIVE_INFINITY;

        // for each atom in our molecule, we try to see if it can bond to other atoms
        for ( Atom2D ourAtom : molecule.getAtoms() ) {

            // all other atoms
            for ( Atom2D otherAtom : atoms ) {
                // disallow loops in an already-connected molecule
                if ( getMolecule( otherAtom ) == molecule ) {
                    continue;
                }

                // don't bond to something in a bucket!
                if ( !isContainedInBucket( otherAtom ) ) {

                    // sanity check, and run it through our molecule structure model to see if it would be allowable
                    if ( otherAtom == ourAtom || !canBond( ourAtom, otherAtom ) ) {
                        continue;
                    }

                    for ( LewisDotModel.Direction otherDirection : lewisDotModel.getOpenDirections( otherAtom ) ) {
                        Direction direction = Direction.opposite( otherDirection );
                        if ( !lewisDotModel.getOpenDirections( ourAtom ).contains( direction ) ) {
                            // the spot on otherAtom was open, but the corresponding spot on our main atom was not
                            continue;
                        }

                        // check the lewis dot model to make sure we wouldn't have two "overlapping" atoms that aren't both hydrogen
                        if ( !lewisDotModel.willAllowBond( ourAtom, direction, otherAtom ) ) {
                            continue;
                        }

                        BondingOption location = new BondingOption( otherAtom, otherDirection, ourAtom );
                        double distance = ourAtom.getPosition().distance( location.getIdealLocation() );
                        if ( distance < bestDistanceFromIdealLocation ) {
                            bestLocation = location;
                            bestDistanceFromIdealLocation = distance;
                        }
                    }
                }
            }
        }

        // if our closest bond is too far, then ignore it
        boolean isBondingInvalid = bestLocation == null || bestDistanceFromIdealLocation > BOND_DISTANCE_THRESHOLD;

        ParameterSet baseParameterSet = ParameterSet.parameterSet( ParameterKey.bondOccurs, !isBondingInvalid )
                .with( ParameterKey.moleculeId, molecule.getMoleculeId() );
        SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.bondAttempt,
                                            isBondingInvalid ? baseParameterSet : baseParameterSet
                                                    .with( ParameterKey.bondAtomA, bestLocation.a.getId() )
                                                    .with( ParameterKey.bondAtomB, bestLocation.b.getId() )
                                                    .with( ParameterKey.bondDirection, bestLocation.direction.toString() )
                                                    .with( ParameterKey.bondMoleculeIdA, getMolecule( bestLocation.a ).getMoleculeId() )
                                                    .with( ParameterKey.bondMoleculeIdB, getMolecule( bestLocation.b ).getMoleculeId() ) );
        if ( isBondingInvalid ) {
            Set<Molecule> pushedMolecules = separateMoleculeDestinations();

            SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.moleculeStatusAfterDrop, new ParameterSet()
                    .with( ParameterKey.bondOccurs, false )
                    .with( ParameterKey.moleculeRepulsed, pushedMolecules.contains( molecule ) )
                    .with( ParameterKey.moleculeId, molecule.getMoleculeId() )
                    .with( ParameterKey.moleculesRepulsed, mkString( pushedMolecules, new Function1<Molecule, String>() {
                        public String apply( Molecule molecule ) {
                            return Integer.toString( molecule.getMoleculeId() );
                        }
                    }, "," ) ) );
            return false;
        }
        else {
            SimSharingManager.sendModelMessage( ModelComponent.molecule, ModelComponentTypes.modelElement, ModelAction.moleculeStatusAfterDrop, new ParameterSet()
                    .with( ParameterKey.bondOccurs, true )
                    .with( ParameterKey.moleculeRepulsed, false )
                    .with( ParameterKey.moleculeId, molecule.getMoleculeId() ) );
        }

        // cause all atoms in the molecule to move to that location
        Vector2D delta = bestLocation.getIdealLocation().minus( bestLocation.b.getPosition() );
        for ( Atom2D atomInMolecule : getMolecule( bestLocation.b ).getAtoms() ) {
            atomInMolecule.setDestination( atomInMolecule.getPosition().plus( delta ) );
        }

        // we now will bond the atom
        bond( bestLocation.a, bestLocation.direction, bestLocation.b ); // model bonding
        return true;
    }

    private boolean canBond( Atom2D a, Atom2D b ) {
        return getMolecule( a ) != getMolecule( b ) && getPossibleMoleculeStructureFromBond( a, b ).isAllowedStructure();
    }

    /**
     * A bond option from A to B. B would be moved to the location near A to bond.
     */
    private static class BondingOption {
        public final Atom2D a;
        public final LewisDotModel.Direction direction;
        public final Atom2D b;

        private BondingOption( Atom2D a, LewisDotModel.Direction direction, Atom2D b ) {
            this.a = a;
            this.direction = direction;
            this.b = b;
        }

        /**
         * @return The location the atom should be placed
         */
        public Vector2D getIdealLocation() {
            return a.getPosition().plus( direction.getVector().times( a.getRadius() + b.getRadius() ) );
        }
    }
}
