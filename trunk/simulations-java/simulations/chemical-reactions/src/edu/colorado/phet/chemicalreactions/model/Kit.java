// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.combinations;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.filter;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.unique;

// TODO: doc, and add the reaction "target" or "targets"
public class Kit {
    private final List<MoleculeBucket> buckets;

    private final List<MoleculeBucket> reactantBuckets;
    private final List<MoleculeBucket> productBuckets;
    private final List<ReactionShape> possibleReactions;

    private final List<Atom> atoms = new LinkedList<Atom>();
    private final Set<Molecule> molecules = new HashSet<Molecule>();
    private final ObservableList<Molecule> moleculesInPlayArea = new ObservableList<Molecule>();
    public final Property<Boolean> visible = new Property<Boolean>( false );
    public final Property<Boolean> hasMoleculesInBoxes = new Property<Boolean>( false );

    private final LayoutBounds layoutBounds;

    private final List<MoleculeListener> moleculeListeners = new LinkedList<MoleculeListener>();

    private final Box2dModel box2dModel;

    private final List<Reaction> reactions = new ArrayList<Reaction>();
    private final Map<Molecule, Reaction> reactionMap = new HashMap<Molecule, Reaction>();

    public final VoidNotifier stepCompleted = new VoidNotifier();

    public Kit( final LayoutBounds layoutBounds,
                final List<MoleculeBucket> reactantBuckets,
                final List<MoleculeBucket> productBuckets,
                final List<ReactionShape> possibleReactions ) {
        this.reactantBuckets = reactantBuckets;
        this.productBuckets = productBuckets;
        this.possibleReactions = possibleReactions;

        box2dModel = new Box2dModel( layoutBounds );

        this.buckets = new ArrayList<MoleculeBucket>() {{
            addAll( reactantBuckets );
            addAll( productBuckets );
        }};
        this.layoutBounds = layoutBounds;

        resetKit();

        // lays out the buckets correctly, and takes care of changing atom positions
        layout();
    }

    public void layout() {
        double kitY = getLayoutBounds().getAvailableKitModelBounds().getCenterY() - 20;
        double kitXCenter = getLayoutBounds().getAvailableKitModelBounds().getCenterX();

        double usedWidth = 0;

        double BUCKET_PADDING_PLUS = 300;
        double BUCKET_PADDING_ARROW = 450;

        // lays out all of the buckets from the left to right
        for ( int i = 0; i < this.buckets.size(); i++ ) {
            MoleculeBucket bucket = this.buckets.get( i );
            if ( i != 0 ) {
                if ( bucket == getProductBuckets().get( 0 ) ) {
                    // padding between reactants and products
                    usedWidth += BUCKET_PADDING_ARROW;
                }
                else {
                    // padding between two reactants or two products (for plus)
                    usedWidth += BUCKET_PADDING_PLUS;
                }
            }
            bucket.setPosition( new Point2D.Double( usedWidth, kitY ) );
            usedWidth += bucket.getSize().getWidth();
        }

        // centers the buckets horizontally within the kit
        for ( MoleculeBucket bucket : buckets ) {

            // also note: this moves the atoms also!
            bucket.setPosition( new Point2D.Double( bucket.getPosition().getX() - usedWidth / 2 + kitXCenter + bucket.getSize().getWidth() / 2, kitY ) );
        }
    }

    public void resetKit() {
        // TODO fill in here
    }

    public void show() {
        visible.set( true );
    }

    public void hide() {
        visible.set( false );
    }

    public void tick( double simulationTimeChange ) {
        /*---------------------------------------------------------------------------*
        * try to identify new reactions (potential collisions)
        *----------------------------------------------------------------------------*/
        for ( ReactionShape reactionShape : possibleReactions ) {
            // TODO: replace with a more performant version
            if ( moleculesInPlayArea.size() >= reactionShape.reactantSpots.size() ) {
                for ( List<Molecule> reactants : combinations( moleculesInPlayArea, reactionShape.reactantSpots.size() ) ) {
                    // for now, verify that the molecule types match up
                    boolean matches = true;
                    for ( int i = 0; i < reactionShape.reactantSpots.size(); i++ ) {
                        if ( reactionShape.reactantSpots.get( i ).shape != reactants.get( i ).shape ) {
                            matches = false;
                            break;
                        }
                    }
                    if ( !matches ) {
                        continue;
                    }
                    // compute the reaction's properties
                    final Reaction potentialReaction = new Reaction( reactionShape, reactants );
                    potentialReaction.update();

                    if ( potentialReaction.getFitness() <= 0 ) {
                        continue;
                    }
                    // we need to check for all reactions that are currently in place and would use the molecules we want
                    List<Reaction> replaceableReactions = filter( unique( map( reactants, new Function1<Molecule, Reaction>() {
                        public Reaction apply( Molecule molecule ) {
                            return reactionMap.get( molecule );
                        }
                    } ) ), new Function1<Reaction, Boolean>() {
                        public Boolean apply( Reaction reaction ) {
                            return reaction != null;
                        }
                    } );

                    // only if we are better than every other reaction with those molecules do we become active
                    if ( FunctionalUtils.every( replaceableReactions, new Function1<Reaction, Boolean>() {
                        public Boolean apply( Reaction reaction ) {
                            return potentialReaction.getFitness() > reaction.getFitness();
                        }
                    } ) ) {
                        for ( Reaction replaceableReaction : replaceableReactions ) {
                            removeReaction( replaceableReaction );
                        }
                        addReaction( potentialReaction );
                    }
                }
            }
        }


        /*---------------------------------------------------------------------------*
        * tweak velocities based on reactions
        *----------------------------------------------------------------------------*/
        for ( Reaction reaction : reactions ) {
            reaction.tweak( simulationTimeChange );
        }

        /*---------------------------------------------------------------------------*
        * run the physics
        *----------------------------------------------------------------------------*/
        box2dModel.tick( simulationTimeChange );

        /*---------------------------------------------------------------------------*
        * update the reaction targets
        *----------------------------------------------------------------------------*/
        for ( Reaction reaction : new ArrayList<Reaction>( reactions ) ) {
            reaction.update();

            // kick out now-invalid reactions
            if ( reaction.getFitness() == 0 ) {
                removeReaction( reaction );
            }
        }

        /*---------------------------------------------------------------------------*
        * notify
        *----------------------------------------------------------------------------*/
        stepCompleted.updateListeners();
    }

    public void dragStart( Molecule molecule ) {
        if ( molecule.getBody() != null ) {
            box2dModel.removeBody( molecule );
            moleculesInPlayArea.remove( molecule );
        }
    }

    public void dragEnd( Molecule molecule ) {
        ImmutableVector2D position = molecule.position.get();
        if ( layoutBounds.getAvailablePlayAreaModelBounds().contains( position.getX(), position.getY() ) ) {
            box2dModel.addBody( molecule );
            moleculesInPlayArea.add( molecule );
        }
    }

    private void addReaction( Reaction reaction ) {
        for ( Molecule molecule : reaction.reactants ) {
            assert reactionMap.get( molecule ) == null;
            reactionMap.put( molecule, reaction );
        }
        reactions.add( reaction );
    }

    private void removeReaction( Reaction reaction ) {
        for ( Molecule molecule : reaction.reactants ) {
            reactionMap.remove( molecule );
        }
        reactions.remove( reaction );
    }

    public boolean isContainedInBucket( Molecule molecule ) {
        for ( MoleculeBucket bucket : buckets ) {
            if ( bucket.getMolecules().contains( molecule ) ) {
                return true;
            }
        }
        return false;
    }

    public List<MoleculeBucket> getBuckets() {
        return buckets;
    }

    public Set<Molecule> getMolecules() {
        return molecules;
    }

    public MoleculeBucket getBucketForShape( MoleculeShape shape ) {
        for ( MoleculeBucket bucket : buckets ) {
            if ( bucket.getShape() == shape ) {
                return bucket;
            }
        }
        throw new RuntimeException( "Bucket not found for shape: " + shape );
    }

    public boolean hasMoleculesOutsideOfBuckets() {
        return !molecules.isEmpty() || hasMoleculesInBoxes.get();
    }

    public void addMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.add( listener );
    }

    public void removeMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.remove( listener );
    }

    public LayoutBounds getLayoutBounds() {
        return layoutBounds;
    }

    public List<MoleculeBucket> getProductBuckets() {
        return productBuckets;
    }

    public List<MoleculeBucket> getReactantBuckets() {
        return reactantBuckets;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public Molecule getMolecule( Atom atom ) {
        for ( Molecule molecule : molecules ) {
            if ( molecule.getAtoms().contains( atom ) ) {
                return molecule;
            }
        }
        //return null;
        throw new RuntimeException( "molecule not found for atom: " + atom );
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
}
