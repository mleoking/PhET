// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.Equal;
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
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.pictures.PictureLevel;
import edu.colorado.phet.fractions.buildafraction.model.pictures.PictureTarget;
import edu.colorado.phet.fractions.buildafraction.model.pictures.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.SceneNode;
import edu.colorado.phet.fractions.buildafraction.view.Stack;
import edu.colorado.phet.fractions.buildafraction.view.StackContext;
import edu.colorado.phet.fractions.common.util.FJUtils;
import edu.colorado.phet.fractions.common.view.BackButton;
import edu.colorado.phet.fractions.common.view.FNode;
import edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.buildafraction.view.pictures.ContainerNode._getSingleContainerNodes;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.ContainerNode._isAtStartingLocation;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.PieceIconNode.TINY_SCALE;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.RefreshButtonNode.BUTTON_COLOR;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.createPieSlice;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.createRect;
import static edu.colorado.phet.fractions.common.util.FJUtils.ord;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static fj.Ord.doubleOrd;
import static fj.data.List.iterableList;
import static fj.function.Booleans.not;

/**
 * Node for the scene when the user is constructing fractions with pictures (shapes).
 *
 * @author Sam Reid
 */
public class PictureSceneNode extends SceneNode implements ContainerContext, PieceContext, StackContext<PieceNode> {
    private final BuildAFractionModel model;
    private final List<Target> targetPairs;
    private final RichPNode toolboxNode;
    private final VBox faceNodeDialog;
    public final int levelIndex;
    private final SceneContext context;

    //Declare type-specific wrappers for declaration site variance to make map call site more readable
    private final F<PieceIconNode, Double> _minX = FNode._minX();
    private final F<ContainerNode, Double> _maxX = FNode._maxX();
    private ArrayList<Stack> stackList;

    final int spacing = 140;
    private final int layoutXOffset;
    private final PictureLevel level;

    public PictureSceneNode( final int levelIndex, final BuildAFractionModel model, final PDimension STAGE_SIZE, final SceneContext context, BooleanProperty soundEnabled ) {
        super( soundEnabled );
        this.level = model.getPictureLevel( levelIndex );
        this.model = model;
        this.levelIndex = levelIndex;
        this.context = context;

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
        for ( int i = 0; i < model.getPictureLevel( levelIndex ).targets.length(); i++ ) {
            PictureTarget target = model.getPictureLevel( levelIndex ).getTarget( i );

            FractionNode f = new FractionNode( target.fraction, 0.33 );
            final PictureScoreBoxNode cell = new PictureScoreBoxNode( target.fraction.numerator, target.fraction.denominator,
                                                                      level.createdFractions, this,
                                                                      level.flashTargetCellOnMatch );
            pairList.add( new Target( cell, new ZeroOffsetNode( f ), target.fraction ) );
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
        List<List<Integer>> groups = level.pieces.group( Equal.intEqual );
        int numGroups = groups.length();
        layoutXOffset = ( 6 - numGroups ) * spacing / 4;
        stackList = new ArrayList<Stack>();
        final int toolboxHeight = level.shapeType == ShapeType.HORIZONTAL_BAR ? 100 : 140;
        for ( P2<List<Integer>, Integer> groupWithIndex : groups.zipIndex() ) {
            int stackIndex = groupWithIndex._2();
            List<Integer> group = groupWithIndex._1();

            Rectangle2D bounds = null;
            ArrayList<PieceNode> pieces = new ArrayList<PieceNode>();
            for ( final P2<Integer, Integer> pieceDenominatorWithIndex : group.zipIndex() ) {
                int pieceDenominator = pieceDenominatorWithIndex._1();
                int cardIndex = pieceDenominatorWithIndex._2();

                //Choose the shape for the level, pies or horizontal bars
                final PhetPPath shape = level.shapeType == ShapeType.HORIZONTAL_BAR ? new PhetPPath( createRect( pieceDenominator ), level.color, PieceNode.stroke, Color.black )
                                                                                    : new PhetPPath( createPieSlice( pieceDenominator ), level.color, PieceNode.stroke, Color.black );
                final PieceNode piece = new PieceNode( pieceDenominator, PictureSceneNode.this, shape, level.shapeType );
                piece.setOffset( getLocation( stackIndex, cardIndex, piece ).toPoint2D() );
                piece.setInitialScale( TINY_SCALE );

                PictureSceneNode.this.addChild( piece );
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
            stackList.add( stack );

            final PieceIconNode child = new PieceIconNode( group.head(), level.shapeType );
            final PieceNode firstPiece = pieces.get( 0 );

            child.setOffset( level.shapeType == ShapeType.PIE ? new Point2D.Double( firstPiece.getFullBounds().getMaxX() - child.getFullBounds().getWidth(), firstPiece.getYOffset() )
                                                              : pieces.get( 0 ).getOffset() );
            addChild( child );
            child.moveToBack();
        }

        //Containers the user can drag out of the toolbox.
        final int finalGroupIndex = groups.length();
        final int numInGroup = level.targets.length() - 1;
        for ( int i = 0; i < numInGroup; i++ ) {
            final double delta = toDelta( numInGroup, i );
            final ContainerNode containerNode = new ContainerNode( this, this, level.hasValuesGreaterThanOne(), level.shapeType ) {{
                setInitialState( layoutXOffset + INSET + 20 + delta + finalGroupIndex * spacing,
                                 STAGE_SIZE.height - INSET - toolboxHeight + 20 + delta, TINY_SCALE );
            }};
            addChild( containerNode );
        }

        //Add a piece container toolbox the user can use to get containers
        toolboxNode = new RichPNode() {{

            //Width is just before the first group to the last container node in the toolbox
            double min = FNode.getChildren( PictureSceneNode.this, PieceIconNode.class ).map( _minX ).minimum( doubleOrd );
            double max = FNode.getChildren( PictureSceneNode.this, ContainerNode.class ).map( _maxX ).maximum( doubleOrd );
            double width = max - min;
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width + INSET * 6, toolboxHeight, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, BuildAFractionCanvas.controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( min - INSET * 3, AbstractFractionsCanvas.STAGE_SIZE.height - INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );
        toolboxNode.moveToBack();

        faceNodeDialog = new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", new PhetFont( 20, true ), BUTTON_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    context.goToNextPictureLevel( levelIndex + 1 );
                }
            } );
        }} ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 100 );
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
        final PhetPText levelReadoutTitle = new PhetPText( "Level " + ( levelIndex + 1 ), new PhetFont( 32, true ) );
        levelReadoutTitle.setOffset( ( minScoreCellX - INSET ) / 2 - levelReadoutTitle.getFullWidth() / 2, backButton.getFullBounds().getCenterY() - levelReadoutTitle.getFullHeight() / 2 );
        addChild( levelReadoutTitle );

        final TextButtonNode resetButton = new TextButtonNode( "Reset", AbstractFractionsCanvas.CONTROL_FONT, BUTTON_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    reset();
                }
            } );
        }};

        final RefreshButtonNode refreshButton = new RefreshButtonNode( _resampleLevel );

        addChild( new HBox( resetButton, refreshButton ) {{
            setOffset( levelReadoutTitle.getCenterX() - getFullBounds().getWidth() / 2, levelReadoutTitle.getMaxY() + INSET );
        }} );
    }

    private double toDelta( final int numInGroup, final int pieceIndex ) {
        double dx = level.shapeType == ShapeType.HORIZONTAL_BAR ? -3.0 : -3.0;
        double totalHorizontalSpacing = dx * ( numInGroup - 1 );
        return getDelta( numInGroup, pieceIndex, totalHorizontalSpacing );
    }

    private double getDelta( final int numInGroup, final int pieceIndex, final double totalHorizontalSpacing ) {
        LinearFunction offset = new LinearFunction( 0, numInGroup - 1, -totalHorizontalSpacing / 2, +totalHorizontalSpacing / 2 );
        return numInGroup == 1 ? 0 : offset.evaluate( pieceIndex );
    }

    //clear cards and target values, then get some new ones
    public VoidFunction0 _resampleLevel = new VoidFunction0() {
        public void apply() {
            context.resamplePictureLevel( levelIndex );
        }
    };

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

    public void endDrag( final ContainerNode containerNode, final PInputEvent event ) {
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
                containerNode.animateToPositionScaleRotation( pair.cell.getFullBounds().getCenterX() - containerNode.getPiecesWidth() / 2 * scale,
                                                              pair.cell.getFullBounds().getCenterY() - containerNode.getPiecesHeight() / 2 * scale, scale, 0, 200 );
                pair.cell.setCompletedFraction( containerNode );
                containerNode.setAllPickable( false );
                containerNode.setInTargetCell( true );
                hit = true;
                break;
            }
        }

        if ( hit ) {
            level.incrementFilledTargets();
        }

        //Put the piece back in its starting location, but only if it is empty
        if ( !hit && containerNode.getGlobalFullBounds().intersects( toolboxNode.getGlobalFullBounds() ) && containerNode.getFractionValue().numerator == 0 ) {
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
                g.animateToPositionScaleRotation( toolboxNode.getCenterX() - g.getFullBounds().getWidth() / 2, 300, 1, 0, 1000 );
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

    public void endDrag( final PieceNode piece, final PInputEvent event ) {
        boolean droppedInto = false;
        List<SingleContainerNode> targets = getContainerNodes().bind( _getSingleContainerNodes );
        List<SingleContainerNode> containerNodes = targets.sort( ord( new F<SingleContainerNode, Double>() {
            @Override public Double f( final SingleContainerNode s ) {
                return s.getGlobalFullBounds().getCenter2D().distance( piece.getGlobalFullBounds().getCenter2D() );
            }
        } ) );

        for ( SingleContainerNode target : containerNodes ) {
            if ( target.getGlobalFullBounds().intersects( piece.getGlobalFullBounds() ) && !target.isInToolbox() && !target.willOverflow( piece ) ) {
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

    public double getNextAngle( final PieceNode pieceNode ) {

        for ( ContainerNode containerNode : getContainerNodes() ) {
            System.out.println( "containerNode = " + containerNode + ", t = " + containerNode.isInToolbox() );
        }

        //find closest container
        SingleContainerNode closest = getContainerNodes().
                bind( _getSingleContainerNodes ).
                filter( new F<SingleContainerNode, Boolean>() {
                    @Override public Boolean f( final SingleContainerNode n ) {
                        return !n.isInToolbox();
                    }
                } ).
                sort( FJUtils.ord( new F<SingleContainerNode, Double>() {
                    @Override public Double f( final SingleContainerNode n ) {
                        return n.getGlobalFullBounds().getCenter2D().distance( pieceNode.getGlobalFullBounds().getCenter2D() );
                    }
                } ) ).
                head();
        DropLocation dropLocation = closest.getDropLocation( pieceNode, level.shapeType );
        return dropLocation.angle;
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
            piece.setPieceRotation( dropLocation.angle );
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

    public void syncModelFractions() { level.createdFractions.set( getUserCreatedFractions() ); }

    //TODO: when we have multiple containers, this will have to be modified
    private List<Fraction> getUserCreatedFractions() { return getContainerNodes().filter( not( ContainerNode._isInTargetCell ) ).map( ContainerNode._getFractionValue ); }

    public void splitPieceFromContainer( final PieceNode piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        piece.setPickable( true );
        piece.setChildrenPickable( true );
        piece.moveToTopOfStack();
    }

    private List<ContainerNode> getContainerNodes() { return getChildren( this, ContainerNode.class ); }

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

    public Vector2D getLocation( final int stackIndex, final int cardIndex, final PieceNode card ) {
        int sign = level.shapeType == ShapeType.PIE ? -1 : +1;
        List<List<Integer>> groups = level.pieces.group( Equal.intEqual );
        double delta = toDelta( groups.index( stackIndex ).length(), cardIndex );
        return new Vector2D( layoutXOffset + INSET + 20 + delta * sign + stackIndex * spacing, STAGE_SIZE.height - INSET - 127 + 20 + delta );
    }
}