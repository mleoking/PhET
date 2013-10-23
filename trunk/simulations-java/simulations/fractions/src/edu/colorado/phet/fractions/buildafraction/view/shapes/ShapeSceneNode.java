// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.Effect;
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
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
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

import static edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils.ord;
import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType.PIE;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerNode.*;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.*;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.PieceIconNode.toolboxScale;
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

    private final double distanceBetweenStacks;
    private final double layoutXOffset;
    public final ShapeLevel level;
    private final ArrayList<Vector2D> containerNodeToolboxLocations = new ArrayList<Vector2D>();
    private static final Random random = new Random();

    private static final double CARD_SPACING_DX = 3;
    private static final double CARD_SPACING_DY = CARD_SPACING_DX;
    private final BuildAFractionModel model;
    public int toolboxHeight;
    private boolean toolboxEnabled = true;

    public static class DropResult {
        public final boolean hit;
        public final Fraction source;
        public final Fraction target;

        //Size the container is divided into, or -1 if it was a card
        public int selectedPieceSize;

        public DropResult( boolean hit, Fraction source, Fraction target,int selectedPieceSize ) {
            this.hit = hit;
            this.source = source;
            this.target = target;
            this.selectedPieceSize = selectedPieceSize;
        }
    }

    public final ArrayList<VoidFunction1<DropResult>> dropListeners = new ArrayList<VoidFunction1<DropResult>>();

    public ShapeSceneNode( final int levelIndex, final BuildAFractionModel model, final SceneContext context, BooleanProperty soundEnabled, boolean fractionLab, final boolean showContainerNodeOnStartup ) {
        this( levelIndex, model, context, soundEnabled, Option.some( getToolbarOffset( levelIndex, model, context, soundEnabled, fractionLab, showContainerNodeOnStartup ) ), fractionLab, showContainerNodeOnStartup );
    }

    //Create and throw away a new ShapeSceneNode in order to get the layout of the toolbar perfectly centered under the title.
    //Hack alert!  I have concerns that this could lead to difficult to understand code, especially during debugging because 2 ShapeSceneNodes are created for each one displayed.
    //This code was written because everything is in the same layer because nodes must move freely between toolbox, play area and collection boxes.
    private static double getToolbarOffset( final int levelIndex, final BuildAFractionModel model, final SceneContext context, final BooleanProperty soundEnabled, boolean fractionLab, final boolean showContainerNodeOnStartup ) {
        ShapeSceneNode node = new ShapeSceneNode( levelIndex, model, context, soundEnabled, Option.<Double>none(), fractionLab, showContainerNodeOnStartup );

        //Re-layout the toolbox based on the location of the title
        double desiredToolboxCenter = node.title.getFullBounds().getCenterX();
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

    private ShapeSceneNode( final int levelIndex, final BuildAFractionModel model, final SceneContext context, BooleanProperty soundEnabled, Option<Double> toolboxOffset,
                            final boolean fractionLab, final boolean showContainerNodeOnStartup ) {
        super( levelIndex, soundEnabled, context, fractionLab );
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

        //Make sure enough room to fit all the stacks in all modes
        distanceBetweenStacks = fractionLab ?
                                level.shapeType == ShapeType.BAR ? 104 : 104 :
                                level.shapeType == ShapeType.BAR ? 140 * 0.85 - 2 : 106;

        //Create the scoring cells with target patterns
        ArrayList<ShapeSceneCollectionBoxPair> _pairs = new ArrayList<ShapeSceneCollectionBoxPair>();
        for ( int i = 0; i < level.targets.length(); i++ ) {
            MixedFraction target = level.getTarget( i );

            PNode f = MixedFractionNodeFactory.toNode( target );
            final ShapeCollectionBoxNode cell = new ShapeCollectionBoxNode( this, level.targets.maximum( ord( MixedFraction._toDouble ) ), model.collectedMatch.or( level.matchExists ) );
            _pairs.add( new ShapeSceneCollectionBoxPair( cell, new ZeroOffsetNode( f ), target ) );
        }
        initCollectionBoxes( insetY, _pairs, fractionLab );

        init( levelIndex, model, goToNextLevel, _resampleLevel, fractionLab );

        //Center the first container node in the play area.
        //Layout values sampled manually at runtime
        if ( showContainerNodeOnStartup ) {
            ContainerNode firstContainerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne(), level.shapeType, level.getMaxNumberOfSingleContainers() ) {{
                Vector2D position = getContainerPosition();
                setInitialState( position.x, position.y, getContainerScale() );
            }};
            addChild( firstContainerNode );
        }

        //Pieces in the toolbar that the user can drag
        List<List<Integer>> groups = level.pieces.group( intEqual );
        int numGroups = groups.length();
        layoutXOffset = ( 6 - numGroups ) * distanceBetweenStacks / 4 + ( level.shapeType == ShapeType.BAR ? 0 : 45 ) + ( toolboxOffset.isSome() ? toolboxOffset.some() : 0.0 );

        toolboxHeight = ( level.shapeType == ShapeType.BAR ? 100 : 140 ) + 5;
        if ( fractionLab && level.shapeType == PIE ) { toolboxHeight = toolboxHeight - 25; }

        for ( P2<List<Integer>, Integer> groupWithIndex : groups.zipIndex() ) {
            int stackIndex = groupWithIndex._2();
            List<Integer> group = groupWithIndex._1();

            Rectangle2D bounds = null;
            ArrayList<PieceNode> pieces = new ArrayList<PieceNode>();
            for ( final P2<Integer, Integer> pieceDenominatorWithIndex : group.zipIndex() ) {
                int pieceDenominator = pieceDenominatorWithIndex._1();
                int cardIndex = pieceDenominatorWithIndex._2();

                //Choose the shape for the level, pies or horizontal bars
                final PieceNode piece = level.shapeType == PIE ? new PiePieceNode( pieceDenominator, ShapeSceneNode.this, new PhetPPath( createPieSlice( pieceDenominator ), level.color, PieceNode.stroke, Color.black ) )
                                                               : new BarPieceNode( pieceDenominator, ShapeSceneNode.this, new PhetPPath( createRect( pieceDenominator ), level.color, PieceNode.stroke, Color.black ) );
                piece.setOffset( getLocation( stackIndex, cardIndex, piece ).toPoint2D() );
                piece.setInitialScale( toolboxScale( fractionLab ) );

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

            final PieceIconNode child = new PieceIconNode( group.head(), level.shapeType, fractionLab );
            final PieceNode firstPiece = pieces.get( 0 );

            child.setOffset( level.shapeType == PIE ? new Point2D.Double( firstPiece.getFullBounds().getMaxX() - child.getFullBounds().getWidth(), firstPiece.getYOffset() )
                                                    : new Point2D.Double( pieces.get( 0 ).getOffset().getX() + 0.5, pieces.get( 0 ).getOffset().getY() + 0.5 ) );

            //It is unknown why the above computation doesn't work for fifths pie slices, but it is off horizontally by several units
            if ( level.shapeType == PIE && firstPiece.pieceSize == 5 ) {
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
                final int sign = level.shapeType == PIE ? -1 : +1;
                setInitialState( layoutXOffset + INSET + 20 + delta * sign + finalGroupIndex * distanceBetweenStacks,
                                 AbstractFractionsCanvas.STAGE_SIZE.height - INSET - toolboxHeight + 20 + delta, toolboxScale( fractionLab ) );
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
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width + INSET * 6, toolboxHeight, 30, 30 ), Color.white, BuildAFractionCanvas.CONTROL_PANEL_STROKE, Color.darkGray );
            addChild( border );
            setOffset( min - INSET * 3, AbstractFractionsCanvas.STAGE_SIZE.height - INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );
        toolboxNode.moveToBack();
    }

    //Return true if and only if this level contains mixed numbers.
    public boolean isMixedNumbers() { return model.isMixedNumbers(); }

    //Location for a container to move to in the center of the play area
    private Vector2D getContainerPosition() {
        double offset = level.shapeType == PIE ? circleDiameter / 2 : rectangleWidth / 2;
        double fractionLabYOffset = fractionLab ? STAGE_SIZE.height - toolboxHeight - INSET * 3 - 50 : 0;
        final double y = ( level.shapeType == PIE ? 200 : 250 ) + fractionLabYOffset;

        return new Vector2D( fractionLab ? 485 : title.getCenterX() - offset * getContainerScale(), y );
    }

    //The user pressed the "reset" button and everything should start over on this screen.
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
        for ( ShapeSceneCollectionBoxPair targetPair : getCollectionBoxPairs() ) {
            targetPair.collectionBoxNode.undo();
        }

        for ( PieceNode piece : getPieceNodes() ) {
            piece.animateToTopOfStack();
        }
        level.filledTargets.reset();
    }

    @Override public void setToolboxEnabled( final boolean enabled ) {

        //Note, this method cannot bail early if the value is the same, since this is used to update state after animation.
        this.toolboxEnabled = enabled;
        toolboxNode.setVisible( enabled );
        toolboxNode.setPickable( enabled );
        toolboxNode.setChildrenPickable( enabled );

        for ( PieceNode pieceNode : getPieceNodes() ) {
            if ( pieceNode.isInStack() ) {
                pieceNode.setVisible( enabled );
                pieceNode.setPickable( enabled );
                pieceNode.setChildrenPickable( enabled );
            }
        }

        for ( ContainerNode containerNode : getContainerNodesInToolbox() ) {
            containerNode.setVisible( enabled );
            containerNode.setPickable( enabled );
            containerNode.setChildrenPickable( enabled );
        }

        for ( PieceIconNode pieceIconNode : getPieceIconNodes() ) {
            pieceIconNode.setVisible( enabled );
            pieceIconNode.setPickable( enabled );
            pieceIconNode.setChildrenPickable( enabled );
        }
    }

    //Get all of the piece nodes, whether they be in the toolbox, play area or in a container.
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

        //Enable the buttons
        containerNode.updateExpansionButtonsEnabled();

        //See if it hits any matching collection boxes
        List<ShapeSceneCollectionBoxPair> pairs = this.getCollectionBoxPairs().sort( ord( new F<ShapeSceneCollectionBoxPair, Double>() {
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

                    final double scale = 0.5 * pair.collectionBoxNode.scaleFactor;
                    containerNode.removeUndoButton();

                    //Order dependence: set in target cell first so that layout code will work better afterwards
                    containerNode.setInCollectionBox( true, pair.value.denominator );
                    final PBounds boxBounds = pair.collectionBoxNode.getFullBounds();
                    final PBounds containerBounds = containerNode.getFullBounds();
                    containerNode.animateToPositionScaleRotation( boxBounds.getCenterX() - containerBounds.getWidth() / 2 * scale / getContainerScale(),
                                                                  boxBounds.getCenterY() - containerBounds.getHeight() / 2 * scale / getContainerScale() + 20, scale, 0,
                                                                  BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( containerNode, false ) );
                    pair.collectionBoxNode.setCompletedFraction( containerNode );
                    containerNode.setAllPickable( false );

                    hit = true;

                    //Send a data collection message
                    for ( VoidFunction1<DropResult> dropListener : dropListeners ) {
                        containerNode.moveDottedLinesToFront();
                        dropListener.apply( new DropResult( true, containerNode.getFractionValue(), pairs.index( 0 ).value.toFraction() ,containerNode.selectedPieceSize.get()) );
                    }
                    break;
                }
                else if ( intersects && !fractionLab ) {
                    //move back to the center of the screen, but only if no other container node is already there.
                    if ( getContainerNodesInPlayArea().exists( new F<ContainerNode, Boolean>() {
                        @Override public Boolean f( final ContainerNode containerNode ) {
                            return containerNode.getOffset().distance( getContainerPosition().toPoint2D() ) < 10;
                        }
                    } ) ) {
                        double angle = Math.PI * 2 * random.nextDouble();
                        animateToPosition( containerNode, getContainerPosition().plus( Vector2D.createPolar( 150, angle ) ), new MoveAwayFromCollectionBoxes( this, containerNode ) );

                        //Send a data collection message
                        for ( VoidFunction1<DropResult> dropListener : dropListeners ) {
                            dropListener.apply( new DropResult( false, containerNode.getFractionValue(), pairs.index( 0 ).value.toFraction(),containerNode.selectedPieceSize.get() ) );
                        }
                    }
                    else {
                        animateToPosition( containerNode, getContainerPosition(), new MoveAwayFromCollectionBoxes( this, containerNode ) );

                        //Send a data collection message
                        for ( VoidFunction1<DropResult> dropListener : dropListeners ) {
                            dropListener.apply( new DropResult( false, containerNode.getFractionValue(), pairs.index( 0 ).value.toFraction(),containerNode.selectedPieceSize.get() ) );
                        }
                    }

                }
            }
        }

        if ( hit ) {
            level.filledTargets.increment();
        }

        //Put the piece back in its starting location, but only if it is empty
        if ( !hit && intersectsToolbox && containerNode.getFractionValue().numerator == 0 ) {
            containerNode.resetNumberOfContainers();
            containerNode.selectedPieceSize.reset();
            if ( !containerNode.startedInToolbox() || fractionLab ) {
                //move on top of stack
                final List<Point2D> list = getContainerNodesThatStartedInToolbox().map( new F<ContainerNode, Point2D>() {
                    @Override public Point2D f( final ContainerNode c ) {
                        return new Point2D.Double( c.initialX, c.initialY );
                    }
                } ).sort( FJUtils.ord( new F<Point2D, Double>() {
                    @Override public Double f( final Point2D p ) {
                        return p.getX();
                    }
                } ) );
                System.out.println( "list = " + list );
                Point2D location = level.shapeType == PIE ? list.last() : list.head();
                containerNode.animateToToolboxStack( location, getContainerNodesThatStartedInToolbox().map( new F<ContainerNode, Double>() {
                    @Override public Double f( final ContainerNode containerNode ) {
                        return containerNode.initialScale;
                    }
                } ).minimum( doubleOrd ) );
            }
            else {
                containerNode.animateHome();
            }
        }

        //Add a new container when the previous one is completed
        if ( !allCollectionBoxesFilled() ) {

            //If no fraction skeleton in play area, move one there
            final List<ContainerNode> inToolbox = getContainerNodesInToolbox();
            final int playing = getContainerNodesInPlayArea().length();
            final int boxing = inToolbox.length();
            if ( playing == 0 && boxing > 0 ) {
                animateToCenterScreen( inToolbox.head(), new NullDelegate() );
            }
        }

        if ( allCollectionBoxesFilled() ) {
            notifyAllCompleted();

            faceNodeDialog.setVisible( true );
            faceNodeDialog.animateToTransparency( 1f, BuildAFractionModule.ANIMATION_TIME );
            faceNodeDialog.setPickable( true );
            faceNodeDialog.setChildrenPickable( true );
            faceNodeDialog.moveToFront();
        }
        if ( !allCollectionBoxesFilled() && hit ) {
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
                animateToPosition( containerNode, getContainerPosition().plus( 100, 100 ), new NullDelegate() );
            }
            else {
                containerNode.animateToPositionScaleRotation( v.head().x, v.head().y,
                                                              toolboxScale( fractionLab ), 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( containerNode, true ) );
                animateToPosition( containerNode, v.head(), new NullDelegate() );
            }
        }
    }

    private List<ContainerNode> getContainerNodesThatStartedInToolbox() {
        return getContainerNodes().filter( new F<ContainerNode, Boolean>() {
            @Override public Boolean f( final ContainerNode containerNode ) {
                return containerNode.startedInToolbox();
            }
        } );
    }

    private List<ContainerNode> getContainerNodesInToolbox() {
        return getContainerNodes().filter( new F<ContainerNode, Boolean>() {
            @Override public Boolean f( final ContainerNode containerNode ) {
                return containerNode.isInToolbox();
            }
        } );
    }

    private List<PieceIconNode> getPieceIconNodes() { return getChildren( this, PieceIconNode.class ); }

    private List<ContainerNode> getContainerNodesInPlayArea() {
        return getContainerNodes().filter( new F<ContainerNode, Boolean>() {
            @Override public Boolean f( final ContainerNode containerNode ) {
                return containerNode.isInPlayArea();
            }
        } );
    }

    //Animate a ContainerNode from the toolbox to the center of the screen when the user fills one collection box and there is no ContainerNode in the play area.
    private void animateToCenterScreen( final ContainerNode containerNode, PActivityDelegate delegate ) { animateToPosition( containerNode, getContainerPosition(), delegate ); }

    //Utility function to animate a ContainerNode to the specified location.
    private void animateToPosition( final ContainerNode containerNode, final Vector2D position, PActivityDelegate delegate ) {
        containerNode.animateToPositionScaleRotation( position.x, position.y,
                                                      getContainerScale(), 0, BuildAFractionModule.ANIMATION_TIME ).
                setDelegate( new CompositeDelegate( new DisablePickingWhileAnimating( containerNode, true ), delegate, new PActivityDelegateAdapter() {
                    @Override public void activityFinished( final PActivity activity ) {
                        containerNode.updateExpansionButtonsEnabled();
                    }
                } ) );
    }

    //The user has dropped a PieceNode, determines whether it has been dropped into a container or if it should go back to the toolbox.
    public void endDrag( final PieceNode piece ) {
        boolean droppedInto = false;
        List<SingleContainerNode> targets = getContainerNodes().bind( _getSingleContainerNodes );
        List<SingleContainerNode> containerNodes = targets.sort( ord( new F<SingleContainerNode, Double>() {
            @Override public Double f( final SingleContainerNode s ) {
                return s.getGlobalFullBounds().getCenter2D().distance( piece.getGlobalFullBounds().getCenter2D() );
            }
        } ) );

        for ( SingleContainerNode target : containerNodes ) {
            if ( target.getGlobalFullBounds().intersects( piece.getGlobalFullBounds() ) && !target.isInToolbox() && !target.willOverflow( piece ) && !target.parent.isInCollectionBox() ) {
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
                        return !n.parent.isInCollectionBox();
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

    //Determines how large the containers should be rendered.
    public double getContainerScale() {
        final double smaller = 0.6 * 1.15;
        return isFractionLab() ? smaller :
               isMixedNumbers() ? smaller : 1.0;
    }

    public boolean isFractionLab() { return fractionLab; }

    public void containerNodeAnimationToToolboxFinished( final ContainerNode containerNode ) {
        setToolboxEnabled( toolboxEnabled );
    }

    //The user started dragging a PieceNode.  In the "Fractions Lab" tab, create a copy to make it seem like there is an endless stack of pieces.
    public void startDrag( final PieceNode pieceNode ) {
        if ( isFractionLab() ) {
            //create another piece node under this one
            PieceNode copy = pieceNode.copy();
            addChild( copy );
            copy.setGlobalRotation( pieceNode.getGlobalRotation() );
            copy.setGlobalScale( pieceNode.getGlobalScale() );
            copy.setGlobalTranslation( pieceNode.getGlobalTranslation() );
        }
        fireInteractionEvent();
    }

    //Location where a piece can be dropped.  The angle is included for pies, it is always 0.0 for bars.
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
        if ( level.shapeType == PIE ) {
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

    //Update the list of user-created fractions for purposes of highlighting collection boxes when the first match is made.
    public void syncModelFractions() { level.createdFractions.set( getUserCreatedFractions() ); }

    //When a container is added to a container node, move it to the left if it overlaps the scoring boxes.
    public void containerAdded( final ContainerNode c ) {
        moveContainerNodeAwayFromCollectionBoxes( c );
    }

    //The user started dragging a ContainerNode.  In the "Fractions Lab" tab, create a copy to give the sense of an endles stack.
    public void startDrag( final ContainerNode parent ) {
        if ( isFractionLab() && parent.isInToolbox() && getContainerNodes().length() < 4 ) {
            ContainerNode copy = parent.copy();
            copy.setInitialState( parent.initialX, parent.initialY, parent.initialScale );
            addChild( copy );
            copy.setGlobalScale( parent.getGlobalScale() );
            copy.setGlobalTranslation( parent.getGlobalTranslation() );
            parent.moveToFront();
            copy.updateExpansionButtonsEnabled();
        }
        fireInteractionEvent();
    }

    //if its right edge is past the left edge of any collection box, move it left
    public void moveContainerNodeAwayFromCollectionBoxes( final ContainerNode containerNode ) {
        double rightSide = containerNode.getGlobalFullBounds().getMaxX();
        double edge = isFractionLab() ? STAGE_SIZE.width : getCollectionBoxPairs().map( new F<ShapeSceneCollectionBoxPair, Double>() {
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

    //Get all the fractions the user has created in this challeng so far.
    private List<Fraction> getUserCreatedFractions() { return getContainerNodes().filter( not( _isInTargetCell ) ).map( _getFractionValue ); }

    //Remove a single piece from its parent container and animate it back to the top of its stack in the toolbox.
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

    //Get all the container nodes in this scene.
    private List<ContainerNode> getContainerNodes() { return getChildren( this, ContainerNode.class ); }

    //Determine if the user has filled all the collection boxes.
    private boolean allCollectionBoxesFilled() {
        return getCollectionBoxPairs().map( new F<ShapeSceneCollectionBoxPair, Boolean>() {
            @Override public Boolean f( final ShapeSceneCollectionBoxPair pair ) {
                return pair.collectionBoxNode.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == getCollectionBoxPairs().length();
    }

    //When the user pressed "undo" on a collection box, record it in the model and hide the face if it was showing.
    public void collectionBoxUndone() {
        level.filledTargets.decrement();
        faceNodeDialog.animateToTransparency( 0.0f, BuildAFractionModule.ANIMATION_TIME );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );
    }

    //Determine the location of a card within a stack.  This code is complicated because it takes into account the number of stacks and the card types
    public Vector2D getLocation( final int stackIndex, final int cardIndex, final PieceNode card ) {
        int sign = level.shapeType == PIE ? -1 : +1;
        List<List<Integer>> groups = level.pieces.group( intEqual );
        double deltaWithinStack = getCardOffsetWithinStack( groups.index( stackIndex ).length(), cardIndex );
        int yOffset = level.shapeType == ShapeType.BAR ? 25 : 0;
        if ( isFractionLab() && level.shapeType == PIE ) {
            yOffset = yOffset + 12;
        }
        return new Vector2D( layoutXOffset + 30 + deltaWithinStack * sign + stackIndex * distanceBetweenStacks,
                             STAGE_SIZE.height - 117 - CARD_SPACING_DY * cardIndex + yOffset );
    }

    //After pieces finished animating, update the z-ordering so the dotted lines don't appear in front.
    public void movedNonStackCardsToFront() {

        //Fixed: Dotted lines sometimes disappear when dragging a card out of the toolbox
        getContainerNodes().foreach( new Effect<ContainerNode>() {
            @Override public void e( final ContainerNode containerNode ) {
                containerNode.moveDottedLinesToFront();
            }
        } );
    }

    //Determine the location of a card within its stack, for layout
    private double getCardOffsetWithinStack( final int numInGroup, final int cardIndex ) {
        double totalSpacing = -CARD_SPACING_DX * ( numInGroup - 1 );
        return getCardOffsetWithinStack( numInGroup, cardIndex, totalSpacing );
    }

    //Determine the location of a card within its stack, for layout
    private double getCardOffsetWithinStack( final int numInGroup, final int cardIndex, final double totalSpacing ) {
        return numInGroup == 1 ? 0 :
               new LinearFunction( 0, numInGroup - 1, -totalSpacing / 2, +totalSpacing / 2 ).evaluate( cardIndex );
    }
}