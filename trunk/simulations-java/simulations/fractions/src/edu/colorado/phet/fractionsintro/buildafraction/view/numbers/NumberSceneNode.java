package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.NumberLevel;
import edu.colorado.phet.fractionsintro.buildafraction.model.NumberTarget;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND;
import static edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas.controlPanelStroke;
import static java.awt.Color.darkGray;

/**
 * Node for the scene when the user is constructing fractions with numbers.
 *
 * @author Sam Reid
 */
public class NumberSceneNode extends PNode implements NumberDragContext, FractionDraggingContext {
    public final ArrayList<FractionNode> fractionGraphics = new ArrayList<FractionNode>();
    public final PNode rootNode;
    private final BuildAFractionModel model;
    public final PDimension STAGE_SIZE;
    public final NumberSceneContext context;
    public final List<Pair> pairList;
    public RichPNode toolboxNode;
    public final int level;
    private final VBox faceNodeDialog;

    public void endDrag( final FractionNode fractionGraphic, final PInputEvent event ) {

        //if fraction graphic overlaps the toolbox when dropped, animate back to the toolbox position.
        if ( toolboxNode.getGlobalFullBounds().intersects( fractionGraphic.getGlobalFullBounds() ) ) {
            fractionGraphic.animateToPositionScaleRotation( 300, 300, 1, 0, 1000 );
        }
    }

    public NumberSceneNode( int level, final PNode rootNode, final BuildAFractionModel model, final PDimension STAGE_SIZE, final NumberSceneContext context ) {
        this.rootNode = rootNode;
        this.level = level;
        this.model = model;
        this.STAGE_SIZE = STAGE_SIZE;
        this.context = context;
        final PhetPText title = new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT );

        //Create the scoring cells with target patterns
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for ( int i = 0; i < model.getNumberLevel( level ).targets.length(); i++ ) {
            NumberTarget target = model.getNumberLevel( level ).getTarget( i );

            ArrayList<PatternNode> nodes = new ArrayList<PatternNode>();
            for ( int k = 0; k < target.filledPattern.length(); k++ ) {
                nodes.add( new PatternNode( target.filledPattern.index( k ), target.color ) );
            }
            HBox patternNode = new HBox( nodes.toArray( new PNode[nodes.size()] ) );
            pairs.add( new Pair( new ScoreBoxNode( target.fraction.numerator, target.fraction.denominator, model.getCreatedFractions( level ),
                                                   rootNode, model, this, model.getNumberLevel( level ).flashTargetCellOnMatch ), new ZeroOffsetNode( patternNode ) ) );
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
        double offsetY = title.getFullHeight() + 5;
        double insetY = 5;
        addChild( title );
        for ( Pair pair : pairs ) {

            pair.targetCell.setOffset( offsetX, offsetY );
            pair.patternNode.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.targetCell );
            addChild( pair.patternNode );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        //Center title above the "my fractions" scoring cell boxes
        title.setOffset( pairs.get( 0 ).getTargetCell().getFullBounds().getCenterX() - title.getFullWidth() / 2, pairs.get( 0 ).getTargetCell().getFullBounds().getY() - title.getFullHeight() );

        //Add a piece container toolbox the user can use to get containers
        //Put numbers on cards so you can see how many there are in a stack
        //I suspect it will look awkward unless all cards have the same dimensions
        NumberLevel myLevel = model.getNumberLevel( level );
        List<List<Integer>> stacks = myLevel.numbers.group( Equal.intEqual );

        //Find the max size of each number node, so we can create a consistent card size
        List<NumberNode> prototypes = stacks.map( new F<List<Integer>, NumberNode>() {
            @Override public NumberNode f( final List<Integer> integers ) {
                return new NumberNode( integers.head() );
            }
        } );
        double maxNumberNodeWidth = prototypes.map( new F<NumberNode, Double>() {
            @Override public Double f( final NumberNode numberNode ) {
                return numberNode.getFullBounds().getWidth();
            }
        } ).maximum( Ord.doubleOrd );
        double maxNumberNodeHeight = prototypes.map( new F<NumberNode, Double>() {
            @Override public Double f( final NumberNode numberNode ) {
                return numberNode.getFullBounds().getHeight();
            }
        } ).maximum( Ord.doubleOrd );

        final Dimension2DDouble cardSize = new Dimension2DDouble( maxNumberNodeWidth + 22, maxNumberNodeHeight );

        //Create a stack of cards for each unique number
        List<List<NumberCardNode>> cardNodes = stacks.map( new F<List<Integer>, List<NumberCardNode>>() {
            @Override public List<NumberCardNode> f( final List<Integer> integers ) {
                return integers.map( new F<Integer, NumberCardNode>() {
                    @Override public NumberCardNode f( final Integer integer ) {
                        return new NumberCardNode( cardSize, integer, NumberSceneNode.this );
                    }
                } );
            }
        } );

        final FractionNode fractionGraphic = new FractionNode( this );
        double cardWidth = cardSize.width;

        double spacingBetweenNumbers = 20;
        double leftRightInset = 20;
        double spacingBetweenNumbersAndFractionSkeleton = 50;
        final double extentX = cardWidth * stacks.length() + spacingBetweenNumbers * ( stacks.length() - 1 ) + leftRightInset * 2 + spacingBetweenNumbersAndFractionSkeleton + fractionGraphic.getFullBounds().getWidth();

        //Create the toolbox node
        toolboxNode = new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, extentX, 160, 30, 30 ), CONTROL_PANEL_BACKGROUND, controlPanelStroke, darkGray );
            addChild( border );
            setOffset( ( AbstractFractionsCanvas.STAGE_SIZE.width - 150 ) / 2 - this.getFullWidth() / 2, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );

        int groupIndex = 0;
        double cardDeltaX = 4;
        double cardDeltaY = 4;
        for ( List<NumberCardNode> cardNodeStack : cardNodes ) {

            int numInStack = cardNodeStack.length();
            int indexInStack = 0;
            for ( NumberCardNode cardNode : cardNodeStack ) {
                int reverseIndex = numInStack - 1 - indexInStack;
                cardNode.setInitialPosition( toolboxNode.getMinX() + leftRightInset + ( cardWidth + spacingBetweenNumbers ) * groupIndex + reverseIndex * cardDeltaX,
                                             toolboxNode.getCenterY() - cardNode.getFullBounds().getHeight() / 2 + reverseIndex * cardDeltaY );
                addChild( cardNode );
                indexInStack++;
            }
            groupIndex++;
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

        faceNodeDialog = new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", Color.orange ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    context.nextNumberLevel();
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
            numberCardNode.animateHome();
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
            model.addCreatedValue( fractionGraphic.getValue() );

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
            model.numberScore.add( 1 );
        }
    }

    private boolean allTargetsComplete() {
        return pairList.map( new F<Pair, Boolean>() {
            @Override public Boolean f( final Pair pair ) {
                return pair.targetCell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == pairList.length();
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
}