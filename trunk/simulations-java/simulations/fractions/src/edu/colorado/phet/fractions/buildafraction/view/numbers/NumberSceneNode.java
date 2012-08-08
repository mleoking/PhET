// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
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
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.SceneNode;
import edu.colorado.phet.fractions.buildafraction.view.Stack;
import edu.colorado.phet.fractions.buildafraction.view.StackContext;
import edu.colorado.phet.fractions.buildafraction.view.shapes.SceneContext;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.BackButton;
import edu.colorado.phet.fractions.common.view.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static fj.data.Option.some;
import static java.awt.Color.darkGray;

/**
 * Node for the scene when the user is constructing fractions with numbers.
 *
 * @author Sam Reid
 */
public class NumberSceneNode extends SceneNode implements NumberDragContext, FractionDraggingContext, StackContext<NumberCardNode> {
    private final ArrayList<FractionNode> fractionNodes = new ArrayList<FractionNode>();
    private final PNode rootNode;
    public final List<ScoreBoxPair> pairList;
    private final RichPNode toolboxNode;
    private final VBox faceNodeDialog;

    //When sending cards back to the toolbox, make sure they have the right location and z-order.
    private final ArrayList<Stack> stackList;

    private static final double spacingBetweenNumbers = 20;
    private static final double leftRightInset = 20;
    private static final double spacingBetweenNumbersAndFractionSkeleton = 50;
    public final NumberLevel level;
    private final Dimension2DDouble singleDigitCardSize;
    private final Dimension2DDouble doubleDigitCardSize;
    private final List<List<Integer>> stacks;

    public NumberSceneNode( final int levelIndex, final PNode rootNode, final BuildAFractionModel model, final PDimension STAGE_SIZE, final SceneContext context, BooleanProperty soundEnabled ) {
        super( soundEnabled );
        level = model.getNumberLevel( levelIndex );
        this.rootNode = rootNode;

        final BackButton backButton = new BackButton( new VoidFunction0() {
            public void apply() {
                context.goToLevelSelectionScreen();
            }
        } ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
        addChild( backButton );

        //Create the scoring cells with target patterns
        ArrayList<ScoreBoxPair> pairs = new ArrayList<ScoreBoxPair>();
        for ( int i = 0; i < model.getNumberLevel( levelIndex ).targets.length(); i++ ) {
            NumberTarget target = model.getNumberLevel( levelIndex ).targets.index( i );

            ArrayList<PatternNode> nodes = new ArrayList<PatternNode>();
            for ( int k = 0; k < target.filledPattern.length(); k++ ) {
                nodes.add( new PatternNode( target.filledPattern.index( k ), target.color ) {{

                    //Scale all nodes up to be the same size, otherwise it is too much work fine tuning each representation to have a good width
                    double desiredWidth = 80;
                    double width = this.getFullBounds().getWidth();
                    double scale = desiredWidth / width;
                    scale( scale );
                    scaleStrokes( 1.0 / scale );
                }} );
            }
            HBox patternNode = new HBox( nodes.toArray( new PNode[nodes.size()] ) );
            pairs.add( new ScoreBoxPair( new NumberScoreBoxNode( target.fraction.numerator, target.fraction.denominator,
                                                                 rootNode, model, this ), new ZeroOffsetNode( patternNode ) ) );
        }
        pairList = List.iterableList( pairs );

        List<PNode> patterns = pairList.map( new F<ScoreBoxPair, PNode>() {
            @Override public PNode f( final ScoreBoxPair pair ) {
                return pair.patternNode;
            }
        } );
        double maxWidth = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getWidth();
            }
        } ).maximum( Ord.doubleOrd );
        double maxHeight = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( Ord.doubleOrd );

        //Layout for the scoring cells and target patterns
        double separation = 5;
        double rightInset = 10;
        final PBounds targetCellBounds = pairs.get( 0 ).getTargetCell().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
//        double offsetY = title.getFullHeight() + 5;
        double offsetY = INSET;
        double insetY = 10;
//        addChild( title );
        for ( ScoreBoxPair pair : pairs ) {

            pair.targetCell.setOffset( offsetX, offsetY );
            pair.patternNode.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.targetCell );
            addChild( pair.patternNode );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        //Add a piece container toolbox the user can use to get containers
        //Put numbers on cards so you can see how many there are in a stack
        //I suspect it will look awkward unless all cards have the same dimensions

        stacks = level.numbers.group( Equal.intEqual );

        //Find the max size of each number node, so we can create a consistent card size
        singleDigitCardSize = getCardSize( stacks, new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer.toString().length() < 2;
            }
        } );
        doubleDigitCardSize = getCardSize( stacks, new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer.toString().length() >= 2;
            }
        } );

        //Create a stack of cards for each unique number
        List<List<NumberCardNode>> cardNodes = stacks.map( new F<List<Integer>, List<NumberCardNode>>() {
            @Override public List<NumberCardNode> f( final List<Integer> integers ) {
                return integers.map( new F<Integer, NumberCardNode>() {
                    @Override public NumberCardNode f( final Integer integer ) {
                        return new NumberCardNode( integer.toString().length() < 2 ? singleDigitCardSize : doubleDigitCardSize, integer, NumberSceneNode.this );
                    }
                } );
            }
        } );

        final FractionNode fractionNode = new FractionNode( this );
        fractionNode.setScale( 1.0 );

        final double extentX = leftRightInset * 2 + getStackOffset( stacks.length() ) - singleDigitCardSize.width + spacingBetweenNumbersAndFractionSkeleton + fractionNode.getFullBounds().getWidth();

        //Create the toolbox node
        toolboxNode = new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, extentX, 130, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, BuildAFractionCanvas.controlPanelStroke, darkGray );
            addChild( border );
            final double offsetX = Math.max( ( AbstractFractionsCanvas.STAGE_SIZE.width - 150 ) / 2 - this.getFullWidth() / 2, INSET );

            setOffset( offsetX, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );

        stackList = new ArrayList<Stack>();
        for ( P2<List<NumberCardNode>, Integer> cards : cardNodes.zipIndex() ) {
            final Integer stackIndex = cards._2();
            final Stack<NumberCardNode> stack = new Stack<NumberCardNode>( cards._1(), stackIndex, this );
            stackList.add( stack );
            for ( P2<NumberCardNode, Integer> cardNode : cards._1().zipIndex() ) {
                final Integer index = cardNode._2();
                final NumberCardNode node = cardNode._1();
                node.setOffset( stack.getLocation( index, node ).toPoint2D() );
                node.setPositionInStack( some( index ) );
                node.setStack( stack );

                addChild( node );
            }
            stack.update();
        }

        addChild( fractionNode );
        fractionNodes.add( fractionNode );

        //Add remaining fraction graphics into the toolbox
        int numRemainingFractionSkeletons = level.targets.length() - 1;

        double toolboxPositionX = 0;
        double toolboxPositionY = 0;
        for ( int i = 0; i < numRemainingFractionSkeletons; i++ ) {
            final FractionNode toolboxFractionGraphic = new FractionNode( this );

            //Put it to the right of the numbers in the toolbox

            toolboxPositionX = toolboxNode.getMaxX() - 10 - toolboxFractionGraphic.getFullBounds().getWidth();
            toolboxPositionY = toolboxNode.getCenterY() - toolboxFractionGraphic.getFullBounds().getHeight() / 2;
            toolboxFractionGraphic.setToolboxPosition( toolboxPositionX, toolboxPositionY );
            toolboxFractionGraphic.setOffset( toolboxPositionX, toolboxPositionY );
            addChild( toolboxFractionGraphic );
            fractionNodes.add( toolboxFractionGraphic );

            toolboxFractionGraphic.moveInFrontOf( toolboxNode );
        }

        fractionNode.setToolboxPosition( toolboxPositionX, toolboxPositionY );
        fractionNode.setOffset( toolboxNode.getCenterX() - fractionNode.getFullBounds().getWidth() / 2, 300 );
        fractionNode.moveInFrontOf( toolboxNode );

        faceNodeDialog = new VBox( new FaceNode( 200 ), new HTMLImageButtonNode( Strings.NEXT, new PhetFont( 20, true ), Color.orange ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    context.goToNextNumberLevel( levelIndex + 1 );
                }
            } );
        }} ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 50 );
        }};

        faceNodeDialog.setTransparency( 0 );
        faceNodeDialog.setVisible( false );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );

        addChild( faceNodeDialog );

        double minScoreCellX = List.iterableList( pairList ).map( new F<ScoreBoxPair, Double>() {
            @Override public Double f( final ScoreBoxPair target ) {
                return target.patternNode.getFullBounds().getMinX();
            }
        } ).minimum( Ord.doubleOrd );
        final PhetPText levelReadoutTitle = new PhetPText( MessageFormat.format( Strings.LEVEL__PATTERN, levelIndex + 1 ), new PhetFont( 32, true ) );
        levelReadoutTitle.setOffset( ( minScoreCellX - AbstractFractionsCanvas.INSET ) / 2 - levelReadoutTitle.getFullWidth() / 2, backButton.getFullBounds().getCenterY() - levelReadoutTitle.getFullHeight() / 2 );
        addChild( levelReadoutTitle );

        final TextButtonNode resetButton = new TextButtonNode( Strings.RESET, AbstractFractionsCanvas.CONTROL_FONT, RefreshButtonNode.BUTTON_COLOR ) {{
            setUserComponent( Components.resetButton );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    reset();
                }
            } );
        }};

        final VoidFunction0 _resampleLevel = new VoidFunction0() {
            public void apply() {
                context.resampleNumberLevel( levelIndex );
            }
        };
        final RefreshButtonNode refreshButton = new RefreshButtonNode( _resampleLevel );
        addChild( new HBox( resetButton, refreshButton ) {{
            setOffset( levelReadoutTitle.getCenterX() - getFullBounds().getWidth() / 2, levelReadoutTitle.getMaxY() + INSET );
        }} );
    }

    //Find the max size of each number node, so we can create a consistent card size
    private Dimension2DDouble getCardSize( final List<List<Integer>> stacks, final F<Integer, Boolean> match ) {
        List<NumberNode> prototypes = stacks.map( new F<List<Integer>, NumberNode>() {
            @Override public NumberNode f( final List<Integer> integers ) {
                return new NumberNode( integers.head() );
            }
        } );
        final List<NumberNode> filtered = prototypes.filter( new F<NumberNode, Boolean>() {
            @Override public Boolean f( final NumberNode numberNode ) {
                return match.f( numberNode.number );
            }
        } );
        if ( filtered.length() == 0 ) { return new Dimension2DDouble( 0, 0 ); }
        double maxNumberNodeWidth = filtered.map( new F<NumberNode, Double>() {
            @Override public Double f( final NumberNode numberNode ) {
                return numberNode.getFullBounds().getWidth();
            }
        } ).maximum( Ord.doubleOrd );
        double maxNumberNodeHeight = filtered.map( new F<NumberNode, Double>() {
            @Override public Double f( final NumberNode numberNode ) {
                return numberNode.getFullBounds().getHeight();
            }
        } ).maximum( Ord.doubleOrd );

        return new Dimension2DDouble( maxNumberNodeWidth + 22, maxNumberNodeHeight );
    }

    private void reset() {
        Property<Boolean> sentOneToPlayArea = new Property<Boolean>( false );
        //Order is important, have to reset score boxes first or the fractions in the score box will be split and cause exceptions
        resetScoreBoxes( sentOneToPlayArea );
        resetFractions( sentOneToPlayArea );
    }

    private void resetFractions( final Property<Boolean> sentOneToPlayArea ) {
        for ( FractionNode fractionNode : fractionNodes ) {
            fractionNode.split();
            if ( !sentOneToPlayArea.get() ) {
                fractionNode.sendFractionSkeletonToCenterOfScreen();
                sentOneToPlayArea.set( true );
            }
            else {
                fractionNode.sendFractionSkeletonToToolbox();
            }
        }
    }

    private void resetScoreBoxes( final Property<Boolean> sentOneToPlayArea ) {
        for ( ScoreBoxPair pair : pairList ) {
            if ( pair.targetCell.isCompleted() ) {
                pair.targetCell.split( sentOneToPlayArea.get() );
                sentOneToPlayArea.set( true );
            }
        }
    }

    public void endDrag( final FractionNode fractionGraphic ) {

        //if fraction graphic overlaps the toolbox when dropped, animate back to the toolbox position (but only if empty)
        if ( toolboxNode.getGlobalFullBounds().intersects( fractionGraphic.getGlobalFullBounds() ) && fractionGraphic.isEmpty() ) {
            fractionGraphic.sendFractionSkeletonToToolbox();
        }
    }

    public void endDrag( final NumberCardNode numberCardNode ) {
        boolean hitFraction = false;
        for ( FractionNode fractionGraphic : fractionNodes ) {
            final PhetPPath topBox = fractionGraphic.topBox;
            final PhetPPath bottomBox = fractionGraphic.bottomBox;
            if ( numberCardNode.getGlobalFullBounds().intersects( topBox.getGlobalFullBounds() ) && topBox.getVisible() && !fractionGraphic.isInToolboxPosition() ) {
                numberDroppedOnFraction( fractionGraphic, numberCardNode, topBox );
                hitFraction = true;
                break;
            }
            if ( numberCardNode.getGlobalFullBounds().intersects( bottomBox.getGlobalFullBounds() ) && bottomBox.getVisible() && !fractionGraphic.isInToolboxPosition() ) {
                numberDroppedOnFraction( fractionGraphic, numberCardNode, bottomBox );
                hitFraction = true;
                break;
            }
        }

        //If it didn't hit a fraction, send back to its starting place--the user is not allowed to have free floating numbers in the play area
        if ( !hitFraction ) {
            numberCardNode.moveToTopOfStack();
        }
    }

    private void numberDroppedOnFraction( final FractionNode fractionGraphic, final NumberCardNode numberCardNode, final PhetPPath box ) {
        centerOnBox( numberCardNode, box );
        box.setVisible( false );
        numberCardNode.setPickable( false );
        numberCardNode.setChildrenPickable( false );
        fractionGraphic.splitButton.setVisible( true );
        fractionGraphic.attachNumber( box, numberCardNode );
        if ( fractionGraphic.isComplete() ) {
            level.createdFractions.set( level.createdFractions.get().snoc( fractionGraphic.getValue() ) );

            FractionCardNode fractionCard = new FractionCardNode( fractionGraphic, pairList, this );
            addChild( fractionCard );
            fractionCard.moveInBackOf( fractionGraphic );
        }
    }

    //TODO: this should account for partially complete fractions too
    boolean allIncompleteFractionsInToolbox() {
        for ( FractionNode fractionGraphic : fractionNodes ) {
            if ( !fractionGraphic.isComplete() && !fractionGraphic.isInToolboxPosition() ) {
                return false;
            }
        }
        return true;
    }

    private void centerOnBox( final NumberCardNode numberNode, final PhetPPath box ) {
        Rectangle2D bounds = box.getGlobalFullBounds();
        bounds = rootNode.globalToLocal( bounds );
        numberNode.centerFullBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
    }

    public void fractionCardNodeDroppedInScoreBox( final FractionCardNode fractionCardNode ) {
        level.incrementFilledTargets();

        //Add a new fraction skeleton when the previous one is completed
        if ( !allTargetsComplete() ) {

            playSoundForOneComplete();

            //If no fraction skeleton in play area, move one there
            if ( allIncompleteFractionsInToolbox() ) {
                FractionNode g = null;
                for ( FractionNode graphic : fractionNodes ) {
                    if ( graphic.isInToolboxPosition() ) {
                        g = graphic;
                    }
                }
                if ( g != null ) {
                    g.animateToPositionScaleRotation( toolboxNode.getCenterX() - fractionCardNode.fractionNode.getFullBounds().getWidth() / 2, 300, 1, 0, 1000 );
                }
            }
        }

        //but if all filled up, then add a "next" button
        else {
            faceNodeDialog.setVisible( true );
            faceNodeDialog.animateToTransparency( 1f, 200 );
            faceNodeDialog.setPickable( true );
            faceNodeDialog.setChildrenPickable( true );
            faceNodeDialog.moveToFront();

            playSoundForAllComplete();
        }
    }

    private boolean allTargetsComplete() { return numCompletedTargets() == pairList.length(); }

    private int numCompletedTargets() {
        return pairList.map( new F<ScoreBoxPair, Boolean>() {
            @Override public Boolean f( final ScoreBoxPair pair ) {
                return pair.targetCell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length();
    }

    public void numberScoreBoxSplit() {
        level.filledTargets.reset();

        //Only subtract from the score if the face dialog was showing.  Otherwise you can get a negative score by removing an item from the target container since this method is called
        //each time.
        faceNodeDialog.animateToTransparency( 0.0f, 200 );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );
    }

    //Fix the z-ordering and location within stacks when something comes back to the toolbox to make sure the stacks look correct.
    public void updateStacks() {
        for ( Stack stack : stackList ) {
            stack.update();
        }
    }

    public Vector2D getLocation( final int stackIndex, final int cardIndex, NumberCardNode cardNode ) {

        final double cardDeltaX = 4;
        final double cardOffset = cardIndex * cardDeltaX;
        final double cardDeltaY = 4;
        return new Vector2D( toolboxNode.getMinX() + leftRightInset + getStackOffset( stackIndex ) + cardOffset,
                             toolboxNode.getCenterY() - cardNode.getFullBounds().getHeight() / 2 + cardIndex * cardDeltaY );
    }

    //How far over the stack should be in the x-coordinate.
    private double getStackOffset( final int stackIndex ) {
        double stackOffset = 0;
        for ( List<Integer> stack : stacks.take( stackIndex ) ) {
            stackOffset += stack.head().toString().length() < 2 ? singleDigitCardSize.width : doubleDigitCardSize.width;
            stackOffset += spacingBetweenNumbers;
        }
        return stackOffset;
    }

}