// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.F;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.buildafraction.view.SceneContext;
import edu.colorado.phet.fractions.buildafraction.view.SceneNode;
import edu.colorado.phet.fractions.buildafraction.view.Stack;
import edu.colorado.phet.fractions.buildafraction.view.StackContext;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.FNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils.ord;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerNode.*;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.createPieSlice;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.createRect;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.PieceIconNode.TINY_SCALE;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static fj.Equal.intEqual;
import static fj.Ord.doubleOrd;
import static fj.data.List.iterableList;
import static fj.function.Booleans.not;

/**
 * Node for the scene when the user is constructing fractions with pictures (shapes).
 *
 * @author Sam Reid
 */
public class ShapeSceneNode extends SceneNode<ShapeSceneCollectionBoxPair> implements ContainerContext, PieceContext, StackContext<PieceNode> {
    private final RichPNode toolboxNode;

    //Declare type-specific wrappers for declaration site variance to make map call site more readable
    private final F<PieceIconNode, Double> _minX = FNode._minX();
    private final F<ContainerNode, Double> _maxX = FNode._maxX();

    private final int spacing;
    private final double layoutXOffset;
    private final ShapeLevel level;
    private final ArrayList<Vector2D> containerNodeToolboxLocations = new ArrayList<Vector2D>();
    private static final Random random = new Random();

    private static final double CARD_SPACING_DX = 3;
    private static final double CARD_SPACING_DY = CARD_SPACING_DX;
    private final BuildAFractionModel model;
    public final int toolboxHeight;

    @SuppressWarnings("unchecked") public ShapeSceneNode( final int levelIndex, final BuildAFractionModel model, final PDimension stageSize, final SceneContext context, BooleanProperty soundEnabled, boolean freePlay ) {
        this( levelIndex, model, stageSize, context, soundEnabled, Option.some( getToolbarOffset( levelIndex, model, stageSize, context, soundEnabled, freePlay ) ), freePlay );
    }

    //Create and throw away a new ShapeSceneNode in order to get the layout of the toolbar perfectly centered under the title.
    //Hack alert!  I have concerns that this could lead to difficult to understand code, especially during debugging because 2 ShapeSceneNodes are created for each one displayed.
    //This code was written because everything is in the same layer because nodes must move freely between toolbox, play area and collection boxes.
    private static double getToolbarOffset( final int levelIndex, final BuildAFractionModel model, final PDimension stageSize, final SceneContext context, final BooleanProperty soundEnabled, boolean freePlay ) {
        ShapeSceneNode node = new ShapeSceneNode( levelIndex, model, stageSize, context, soundEnabled, Option.<Double>none(), freePlay );

        //Re-layout the toolbox based on the location of the title
        double desiredToolboxCenter = node.levelReadoutTitle.getFullBounds().getCenterX();
        double originalToolboxCenter = node.toolboxNode.getFullBounds().getCenterX();
        double delta = desiredToolboxCenter - originalToolboxCenter;

        //make sure it doesn't go off the screen to the left
        double originalToolboxX = node.toolboxNode.getFullBounds().getX();
        final double newToolboxX = originalToolboxX + delta;
        if ( newToolboxX < INSET ) {
            delta = INSET - originalToolboxX;
        }
        return delta;
    }

    @SuppressWarnings("unchecked") private ShapeSceneNode( final int levelIndex, final BuildAFractionModel model, final PDimension stageSize, final SceneContext context, BooleanProperty soundEnabled, Option<Double> toolboxOffset,
                                                           final boolean freePlay ) {
        super( levelIndex, soundEnabled, context, freePlay );
        this.model = model;
        double insetY = 10;
        final ActionListener goToNextLevel = new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                context.goToShapeLevel( levelIndex + 1 );
            }
        };
        final VoidFunction0 _resampleLevel = new VoidFunction0() {
            public void apply() {
                context.resampleShapeLevel( levelIndex );
            }
        };
        this.level = model.getShapeLevel( levelIndex );
        spacing = level.shapeType == ShapeType.BAR ? 140 : 120;

        //Create the scoring cells with target patterns
        ArrayList<ShapeSceneCollectionBoxPair> _pairs = new ArrayList<ShapeSceneCollectionBoxPair>();
        for ( int i = 0; i < level.targets.length(); i++ ) {
            MixedFraction target = level.getTarget( i );

            PNode f = MixedFractionNodeFactory.toNode( target );
            final ShapeCollectionBoxNode cell = new ShapeCollectionBoxNode( this, level.targets.maximum( ord( MixedFraction._toDouble ) ), model.collectedMatch.or( level.matchExists ) );
            _pairs.add( new ShapeSceneCollectionBoxPair( cell, new ZeroOffsetNode( f ), target ) );
        }
        initCollectionBoxes( insetY, _pairs, freePlay );

        //Center the first container node in the play area.
        //Layout values sampled manually at runtime
        ContainerNode firstContainerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne(), level.shapeType, level.getMaxNumberOfSingleContainers() ) {{
            Vector2D position = getContainerPosition( level );
            setInitialPosition( position.x, position.y );
            setScale( getContainerScale() );
        }};
        addChild( firstContainerNode );

        //Pieces in the toolbar that the user can drag
        List<List<Integer>> groups = level.pieces.group( intEqual );
        int numGroups = groups.length();
        layoutXOffset = ( 6 - numGroups ) * spacing / 4 + ( level.shapeType == ShapeType.BAR ? 0 : 45 ) + ( toolboxOffset.isSome() ? toolboxOffset.some() : 0.0 );
        toolboxHeight = ( level.shapeType == ShapeType.BAR ? 100 : 140 ) + 5;
        for ( P2<List<Integer>, Integer> groupWithIndex : groups.zipIndex() ) {
            int stackIndex = groupWithIndex._2();
            List<Integer> group = groupWithIndex._1();

            Rectangle2D bounds = null;
            ArrayList<PieceNode> pieces = new ArrayList<PieceNode>();
            for ( final P2<Integer, Integer> pieceDenominatorWithIndex : group.zipIndex() ) {
                int pieceDenominator = pieceDenominatorWithIndex._1();
                int cardIndex = pieceDenominatorWithIndex._2();

                //Choose the shape for the level, pies or horizontal bars
                final PieceNode piece = level.shapeType == ShapeType.PIE ? new PiePieceNode( pieceDenominator, ShapeSceneNode.this, new PhetPPath( createPieSlice( pieceDenominator ), level.color, PieceNode.stroke, Color.black ) )
                                                                         : new BarPieceNode( pieceDenominator, ShapeSceneNode.this, new PhetPPath( createRect( pieceDenominator ), level.color, PieceNode.stroke, Color.black ) );
                piece.setOffset( getLocation( stackIndex, cardIndex, piece ).toPoint2D() );
                piece.setInitialScale( TINY_SCALE );

                ShapeSceneNode.this.addChild( piece );
                if ( bounds == null ) { bounds = piece.getFullBounds(); }
                else { bounds = bounds.createUnion( piece.getFullBounds() ); }
                pieces.add( piece );
            }

            final List<PieceNode> pieceList = iterableList( pieces );
            final Stack<PieceNode> stack = new Stack<PieceNode>( pieceList, stackIndex, this );
            for ( P2<PieceNode, Integer> pieceAndIndex : pieceList.zipIndex() ) {
                final PieceNode piece = pieceAndIndex._1();
                final Integer index = pieceAndIndex._2();
                piece.setStack( stack );
                piece.setPositionInStack( Option.some( index ) );
            }
            stack.update();

            final PieceIconNode child = new PieceIconNode( group.head(), level.shapeType );
            final PieceNode firstPiece = pieces.get( 0 );

            child.setOffset( level.shapeType == ShapeType.PIE ? new Point2D.Double( firstPiece.getFullBounds().getMaxX() - child.getFullBounds().getWidth(), firstPiece.getYOffset() )
                                                              : new Point2D.Double( pieces.get( 0 ).getOffset().getX() + 0.5, pieces.get( 0 ).getOffset().getY() + 0.5 ) );

            //It is unknown why the above computation doesn't work for fifths pie slices, but it is off horizontally by several units
            if ( level.shapeType == ShapeType.PIE && firstPiece.pieceSize == 5 ) {
                child.translate( 4, 0 );
            }
            addChild( child );
            child.moveToBack();
        }

        //Containers the user can drag out of the toolbox.
        final int finalGroupIndex = groups.length();
        final int numInGroup = level.targets.length() - 1;
        for ( int i = 0; i < numInGroup; i++ ) {
            final double delta = getCardOffsetWithinStack( numInGroup, i );
            final ContainerNode containerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne(), level.shapeType, level.getMaxNumberOfSingleContainers() ) {{
                final int sign = level.shapeType == ShapeType.PIE ? -1 : +1;
                setInitialState( layoutXOffset + INSET + 20 + delta * sign + finalGroupIndex * spacing,
                                 stageSize.height - INSET - toolboxHeight + 20 + delta, TINY_SCALE );
            }};
            addChild( containerNode );
            containerNodeToolboxLocations.add( new Vector2D( containerNode.getOffset() ) );
        }

        //Add a piece container toolbox the user can use to get containers
        toolboxNode = new RichPNode() {{

            //Width is just before the first group to the last container node in the toolbox
            double min = FNode.getChildren( ShapeSceneNode.this, PieceIconNode.class ).map( _minX ).minimum( doubleOrd );
            double max = FNode.getChildren( ShapeSceneNode.this, ContainerNode.class ).map( _maxX ).maximum( doubleOrd );
            double width = max - min;
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width + INSET * 6, toolboxHeight, 30, 30 ), Color.white, BuildAFractionCanvas.controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( min - INSET * 3, AbstractFractionsCanvas.STAGE_SIZE.height - INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );
        toolboxNode.moveToBack();

        finishCreatingUI( levelIndex, model, stageSize, goToNextLevel, _resampleLevel, freePlay );
    }

    public boolean isMixedNumbers() { return model.isMixedNumbers(); }

    //Location for a container to move to in the center of the play area
    private static Vector2D getContainerPosition( ShapeLevel level ) {
        double x = 298 + ( level.shapeType == ShapeType.PIE ? 26 : 0 );
        if ( level.hasValuesGreaterThanOne() ) { x = x - 61; }
        final int y = level.shapeType == ShapeType.PIE ? 200 : 250;
        return new Vector2D( x, y );
    }

    protected void reset() {
        //Eject everything from target containers
        //Split everything
        //Return everything home
        for ( ContainerNode containerNode : getContainerNodes() ) {
            containerNode.undoAll();
            containerNode.animateHome();
            containerNode.selectedPieceSize.reset();
            containerNode.resetNumberOfContainers();
        }

        //Order dependence: have to undo the collection boxes after animating other containers home, so one container will go to the center of the screen, see #3397 comment 15 item 2
        for ( ShapeSceneCollectionBoxPair targetPair : pairs ) {
            targetPair.collectionBoxNode.undo();
        }

        for ( PieceNode piece : getPieceNodes() ) {
            piece.animateToTopOfStack();
        }
        level.filledTargets.reset();
    }

    private List<PieceNode> getPieceNodes() {
        ArrayList<PieceNode> children = new ArrayList<PieceNode>();
        for ( Object child : this.getChildrenReference() ) {
            if ( child instanceof PieceNode ) {
                children.add( (PieceNode) child );
            }
        }
        return iterableList( children );
    }

    //The user finished dragging a container node.  Perhaps they dropped it in a correct or incorrect collection box, or over the toolbox.
    public void endDrag( final ContainerNode containerNode ) {

        //Make the buttons pressable
        containerNode.updateExpansionButtonsEnabled();

        //See if it hits any matching collection boxes
        List<ShapeSceneCollectionBoxPair> pairs = this.pairs.sort( ord( new F<ShapeSceneCollectionBoxPair, Double>() {
            @Override public Double f( final ShapeSceneCollectionBoxPair t ) {
                return t.collectionBoxNode.getGlobalFullBounds().getCenter2D().distance( containerNode.getGlobalFullBounds().getCenter2D() );
            }
        } ) );
        boolean hit = false;

        final boolean intersectsToolbox = containerNode.getGlobalFullBounds().intersects( toolboxNode.getGlobalFullBounds() );

        //The following block determines whether the user is trying to put the container in the toolbox.
        //If so, then it should not test for overlap with the collection boxes, which would erroneously eject it to the play area, see #3397
        ShapeSceneCollectionBoxPair closestCollectionBoxPair = pairs.take( 1 ).head();
        double collectionBoxY = closestCollectionBoxPair.getCollectionBoxNode().getGlobalFullBounds().getCenterY();
        double toolboxY = toolboxNode.getGlobalFullBounds().getCenterY();
        double containerNodeY = containerNode.getGlobalFullBounds().getCenterY();
        double distanceToCollectionBoxInY = Math.abs( collectionBoxY - containerNodeY );
        double distanceToToolboxY = Math.abs( toolboxY - containerNodeY );
        boolean closerToToolboxCenter = distanceToToolboxY < distanceToCollectionBoxInY;
        boolean skipCollectionBoxes = intersectsToolbox && closerToToolboxCenter;

        if ( !skipCollectionBoxes ) {
            //Only consider the closest box, otherwise students can overlap many boxes instead of thinking of the correct answer
            for ( ShapeSceneCollectionBoxPair pair : pairs.take( 1 ) ) {
                final boolean intersects = pair.collectionBoxNode.getGlobalFullBounds().intersects( containerNode.getGlobalFullBounds() );
                final boolean matchesValue = containerNode.getFractionValue().approxEquals( pair.value.toFraction() );
                final boolean occupied = pair.getCollectionBoxNode().isCompleted();
                if ( intersects && matchesValue && !occupied ) {

                    //TODO: could fine tune layout here based on shape type or mixed vs non-mixed
                    final double scale = 0.5 * pair.collectionBoxNode.scaleFactor;// * ( isMixedNumbers() ? 1.03 : 1.0 );
                    containerNode.removeUndoButton();

                    //Order dependence: set in target cell first so that layout code will work better afterwards
                    containerNode.setInTargetCell( true, pair.value.denominator );
                    final PBounds boxBounds = pair.collectionBoxNode.getFullBounds();
                    final PBounds containerBounds = containerNode.getFullBounds();
                    containerNode.animateToPositionScaleRotation( boxBounds.getCenterX() - containerBounds.getWidth() / 2 * scale,
                                                                  boxBounds.getCenterY() - containerBounds.getHeight() / 2 * scale + 20, scale, 0,
                                                                  BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( containerNode, false ) );
                    pair.collectionBoxNode.setCompletedFraction( containerNode );
                    containerNode.setAllPickable( false );

                    hit = true;
                    break;
                }
                else if ( intersects ) {
                    //move back to the center of the screen, but only if no other container node is already there.
                    if ( getContainerNodesInPlayArea().exists( new F<ContainerNode, Boolean>() {
                        @Override public Boolean f( final ContainerNode containerNode ) {
                            return containerNode.getOffset().distance( getContainerPosition( level ).toPoint2D() ) < 10;
                        }
                    } ) ) {
                        double angle = Math.PI * 2 * random.nextDouble();
                        animateToPosition( containerNode, getContainerPosition( level ).plus( Vector2D.createPolar( 150, angle ) ), new MoveAwayFromCollectionBoxes( this, containerNode ) );
                    }
                    else {
                        animateToPosition( containerNode, getContainerPosition( level ), new MoveAwayFromCollectionBoxes( this, containerNode ) );
                    }

                }
            }
        }

        if ( hit ) {
            level.filledTargets.increment();
        }

        //Put the piece back in its starting location, but only if it is empty
        if ( !hit && intersectsToolbox && containerNode.getFractionValue().numerator == 0 ) {
            if ( containerNode.belongsInToolbox() ) {
                containerNode.resetNumberOfContainers();
                containerNode.selectedPieceSize.reset();
            }
            containerNode.animateHome();
        }

        //Add a new container when the previous one is completed
        if ( !allTargetsComplete() ) {

            //If no fraction skeleton in play area, move one there
            final List<ContainerNode> inToolbox = getContainerNodesInToolbox();
            final int playing = getContainerNodesInPlayArea().length();
            final int boxing = inToolbox.length();
            if ( playing == 0 && boxing > 0 ) {
                animateToCenterScreen( inToolbox.head(), new NullDelegate() );
            }
        }

        if ( allTargetsComplete() ) {
            notifyAllCompleted();

            faceNodeDialog.setVisible( true );
            faceNodeDialog.animateToTransparency( 1f, BuildAFractionModule.ANIMATION_TIME );
            faceNodeDialog.setPickable( true );
            faceNodeDialog.setChildrenPickable( true );
            faceNodeDialog.moveToFront();
        }
        if ( !allTargetsComplete() && hit ) {
            notifyOneCompleted();
        }

        syncModelFractions();
    }

    //Make the container node go to the play area if it is empty, otherwise go to the toolbox
    public void animateContainerNodeToAppropriateLocation( final ContainerNode containerNode ) {
        if ( getContainerNodesInPlayArea().length() == 0 ) {
            animateToCenterScreen( containerNode, new NullDelegate() );
        }
        else {
            containerNode.animateToShowSpinners();
            //find a free location in the toolbox
            List<Vector2D> v = iterableList( containerNodeToolboxLocations ).filter( new F<Vector2D, Boolean>() {
                @Override public Boolean f( final Vector2D vector2D ) {
                    return !getContainerNodes().exists( new F<ContainerNode, Boolean>() {
                        @Override public Boolean f( final ContainerNode containerNode ) {
                            return new Vector2D( containerNode.getOffset() ).equals( vector2D );
                        }
                    } );
                }
            } );
            //Fail safe!  Shouldn't happen but fail gracefully just in case
            if ( v.length() == 0 ) {
                animateToPosition( containerNode, getContainerPosition( level ).plus( 100, 100 ), new NullDelegate() );
            }
            else {
                containerNode.animateToPositionScaleRotation( v.head().x, v.head().y,
                                                              TINY_SCALE, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( containerNode, true ) );
                animateToPosition( containerNode, v.head(), new NullDelegate() );
            }
        }
    }

    private List<ContainerNode> getContainerNodesInToolbox() {
        return getContainerNodes().filter( new F<ContainerNode, Boolean>() {
            @Override public Boolean f( final ContainerNode containerNode ) {
                return containerNode.isInToolbox();
            }
        } );
    }

    private List<ContainerNode> getContainerNodesInPlayArea() {
        return getContainerNodes().filter( new F<ContainerNode, Boolean>() {
            @Override public Boolean f( final ContainerNode containerNode ) {
                return containerNode.isInPlayArea();
            }
        } );
    }

    private void animateToCenterScreen( final ContainerNode containerNode, PActivityDelegate delegate ) { animateToPosition( containerNode, getContainerPosition( level ), delegate ); }

    private void animateToPosition( final ContainerNode containerNode, final Vector2D position, PActivityDelegate delegate ) {
        containerNode.animateToPositionScaleRotation( position.x, position.y,
                                                      1, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new CompositeDelegate( new DisablePickingWhileAnimating( containerNode, true ), delegate, new PActivityDelegateAdapter() {
            @Override public void activityFinished( final PActivity activity ) {
                containerNode.updateExpansionButtonsEnabled();
            }
        } ) );
    }

    public void endDrag( final PieceNode piece ) {
        boolean droppedInto = false;
        List<SingleContainerNode> targets = getContainerNodes().bind( _getSingleContainerNodes );
        List<SingleContainerNode> containerNodes = targets.sort( ord( new F<SingleContainerNode, Double>() {
            @Override public Double f( final SingleContainerNode s ) {
                return s.getGlobalFullBounds().getCenter2D().distance( piece.getGlobalFullBounds().getCenter2D() );
            }
        } ) );

        for ( SingleContainerNode target : containerNodes ) {
            if ( target.getGlobalFullBounds().intersects( piece.getGlobalFullBounds() ) && !target.isInToolbox() && !target.willOverflow( piece ) && !target.parent.isInTargetCell() ) {
                dropInto( piece, target );
                droppedInto = true;
                break;
            }
        }

        //If didn't intersect a container, see if it should go back to the toolbox
        if ( !droppedInto ) {
            piece.animateToTopOfStack();
        }
    }

    //find closest container that has a site that could hold this piece, and get the angle it would take if it joined
    public Option<Double> getNextAngle( final PieceNode pieceNode ) {
        List<SingleContainerNode> closest = getContainerNodes().
                bind( _getSingleContainerNodes ).
                filter( new F<SingleContainerNode, Boolean>() {
                    @Override public Boolean f( final SingleContainerNode n ) {
                        return !n.isInToolbox();
                    }
                } ).
                filter( new F<SingleContainerNode, Boolean>() {
                    @Override public Boolean f( final SingleContainerNode n ) {
                        return !n.willOverflow( pieceNode );
                    }
                } ).
                filter( new F<SingleContainerNode, Boolean>() {
                    @Override public Boolean f( final SingleContainerNode n ) {
                        return !n.parent.isInTargetCell();
                    }
                } ).
                sort( ord( new F<SingleContainerNode, Double>() {
                    @Override public Double f( final SingleContainerNode n ) {
                        return n.getGlobalFullBounds().getCenter2D().distance( pieceNode.getGlobalFullBounds().getCenter2D() );
                    }
                } ) );
        if ( closest.isNotEmpty() ) {
            return Option.some( closest.head().getDropLocation( pieceNode, level.shapeType ).angle );
        }
        else { return Option.none(); }
    }

    public double getContainerScale() { return isMixedNumbers() ? 0.6 : 1.0; }

    public @Data static class DropLocation {
        public final Vector2D position;
        public final double angle;
    }

    //Piece dropped into container
    private void dropInto( final PieceNode piece, final SingleContainerNode container ) {

        //Make container non-draggable while piece is animating into it, otherwise the piece will end up in the wrong location.
        container.setPickable( false );
        container.setChildrenPickable( false );

        PActivity activity;
        //Function that finalizes the piece in the container
        VoidFunction0 postprocess;
        if ( level.shapeType == ShapeType.PIE ) {
            final DropLocation dropLocation = container.getDropLocation( piece, level.shapeType );
            activity = piece.animateToPositionScaleRotation( dropLocation.position.x, dropLocation.position.y, getContainerScale(), 0, BuildAFractionModule.ANIMATION_TIME );

            postprocess = new VoidFunction0() {
                public void apply() {
                    piece.setPickable( false );
                    piece.setChildrenPickable( false );
                    piece.setOffset( dropLocation.position.x, dropLocation.position.y );
                    piece.setScale( 1.0 );
                    piece.setRotation( 0.0 );
                }
            };
            //Should already be at correct angle, update again just in case
            if ( piece instanceof PiePieceNode ) {
                ( (PiePieceNode) piece ).setPieceRotation( dropLocation.angle );
            }
        }
        else {
            Point2D translation = container.getGlobalTranslation();
            piece.globalToLocal( translation );
            piece.localToParent( translation );
            final DropLocation dropLocation = container.getDropLocation( piece, level.shapeType );
            final Vector2D a = dropLocation.position.plus( translation );
            activity = piece.animateToPositionScaleRotation( a.x, a.y, getContainerScale(), dropLocation.angle, BuildAFractionModule.ANIMATION_TIME );

            postprocess = new VoidFunction0() {
                public void apply() {
                    piece.setPickable( false );
                    piece.setChildrenPickable( false );
                    piece.setOffset( a.x, a.y );
                    piece.setScale( 1.0 );
                    piece.setRotation( dropLocation.angle );
                }
            };
        }
        piece.setPickable( false );
        piece.setChildrenPickable( false );
        final VoidFunction0 finalPostProcess = postprocess;
        activity.setDelegate( new CompositeDelegate( new PActivityDelegateAdapter() {
            public void activityFinished( final PActivity activity ) {
                container.setPickable( true );
                container.setChildrenPickable( true );
                piece.terminateActivities();
                finalPostProcess.apply();
                container.addPiece( piece );
                syncModelFractions();
            }
        }, new DisablePickingWhileAnimating( piece, false ) ) );

        container.addDropLocationToUndoList();
    }

    public void syncModelFractions() {
        level.createdFractions.set( getUserCreatedFractions() );
    }

    //When a container is added to a container node, move it to the left if it overlaps the scoring boxes.
    public void containerAdded( final ContainerNode c ) { moveContainerNodeAwayFromCollectionBoxes( c ); }

    //if its right edge is past the left edge of any collection box, move it left
    public void moveContainerNodeAwayFromCollectionBoxes( final ContainerNode containerNode ) {
        double rightSide = containerNode.getGlobalFullBounds().getMaxX();
        double edge = pairs.map( new F<ShapeSceneCollectionBoxPair, Double>() {
            @Override public Double f( final ShapeSceneCollectionBoxPair target ) {
                return target.getCollectionBoxNode().getGlobalFullBounds().getMinX();
            }
        } ).minimum( doubleOrd );
        double overlap = rightSide - edge;
        if ( overlap > 0 ) {
            double amountToMoveLeft = 20 + overlap;
            containerNode.animateToPositionScaleRotation( containerNode.getXOffset() - amountToMoveLeft, containerNode.getYOffset(), containerNode.getScale(), containerNode.getRotation(), BuildAFractionModule.ANIMATION_TIME ).
                    setDelegate( new DisablePickingWhileAnimating( containerNode, true ) );
        }
    }

    private List<Fraction> getUserCreatedFractions() { return getContainerNodes().filter( not( _isInTargetCell ) ).map( _getFractionValue ); }

    public void undoPieceFromContainer( final PieceNode piece ) {
        Point2D offset = piece.getGlobalTranslation();
        double scale = piece.getGlobalScale();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        piece.setGlobalScale( scale );

        piece.setPickable( true );
        piece.setChildrenPickable( true );
        piece.animateToTopOfStack();
    }

    private List<ContainerNode> getContainerNodes() { return getChildren( this, ContainerNode.class ); }

    private boolean allTargetsComplete() {
        return pairs.map( new F<ShapeSceneCollectionBoxPair, Boolean>() {
            @Override public Boolean f( final ShapeSceneCollectionBoxPair pair ) {
                return pair.collectionBoxNode.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == pairs.length();
    }

    public void collectionBoxUndone() {
        level.filledTargets.decrement();
        faceNodeDialog.animateToTransparency( 0.0f, BuildAFractionModule.ANIMATION_TIME );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );
    }

    //Determine the location of a card within a stack.  This code is complicated because it takes into account the number of stacks and the card types
    public Vector2D getLocation( final int stackIndex, final int cardIndex, final PieceNode card ) {
        int sign = level.shapeType == ShapeType.PIE ? -1 : +1;
        List<List<Integer>> groups = level.pieces.group( intEqual );
        double delta = getCardOffsetWithinStack( groups.index( stackIndex ).length(), cardIndex );
        final int yOffset = level.shapeType == ShapeType.BAR ? 25 : 0;
        return new Vector2D( layoutXOffset + 30 + delta * sign + stackIndex * spacing,
                             STAGE_SIZE.height - 117 - CARD_SPACING_DY * cardIndex + yOffset );
    }

    private double getCardOffsetWithinStack( final int numInGroup, final int cardIndex ) {
        double totalSpacing = -CARD_SPACING_DX * ( numInGroup - 1 );
        return getCardOffsetWithinStack( numInGroup, cardIndex, totalSpacing );
    }

    private double getCardOffsetWithinStack( final int numInGroup, final int cardIndex, final double totalSpacing ) {
        return numInGroup == 1 ? 0 :
               new LinearFunction( 0, numInGroup - 1, -totalSpacing / 2, +totalSpacing / 2 ).evaluate( cardIndex );
    }

}