// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.filter;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.flatten;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.multipleCombinations;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.unique;

/**
 * A kit is basically a collection of buckets (some hold reactants, some hold products), and all of the molecules and reactions that are
 * contained either in the buckets or in the play area. It has its own physics model, and can have its visibility toggled
 */
public class Kit {

    private final List<MoleculeBucket> buckets; // all of the buckets
    private final List<MoleculeBucket> reactantBuckets; // just the buckets that hold reactants
    private final List<MoleculeBucket> productBuckets; // just the buckets that hold the products

    private final List<ReactionShape> possibleReactions; // types of reactions that may occur in this kit

    public final ObservableList<Molecule> molecules = new ObservableList<Molecule>();
    public final ObservableList<Molecule> moleculesInPlayArea = new ObservableList<Molecule>();
    public final Property<Boolean> visible = new Property<Boolean>( false );
    public final Property<Boolean> hasProductInPlayArea = new Property<Boolean>( false );

    private final LayoutBounds layoutBounds;

    private final Box2dModel box2dModel;

    private final Set<MoleculeShape> productShapes = new HashSet<MoleculeShape>();

    private final List<Reaction> reactions = new ArrayList<Reaction>();
    private final Map<Molecule, Reaction> reactionMap = new HashMap<Molecule, Reaction>();

    public final VoidNotifier stepCompleted = new VoidNotifier();
    public final Notifier<Double> timestepCompleted = new Notifier<Double>();

    public Kit( final LayoutBounds layoutBounds,
                final List<MoleculeBucket> reactantBuckets,
                final List<MoleculeBucket> productBuckets,
                final List<ReactionShape> possibleReactions ) {
        this.reactantBuckets = reactantBuckets;
        this.productBuckets = productBuckets;
        this.possibleReactions = possibleReactions;

        box2dModel = new Box2dModel( layoutBounds );

        // calculate what molecule shapes are "product" shapes
        for ( ReactionShape possibleReaction : possibleReactions ) {
            for ( ReactionShape.MoleculeSpot productSpot : possibleReaction.productSpots ) {
                productShapes.add( productSpot.shape );
            }
        }

        this.buckets = new ArrayList<MoleculeBucket>() {{
            addAll( reactantBuckets );
            addAll( productBuckets );
        }};
        this.layoutBounds = layoutBounds;

        for ( MoleculeBucket bucket : buckets ) {
            for ( Molecule molecule : bucket.getMolecules() ) {
                molecules.add( molecule );
            }
        }

        molecules.addElementRemovedObserver( new VoidFunction1<Molecule>() {
            public void apply( Molecule molecule ) {
                if ( moleculesInPlayArea.contains( molecule ) ) {
                    moleculesInPlayArea.remove( molecule );
                }
                else {
                    getBucketForShape( molecule.shape ).removeMolecule( molecule );
                }
            }
        } );

        moleculesInPlayArea.addElementAddedObserver( new VoidFunction1<Molecule>() {
            public void apply( Molecule molecule ) {
                box2dModel.addBody( molecule );
                double angle = Math.random() * 2 * Math.PI;
                double magnitude = Math.random() * 500; // TODO: make initial velocity proportional to the heat?
                molecule.setVelocity( new Vector2D(
                        Math.cos( angle ) * magnitude,
                        Math.sin( angle ) * magnitude
                ) );

                molecule.setAngularVelocity( (float) ( Math.random() - 0.5 ) * 3 );

                checkForProducts();
            }
        } );

        moleculesInPlayArea.addElementRemovedObserver( new VoidFunction1<Molecule>() {
            public void apply( Molecule molecule ) {
                box2dModel.removeBody( molecule );

                checkForProducts();
            }
        } );

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

    public void refillKit() {
        // remove all of the molecules
        for ( Molecule molecule : new ArrayList<Molecule>( molecules ) ) {
            molecules.remove( molecule );
            molecule.disposeNotifier.updateListeners();
        }

        for ( MoleculeBucket bucket : buckets ) {
            bucket.addInitialMolecules();

            // add them to the kit (and thus the view), so that when the bucket positions are computed (in positionMoleculesInBucket), it will get the z-order correct
            for ( Molecule molecule : bucket.getMolecules() ) {
                molecules.add( molecule );
            }

            bucket.positionMoleculesInBucket();
        }
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
        List<Reaction> allPossibleReactions = getAllPossibleReactions();
        for ( final Reaction potentialReaction : allPossibleReactions ) {

            // compute the reaction's properties
            potentialReaction.initialize( 500 / allPossibleReactions.size() ); // scale the rate so we hit approximately N attempts / frame

            // bail if it isn't "possible"
            if ( potentialReaction.getFitness() <= 0 ) {
                continue;
            }
            // we need to check for all reactions that are currently in place and would use the molecules we want
            List<Reaction> replaceableReactions = filter( unique( map( potentialReaction.getReactants(), new Function1<Molecule, Reaction>() {
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


        /*---------------------------------------------------------------------------*
        * tweak velocities based on reactions
        *----------------------------------------------------------------------------*/
        for ( Reaction reaction : new ArrayList<Reaction>( reactions ) ) {
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
            reaction.tick( simulationTimeChange );

            // kick out now-invalid reactions
            if ( reaction.getFitness() == 0 ) {
                removeReaction( reaction );
            }
        }

        /*---------------------------------------------------------------------------*
        * handle molecule/bucket animations
        *----------------------------------------------------------------------------*/
        for ( MoleculeBucket bucket : buckets ) {
            bucket.attractMolecules( simulationTimeChange );
        }

        /*---------------------------------------------------------------------------*
        * notify
        *----------------------------------------------------------------------------*/
        stepCompleted.updateListeners();
        timestepCompleted.updateListeners( simulationTimeChange );
    }

    public void dragStart( Molecule molecule ) {
        if ( molecule.getBody() != null ) {
            // it's in the play area. remove it while we drag
            moleculesInPlayArea.remove( molecule );
        }
        else {
            // dragged out of a bucket. remove it from it's bucket
            for ( MoleculeBucket bucket : buckets ) {
                if ( bucket.getShape() == molecule.shape ) {
                    bucket.removeMolecule( molecule );
                    break;
                }
            }
        }
    }

    public void dragEnd( Molecule molecule ) {
        Vector2D position = molecule.position.get();
        if ( layoutBounds.getAvailablePlayAreaModelBounds().contains( position.getX(), position.getY() ) ) {
            // dropped in the play area, so re-enable physics on it
            moleculesInPlayArea.add( molecule );
        }
        else {
            // dropped outside, so put it back in it's bucket
            for ( MoleculeBucket bucket : buckets ) {
                if ( bucket.getShape() == molecule.shape ) {
                    bucket.addMolecule( molecule );
                    break;
                }
            }
        }
    }

    private void checkForProducts() {
        for ( Molecule molecule : moleculesInPlayArea ) {
            if ( productShapes.contains( molecule.shape ) ) {
                hasProductInPlayArea.set( true );
                return;
            }
        }
        hasProductInPlayArea.set( false );
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

    public MoleculeBucket getBucketForShape( MoleculeShape shape ) {
        for ( MoleculeBucket bucket : buckets ) {
            if ( bucket.getShape() == shape ) {
                return bucket;
            }
        }
        throw new RuntimeException( "Bucket not found for shape: " + shape );
    }

    public boolean hasMoleculesOutsideOfBuckets() {
        return !moleculesInPlayArea.isEmpty();
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

    public void completeReaction( Reaction reaction, List<Molecule> newMolecules ) {
        for ( Molecule reactant : reaction.reactants ) {
            molecules.remove( reactant );
            reactant.disposeNotifier.updateListeners();
        }
        for ( Molecule newMolecule : newMolecules ) {
            molecules.add( newMolecule );
            moleculesInPlayArea.add( newMolecule );
        }
        reactions.remove( reaction );
    }

    // get a list of all possible reaction-combinations, with the molecule-list ordering matching the ordering of the reaction shape
    public List<Reaction> getAllPossibleReactions() {

        List<Reaction> result = new ArrayList<Reaction>();

        // list of molecules for each molecule type
        final Map<MoleculeShape, List<Molecule>> moleculeShapeMap = new HashMap<MoleculeShape, List<Molecule>>() {{
            for ( final Molecule molecule : moleculesInPlayArea ) {
                // fill it for all molecules in the play area
                if ( containsKey( molecule.shape ) ) {
                    get( molecule.shape ).add( molecule );
                }
                else {
                    put( molecule.shape, new ArrayList<Molecule>() {{
                        add( molecule );
                    }} );
                }
            }
        }};

        for ( final ReactionShape reactionShape : possibleReactions ) {
            // how many of each type of molecule are needed
            final Map<MoleculeShape, Integer> moleculeShapeCounts = reactionShape.getMoleculeShapeCounts();

            // verify that we have enough molecules of each type for this reaction to occur
            boolean hasEnoughMolecules = true;
            for ( MoleculeShape moleculeShape : moleculeShapeCounts.keySet() ) {
                int requiredQuantity = moleculeShapeCounts.get( moleculeShape );
                if ( !moleculeShapeMap.containsKey( moleculeShape ) || moleculeShapeMap.get( moleculeShape ).size() < requiredQuantity ) {
                    hasEnoughMolecules = false;
                    break;
                }
            }
            // if not, try other reaction types
            if ( !hasEnoughMolecules ) {
                continue;
            }

            // compute all of the possible combinations that we could have for reactions
            List<List<List<Molecule>>> combinationsList = multipleCombinations( new ArrayList<Pair<Integer, Collection<Molecule>>>() {{
                for ( MoleculeShape moleculeShape : reactionShape.getMoleculeShapeOrdering() ) {
                    // for each shape, create "combinationCount" of these in combinations
                    Integer combinationCount = moleculeShapeCounts.get( moleculeShape );
                    add( new Pair<Integer, Collection<Molecule>>( combinationCount, moleculeShapeMap.get( moleculeShape ) ) );
                }
            }} );

            for ( List<List<Molecule>> lists : combinationsList ) {
                List<Molecule> matchingListOfMolecules = flatten( lists );

                // create a reaction for each symmetry. this can handle necessary reflections
                for ( List<Molecule> moleculeList : reactionShape.getSymmetries( matchingListOfMolecules ) ) {
                    result.add( new Reaction( this, reactionShape, moleculeList ) );
                }
            }
        }

        return result;
    }
}
