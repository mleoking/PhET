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
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.SceneNode;
import edu.colorado.phet.fractions.buildafraction.view.Stack;
import edu.colorado.phet.fractions.buildafraction.view.StackContext;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.BackButton;
import edu.colorado.phet.fractions.common.view.FNode;
import edu.colorado.phet.fractions.common.view.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerNode._getSingleContainerNodes;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerNode._isAtStartingLocation;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.createPieSlice;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.createRect;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.PieceIconNode.TINY_SCALE;
import static edu.colorado.phet.fractions.common.math.Fraction._toDouble;
import static edu.colorado.phet.fractions.common.util.FJUtils.ord;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static edu.colorado.phet.fractions.common.view.RefreshButtonNode.BUTTON_COLOR;
import static fj.Equal.intEqual;
import static fj.Ord.doubleOrd;
import static fj.data.List.iterableList;
import static fj.function.Booleans.not;

/**
 * Node for the scene when the user is constructing fractions with pictures (shapes).
 *
 * @author Sam Reid
 */
public class ShapeSceneNode extends SceneNode implements ContainerContext, PieceContext, StackContext<PieceNode> {
    private final List<Target> targetPairs;
    private final RichPNode toolboxNode;
    private final VBox faceNodeDialog;

    //Declare type-specific wrappers for declaration site variance to make map call site more readable
    private final F<PieceIconNode, Double> _minX = FNode._minX();
    private final F<ContainerNode, Double> _maxX = FNode._maxX();

    private final int spacing;
    private final int layoutXOffset;
    private final ShapeLevel level;

    public ShapeSceneNode( final int levelIndex, final BuildAFractionModel model, final PDimension STAGE_SIZE, final SceneContext context, BooleanProperty soundEnabled ) {
        super( soundEnabled );
        this.level = model.getShapeLevel( levelIndex );
        spacing = level.shapeType == ShapeType.BAR ? 140 : 120;

        final BackButton backButton = new BackButton( new VoidFunction0() {
            public void apply() {
                context.goToLevelSelectionScreen();
            }
        } ) {{
            setOffset( INSET, INSET );
        }};
        addChild( backButton );

        //Create the scoring cells with target patterns
        ArrayList<Target> pairList = new ArrayList<Target>();
        for ( int i = 0; i < level.targets.length(); i++ ) {
            Fraction target = level.getTarget( i );

            FractionNode f = new FractionNode( target, 0.33 );
            final ShapeScoreBoxNode cell = new ShapeScoreBoxNode(
                    this,
                    level.targets.maximum( ord( _toDouble ) ) );
            pairList.add( new Target( cell, new ZeroOffsetNode( f ), target ) );
        }
        this.targetPairs = iterableList( pairList );

        List<PNode> patterns = this.targetPairs.map( new F<Target, PNode>() {
            @Override public PNode f( final Target pair ) {
                return pair.node;
            }
        } );
        double maxWidth = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getWidth();
            }
        } ).maximum( doubleOrd );
        double maxHeight = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( doubleOrd );

        //Layout for the scoring cells and target patterns
        double separation = 5;
        double rightInset = 10;
        final PBounds targetCellBounds = pairList.get( 0 ).getCell().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = INSET;
        double insetY = 5;
        for ( Target pair : pairList ) {

            pair.cell.setOffset( offsetX, offsetY );
            pair.node.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.cell );
            addChild( pair.node );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        ContainerNode firstContainerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne(), level.shapeType ) {{
            setInitialPosition( 285, 200 );
        }};
        addChild( firstContainerNode );

        //Pieces in the toolbar that the user can drag
        List<List<Integer>> groups = level.pieces.group( intEqual );
        int numGroups = groups.length();
        layoutXOffset = ( 6 - numGroups ) * spacing / 4;
        final int toolboxHeight = level.shapeType == ShapeType.BAR ? 100 : 140;
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
            final ContainerNode containerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne(), level.shapeType ) {{
                final int sign = level.shapeType == ShapeType.PIE ? -1 : +1;
                setInitialState( layoutXOffset + INSET + 20 + delta * sign + finalGroupIndex * spacing,
                                 STAGE_SIZE.height - INSET - toolboxHeight + 20 + delta, TINY_SCALE );
            }};
            addChild( containerNode );
        }

        //Add a piece container toolbox the user can use to get containers
        toolboxNode = new RichPNode() {{

            //Width is just before the first group to the last container node in the toolbox
            double min = FNode.getChildren( ShapeSceneNode.this, PieceIconNode.class ).map( _minX ).minimum( doubleOrd );
            double max = FNode.getChildren( ShapeSceneNode.this, ContainerNode.class ).map( _maxX ).maximum( doubleOrd );
            double width = max - min;
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width + INSET * 6, toolboxHeight, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, BuildAFractionCanvas.controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( min - INSET * 3, AbstractFractionsCanvas.STAGE_SIZE.height - INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );
        toolboxNode.moveToBack();

        final HTMLImageButtonNode nextButton = new HTMLImageButtonNode( Strings.NEXT, new PhetFont( 20, true ), BUTTON_COLOR ) {{
            setUserComponent( Components.nextButton );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    context.goToNextShapeLevel( levelIndex + 1 );
                }
            } );
        }};
        faceNodeDialog = new VBox( new FaceNode( 200 ), model.isLastLevel( levelIndex ) ? new PNode() : nextButton ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 50 );
        }};

        faceNodeDialog.setTransparency( 0 );
        faceNodeDialog.setVisible( false );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );

        addChild( faceNodeDialog );

        double minScoreCellX = iterableList( pairList ).map( new F<Target, Double>() {
            @Override public Double f( final Target target ) {
                return target.cell.getFullBounds().getMinX();
            }
        } ).minimum( doubleOrd );
        final PhetPText levelReadoutTitle = new PhetPText( MessageFormat.format( Strings.LEVEL__PATTERN, levelIndex + 1 ), new PhetFont( 32, true ) );
        levelReadoutTitle.setOffset( ( minScoreCellX - INSET ) / 2 - levelReadoutTitle.getFullWidth() / 2, backButton.getFullBounds().getCenterY() - levelReadoutTitle.getFullHeight() / 2 );
        addChild( levelReadoutTitle );

        final TextButtonNode resetButton = new TextButtonNode( Strings.RESET, AbstractFractionsCanvas.CONTROL_FONT, BUTTON_COLOR ) {{
            setUserComponent( Components.resetButton );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    reset();
                }
            } );
        }};

        final VoidFunction0 _resampleLevel = new VoidFunction0() {
            public void apply() {
                context.resampleShapeLevel( levelIndex );
            }
        };
        final RefreshButtonNode refreshButton = new RefreshButtonNode( _resampleLevel );

        addChild( new HBox( resetButton, refreshButton ) {{
            setOffset( levelReadoutTitle.getCenterX() - getFullBounds().getWidth() / 2, levelReadoutTitle.getMaxY() + INSET );
        }} );
    }

    private void reset() {
        //Eject everything from target containers
        //Split everything
        //Return everything home
        for ( Target targetPair : targetPairs ) {
            targetPair.cell.splitIt();
        }
        for ( ContainerNode containerNode : getContainerNodes() ) {
            containerNode.splitAll();
            containerNode.animateHome();
            containerNode.selectedPieceSize.reset();
            containerNode.resetNumberOfContainers();
        }
        for ( PieceNode piece : getPieceNodes() ) {
            piece.moveToTopOfStack();
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

    public void endDrag( final ContainerNode containerNode ) {
        //See if it hits any matches
        List<Target> pairs = targetPairs.sort( ord( new F<Target, Double>() {
            @Override public Double f( final Target t ) {
                return t.cell.getGlobalFullBounds().getCenter2D().distance( containerNode.getGlobalFullBounds().getCenter2D() );
            }
        } ) );
        boolean hit = false;

        //Only consider the closest box, otherwise students can overlap many boxes instead of thinking of the correct answer
        for ( Target pair : pairs.take( 1 ) ) {
            final boolean intersects = pair.cell.getGlobalFullBounds().intersects( containerNode.getGlobalFullBounds() );
            final boolean matchesValue = containerNode.getFractionValue().approxEquals( pair.value );
            final boolean occupied = pair.getCell().isCompleted();
            if ( intersects && matchesValue && !occupied ) {
                final double scale = 0.5;
                containerNode.removeSplitButton();

                //Order dependence: set in target cell first so that layout code will work better afterwards
                containerNode.setInTargetCell( true, pair.value.denominator );
                containerNode.animateToPositionScaleRotation( pair.cell.getFullBounds().getCenterX() - containerNode.getFullBounds().getWidth() / 2 * scale,
                                                              pair.cell.getFullBounds().getCenterY() - containerNode.getFullBounds().getHeight() / 2 * scale + 20, scale, 0, 200 );
                pair.cell.setCompletedFraction( containerNode );
                containerNode.setAllPickable( false );

                hit = true;
                break;
            }
            else if ( intersects ) {
                animateToCenterScreen( containerNode );
            }
        }

        if ( hit ) {
            level.incrementFilledTargets();
        }

        //Put the piece back in its starting location, but only if it is empty
        if ( !hit && containerNode.getGlobalFullBounds().intersects( toolboxNode.getGlobalFullBounds() ) && containerNode.getFractionValue().numerator == 0 ) {
            if ( containerNode.belongsInToolbox() ) {
                containerNode.resetNumberOfContainers();
                containerNode.selectedPieceSize.reset();
            }
            containerNode.animateHome();
        }

        //Add a new container when the previous one is completed
        if ( !allTargetsComplete() ) {

            //If no fraction skeleton in play area, move one there
            final List<ContainerNode> inPlayArea = getContainerNodes().filter( new F<ContainerNode, Boolean>() {
                @Override public Boolean f( final ContainerNode containerNode ) {
                    return !containerNode.isInTargetCell() && !containerNode.isAtStartingLocation();
                }
            } );
            final List<ContainerNode> inToolbox = getContainerNodes().filter( _isAtStartingLocation );
            if ( inPlayArea.length() == 0 && inToolbox.length() > 0 ) {
                ContainerNode g = inToolbox.head();
                animateToCenterScreen( g );
            }
        }

        if ( allTargetsComplete() ) {
            playSoundForAllComplete();

            faceNodeDialog.setVisible( true );
            faceNodeDialog.animateToTransparency( 1f, 200 );
            faceNodeDialog.setPickable( true );
            faceNodeDialog.setChildrenPickable( true );
            faceNodeDialog.moveToFront();
        }
        if ( !allTargetsComplete() && hit ) {
            playSoundForOneComplete();
        }

        syncModelFractions();
    }

    private void animateToCenterScreen( final ContainerNode g ) {
        Vector2D offset = level.shapeType == ShapeType.BAR ? new Vector2D( -100, 50 ) : Vector2D.ZERO;
        g.animateToPositionScaleRotation( toolboxNode.getCenterX() - g.getFullBounds().getWidth() / 2 + offset.x, 200 + offset.y, 1, 0, 1000 );
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
            piece.moveToTopOfStack();
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

    public @Data static class DropLocation {
        public final Vector2D position;
        public final double angle;
    }

    //Piece dropped into container
    private void dropInto( final PieceNode piece, final SingleContainerNode container ) {
        if ( level.shapeType == ShapeType.PIE ) {
            DropLocation dropLocation = container.getDropLocation( piece, level.shapeType );
            PTransformActivity activity = piece.animateToPositionScaleRotation( dropLocation.position.x, dropLocation.position.y, 1, 0, 200 );

            //Should already be at correct angle, update again just in case
            if ( piece instanceof PiePieceNode ) {
                ( (PiePieceNode) piece ).setPieceRotation( dropLocation.angle );
            }
            piece.setPickable( false );
            piece.setChildrenPickable( false );
            activity.setDelegate( new PActivityDelegate() {
                public void activityStarted( final PActivity activity ) {
                }

                public void activityStepped( final PActivity activity ) {
                }

                public void activityFinished( final PActivity activity ) {
                    container.addPiece( piece );
                    syncModelFractions();
                }
            } );
        }

        //TODO: Factor out duplicated code
        else {
            Point2D translation = container.getGlobalTranslation();
            piece.globalToLocal( translation );
            piece.localToParent( translation );
            DropLocation dropLocation = container.getDropLocation( piece, level.shapeType );
            final Vector2D a = dropLocation.position.plus( translation );
            PTransformActivity activity = piece.animateToPositionScaleRotation( a.x, a.y, 1, dropLocation.angle, 200 );
            piece.setPickable( false );
            piece.setChildrenPickable( false );
            activity.setDelegate( new PActivityDelegate() {
                public void activityStarted( final PActivity activity ) {
                }

                public void activityStepped( final PActivity activity ) {
                }

                public void activityFinished( final PActivity activity ) {
                    container.addPiece( piece );
                    syncModelFractions();
                }
            } );
        }
    }

    public void syncModelFractions() {
        level.createdFractions.set( getUserCreatedFractions() );
    }

    //When a container is added to a container node, move it to the left if it overlaps the scoring boxes.
    public void containerAdded( final ContainerNode containerNode ) {

        //if its right edge is past the left edge of any target cell, move it left
        double rightSide = containerNode.getGlobalFullBounds().getMaxX();
        double edge = targetPairs.map( new F<Target, Double>() {
            @Override public Double f( final Target target ) {
                return target.getCell().getGlobalFullBounds().getMinX();
            }
        } ).minimum( doubleOrd );
        double overlap = rightSide - edge;
        if ( overlap > 0 ) {
            double amountToMoveLeft = 20 + overlap;
            containerNode.animateToPositionScaleRotation( containerNode.getXOffset() - amountToMoveLeft, containerNode.getYOffset(), containerNode.getScale(), containerNode.getRotation(), 200 );
        }
    }

    //TODO: when we have multiple containers, this will have to be modified
    private List<Fraction> getUserCreatedFractions() {
        return getContainerNodes().filter( not( ContainerNode._isInTargetCell ) ).map( ContainerNode._getFractionValue );
    }

    public void splitPieceFromContainer( final PieceNode piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        piece.setPickable( true );
        piece.setChildrenPickable( true );
        piece.moveToTopOfStack();
    }

    private List<ContainerNode> getContainerNodes() {
        return getChildren( this, ContainerNode.class );
    }

    private boolean allTargetsComplete() {
        return targetPairs.map( new F<Target, Boolean>() {
            @Override public Boolean f( final Target pair ) {
                return pair.cell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == targetPairs.length();
    }

    public void scoreBoxSplit() {
        level.filledTargets.decrement();
        faceNodeDialog.animateToTransparency( 0.0f, 200 );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );
    }

    private static final double CARD_SPACING_DX = 3;
    private static final double CARD_SPACING_DY = CARD_SPACING_DX;

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