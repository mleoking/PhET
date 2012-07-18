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
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
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
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.SceneNode;
import edu.colorado.phet.fractions.buildafraction.view.Stack;
import edu.colorado.phet.fractions.buildafraction.view.StackContext;
import edu.colorado.phet.fractions.buildafraction.view.pictures.RefreshButtonNode;
import edu.colorado.phet.fractions.buildafraction.view.pictures.SceneContext;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D;
import edu.colorado.phet.fractions.common.view.BackButton;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static fj.data.Option.some;
import static java.awt.Color.darkGray;

/**
 * Node for the scene when the user is constructing fractions with numbers.
 *
 * @author Sam Reid
 */
public class NumberSceneNode extends SceneNode implements NumberDragContext, FractionDraggingContext, StackContext<NumberCardNode> {
    public final ArrayList<FractionNode> fractionGraphics = new ArrayList<FractionNode>();
    public final PNode rootNode;
    private final BuildAFractionModel model;
    public final PDimension STAGE_SIZE;
    public final SceneContext context;
    public final List<Pair> pairList;
    public RichPNode toolboxNode;
    public final int levelIndex;
    private final VBox faceNodeDialog;
    private VoidFunction0 _resampleLevel = new VoidFunction0() {
        public void apply() {
            context.resampleNumberLevel( levelIndex );
        }
    };

    //When sending cards back to the toolbox, make sure they have the right location and z-order.
    private final ArrayList<Stack> stackList;

    private final double cardDeltaX = 4;
    private final double cardDeltaY = 4;
    double spacingBetweenNumbers = 20;
    double leftRightInset = 20;
    double spacingBetweenNumbersAndFractionSkeleton = 50;
    public final NumberLevel myLevel;
    private final Dimension2DDouble singleDigitCardSize;
    private final Dimension2DDouble doubleDigitCardSize;
    private final List<List<Integer>> stacks;

    public NumberSceneNode( final int levelIndex, final PNode rootNode, final BuildAFractionModel model, final PDimension STAGE_SIZE, final SceneContext context, BooleanProperty soundEnabled ) {
        super( soundEnabled );
        myLevel = model.getNumberLevel( levelIndex );
        this.rootNode = rootNode;
        this.levelIndex = levelIndex;
        this.model = model;
        this.STAGE_SIZE = STAGE_SIZE;
        this.context = context;

        final BackButton backButton = new BackButton( new VoidFunction0() {
            public void apply() {
                context.goToLevelSelectionScreen();
            }
        } ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
        addChild( backButton );

        //Create the scoring cells with target patterns
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for ( int i = 0; i < model.getNumberLevel( levelIndex ).targets.length(); i++ ) {
            NumberTarget target = model.getNumberLevel( levelIndex ).getTarget( i );

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
            pairs.add( new Pair( new NumberScoreBoxNode( target.fraction.numerator, target.fraction.denominator, myLevel.createdFractions,
                                                         rootNode, model, this, model.getNumberLevel( levelIndex ).flashTargetCellOnMatch ), new ZeroOffsetNode( patternNode ) ) );
        }
        pairList = List.iterableList( pairs );

        List<PNode> patterns = pairList.map( new F<Pair, PNode>() {
            @Override public PNode f( final Pair pair ) {
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
        for ( Pair pair : pairs ) {

            pair.targetCell.setOffset( offsetX, offsetY );
            pair.patternNode.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.targetCell );
            addChild( pair.patternNode );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        //Add a piece container toolbox the user can use to get containers
        //Put numbers on cards so you can see how many there are in a stack
        //I suspect it will look awkward unless all cards have the same dimensions

        stacks = myLevel.numbers.group( Equal.intEqual );

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

        final FractionNode fractionGraphic = new FractionNode( this );
        fractionGraphic.setScale( 1.0 );

        final double extentX = leftRightInset * 2 + getStackOffset( stacks.length() ) - singleDigitCardSize.width + spacingBetweenNumbersAndFractionSkeleton + fractionGraphic.getFullBounds().getWidth();

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

        addChild( fractionGraphic );
        fractionGraphics.add( fractionGraphic );

        //Add remaining fraction graphics into the toolbox
        int numRemainingFractionSkeletons = myLevel.targets.length() - 1;

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
            fractionGraphics.add( toolboxFractionGraphic );

            toolboxFractionGraphic.moveInFrontOf( toolboxNode );
        }

        fractionGraphic.setToolboxPosition( toolboxPositionX, toolboxPositionY );
        fractionGraphic.setOffset( toolboxNode.getCenterX() - fractionGraphic.getFullBounds().getWidth() / 2, 300 );
        fractionGraphic.moveInFrontOf( toolboxNode );

        faceNodeDialog = new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", new PhetFont( 20, true ), Color.orange ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    context.goToNextNumberLevel( levelIndex + 1 );
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

        double minScoreCellX = List.iterableList( pairList ).map( new F<Pair, Double>() {
            @Override public Double f( final Pair target ) {
                return target.patternNode.getFullBounds().getMinX();
            }
        } ).minimum( Ord.doubleOrd );
        final PhetPText levelReadoutTitle = new PhetPText( "Level " + ( levelIndex + 1 ), new PhetFont( 32, true ) );
        levelReadoutTitle.setOffset( ( minScoreCellX - AbstractFractionsCanvas.INSET ) / 2 - levelReadoutTitle.getFullWidth() / 2, backButton.getFullBounds().getCenterY() - levelReadoutTitle.getFullHeight() / 2 );
        addChild( levelReadoutTitle );

        final TextButtonNode resetButton = new TextButtonNode( "Reset", AbstractFractionsCanvas.CONTROL_FONT, RefreshButtonNode.BUTTON_COLOR ) {{
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
        for ( Pair pair : pairList ) {
            pair.targetCell.split();
        }
        for ( FractionNode fractionGraphic : fractionGraphics ) {
            fractionGraphic.split();
        }
    }

    public void endDrag( final FractionNode fractionGraphic, final PInputEvent event ) {

        //if fraction graphic overlaps the toolbox when dropped, animate back to the toolbox position.
        if ( toolboxNode.getGlobalFullBounds().intersects( fractionGraphic.getGlobalFullBounds() ) ) {
            fractionGraphic.animateToPositionScaleRotation( 300, 300, 1, 0, 1000 );
        }
    }

    public void endDrag( final NumberCardNode numberCardNode, final PInputEvent event ) {
        boolean hitFraction = false;
        for ( FractionNode fractionGraphic : fractionGraphics ) {
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
            myLevel.createdFractions.set( myLevel.createdFractions.get().snoc( fractionGraphic.getValue() ) );

            FractionCardNode fractionCard = new FractionCardNode( fractionGraphic, rootNode, pairList, model, this );
            addChild( fractionCard );
            fractionCard.moveInBackOf( fractionGraphic );
        }
    }

    //TODO: this should account for partially complete fractions too
    public boolean allIncompleteFractionsInToolbox() {
        for ( FractionNode fractionGraphic : fractionGraphics ) {
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

    public void fractionCardNodeDragEnded( final FractionCardNode fractionCardNode, final PInputEvent event ) {
        //Add a new fraction skeleton when the previous one is completed
        if ( !allTargetsComplete() ) {

            playSoundForOneComplete();

            //If no fraction skeleton in play area, move one there
            if ( allIncompleteFractionsInToolbox() ) {
                FractionNode g = null;
                for ( FractionNode graphic : fractionGraphics ) {
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
            model.numberScore.add( 1 );
        }
    }

    private boolean allTargetsComplete() { return numCompletedTargets() == pairList.length(); }

    private int numCompletedTargets() {
        return pairList.map( new F<Pair, Boolean>() {
            @Override public Boolean f( final Pair pair ) {
                return pair.targetCell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length();
    }

    public void hideFace() {

        //Only subtract from the score if the face dialog was showing.  Otherwise you can get a negative score by removing an item from the target container since this method is called
        //each time.
        if ( faceNodeDialog.getPickable() ) {
            model.numberScore.subtract( 1 );
        }
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

        final double cardOffset = cardIndex * cardDeltaX;
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