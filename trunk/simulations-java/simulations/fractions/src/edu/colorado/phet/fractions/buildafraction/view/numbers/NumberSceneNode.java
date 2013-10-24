// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.Equal;
import fj.F;
import fj.P2;
import fj.data.List;
import fj.data.Option;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.SceneContext;
import edu.colorado.phet.fractions.buildafraction.view.SceneNode;
import edu.colorado.phet.fractions.buildafraction.view.Stack;
import edu.colorado.phet.fractions.buildafraction.view.StackContext;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static fj.Ord.doubleOrd;
import static fj.data.Option.some;
import static java.awt.Color.darkGray;

/**
 * Node for the scene when the user is constructing fractions with numbers.
 *
 * @author Sam Reid
 */
public class NumberSceneNode extends SceneNode<NumberSceneCollectionBoxPair> implements NumberDragContext, FractionDraggingContext, StackContext<NumberCardNode> {
    private final ArrayList<FractionNode> fractionNodes = new ArrayList<FractionNode>();
    private final PNode rootNode;
    private final RichPNode toolboxNode;

    //When sending cards back to the toolbox, make sure they have the right location and z-order.
    private final ArrayList<Stack> stackList;
    public final NumberLevel level;
    private final Dimension2DDouble singleDigitCardSize;
    private final Dimension2DDouble doubleDigitCardSize;
    private final List<List<Integer>> stacks;
    private final Vector2D centerOfScreen;

    //Make the spacing big enough that tall stacks don't overlap, but not so large that lots of stacks go off-screen
    private final double spaceBetweenStacks;
    private static final double LEFT_RIGHT_INSET = 20;
    private static final double SPACING_BETWEEN_NUMBERS_AND_FRACTION_SKELETON = 50;
    private final Property<Option<Integer>> draggedCardProperty = new Property<Option<Integer>>( Option.<Integer>none() );
    private double toolboxPositionY;
    private double offsetX;
    private Vector2D initialToolboxPositionForSkeletons;
    public final ArrayList<VoidFunction1<ShapeSceneNode.DropResult>> dropListeners = new ArrayList<VoidFunction1<ShapeSceneNode.DropResult>>();

    public NumberSceneNode( final int levelIndex, final PNode rootNode, final BuildAFractionModel model, final SceneContext context, BooleanProperty soundEnabled, boolean fractionLab ) {
        super( levelIndex, soundEnabled, context, fractionLab );

        double insetY = 10;
        final ActionListener goToNextLevel = new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                context.goToNumberLevel( levelIndex + 1 );
            }
        };
        final VoidFunction0 _resampleLevel = new VoidFunction0() {
            public void apply() {
                context.resampleNumberLevel( levelIndex );
            }
        };
        level = model.getNumberLevel( levelIndex );
        this.rootNode = rootNode;

        //Create the scoring cells with target patterns
        initCollectionBoxes( insetY, getCollectionBoxPairs( model ), fractionLab );

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

        //Macintosh fonts are wider by default, so shrink the spacing so there will be enough room on fraction lab
        LinearFunction linearFunction = new LinearFunction( 58, 68, 38, 28 );
        final double fractionLabSpacing = ( linearFunction.evaluate( singleDigitCardSize.width ) );
        spaceBetweenStacks = fractionLab ? fractionLabSpacing : 28;

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

        final FractionNode fractionNode = new FractionNode( this, level.hasMixedNumbers() );
        fractionNode.setScale( 1.0 );

        double extentX = LEFT_RIGHT_INSET * 2 + getStackOffset( stacks.length() ) - singleDigitCardSize.width + SPACING_BETWEEN_NUMBERS_AND_FRACTION_SKELETON + fractionNode.getFullBounds().getWidth() - 30
                         - ( level.hasMixedNumbers() ? 30 : 0 );
        if ( fractionLab ) { extentX = STAGE_SIZE.width - INSET * 2; }

        //Create the toolbox node
        final double finalExtentX = extentX;
        toolboxNode = new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, finalExtentX, 130, 30, 30 ), Color.white, BuildAFractionCanvas.CONTROL_PANEL_STROKE, darkGray );
            addChild( border );
            final double offsetX = Math.max( ( AbstractFractionsCanvas.STAGE_SIZE.width - 150 ) / 2 - this.getFullWidth() / 2 - 29
                                             - ( level.hasValuesGreaterThanOne() ? 48 : 0 ), INSET );

            setOffset( offsetX, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );

        stackList = new ArrayList<Stack>();
        for ( P2<List<NumberCardNode>, Integer> cards : cardNodes.zipIndex() ) {
            final Integer stackIndex = cards._2();
            final Stack<NumberCardNode> stack = new Stack<NumberCardNode>( cards._1(), stackIndex, this );
            stackList.add( stack );
            for ( final P2<NumberCardNode, Integer> cardNode : cards._1().zipIndex() ) {
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

        double toolboxPositionX;
        double toolboxPositionY = 0;
        for ( int i = 0; i < numRemainingFractionSkeletons; i++ ) {
            CreateNonMixedFractionGraphicFractionLab c = new CreateNonMixedFractionGraphicFractionLab( fractionLab ).invoke();
            toolboxPositionX = c.getToolboxPositionX();
            toolboxPositionY = c.getToolboxPositionY();
            this.initialToolboxPositionForSkeletons = Vector2D.v( toolboxPositionX, toolboxPositionY );
        }

        //For free play, also create a mixed number graphic
        if ( fractionLab ) {
            this.offsetX = toolboxNode.getMaxX() - 200;
            addMixedFractionGraphicFactionLab( toolboxPositionY, offsetX );
            this.toolboxPositionY = toolboxPositionY;
        }

        init( levelIndex, model, goToNextLevel, _resampleLevel, fractionLab );

        fractionNode.setToolboxPosition( initialToolboxPositionForSkeletons.x, initialToolboxPositionForSkeletons.y );
        centerOfScreen = new Vector2D( fractionLab ? 300 : title.getFullBounds().getCenterX() - fractionNode.getFullWidth() / 2 + 28, 350 - fractionNode.getFullHeight() / 2 - ( fractionLab ? 0 : 30 ) );
        fractionNode.setOffset( centerOfScreen.toPoint2D() );
        fractionNode.moveInFrontOf( toolboxNode );
    }

    //For the Fraction Lab tab, add a Mixed Fraction graphic to the toolbox
    private void addMixedFractionGraphicFactionLab( final double toolboxPositionY, final double offsetX ) {
        final FractionNode toolboxFractionGraphic = new FractionNode( this, true );

        //Put it to the right of the numbers in the toolbox

        final double toolboxPositionX = offsetX + 67;
        toolboxFractionGraphic.setToolboxPosition( toolboxPositionX, toolboxPositionY );
        toolboxFractionGraphic.setOffset( toolboxPositionX, toolboxPositionY );
        addChild( toolboxFractionGraphic );
        fractionNodes.add( toolboxFractionGraphic );

        toolboxFractionGraphic.moveInFrontOf( toolboxNode );
    }

    //Create the NumberSceneCollectionBoxPair for the given level.
    private ArrayList<NumberSceneCollectionBoxPair> getCollectionBoxPairs( final BuildAFractionModel model ) {
        ArrayList<NumberSceneCollectionBoxPair> _pairs = new ArrayList<NumberSceneCollectionBoxPair>();
        for ( int i = 0; i < level.targets.length(); i++ ) {
            NumberTarget target = level.targets.index( i );

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


            //On mixed number nodes, make the target values smaller so there is enough room
            if ( model.isMixedNumbers() ) { patternNode.scale( 0.75 ); }

            //Add to the set of pairs
            _pairs.add( new NumberSceneCollectionBoxPair( new NumberCollectionBoxNode( target.mixedFraction, this, model.collectedMatch.or( level.matchExists ) ), new ZeroOffsetNode( patternNode ) ) );
        }
        return _pairs;
    }

    //Find the max size of each number node, so we can create a consistent card size
    private static Dimension2DDouble getCardSize( final List<List<Integer>> stacks, final F<Integer, Boolean> match ) {
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
        } ).maximum( doubleOrd );
        double maxNumberNodeHeight = filtered.map( new F<NumberNode, Double>() {
            @Override public Double f( final NumberNode numberNode ) {
                return numberNode.getFullBounds().getHeight();
            }
        } ).maximum( doubleOrd );

        return new Dimension2DDouble( maxNumberNodeWidth + 22, maxNumberNodeHeight );
    }

    //Resets the scene
    protected void reset() {

        //Order is important, have to reset score boxes first or the fractions in the score box will be split and cause exceptions
        resetCollectionBoxes();
        resetFractions();

        //After all cards have gone back to the toolbox, bring one to the center of the screen
        //This introduces a timing dependency and could fail on machines that aren't able to finish the animation before the timer expires
//        new Timer( 1200, new ActionListener() {
//            public void actionPerformed( final ActionEvent e ) {
//                fractionNodes.get( 0 ).animateToCenterOfScreen();
//            }
//        } ) {{
//            setRepeats( false );
//            setInitialDelay( 1200 );
//        }}.start();
    }

    //Never called, can be no-op
    @Override public void setToolboxEnabled( final boolean enabled ) {
    }

    private void resetFractions() {
        boolean start = true;
        for ( FractionNode fractionNode : fractionNodes ) {
            fractionNode.reset();
            if ( start ) {
                fractionNode.animateToCenterOfScreen();
            }
            else {
                fractionNode.animateToToolbox();
            }
            start = false;
        }
    }

    private void resetCollectionBoxes() {
        for ( NumberSceneCollectionBoxPair pair : getCollectionBoxPairs() ) {
            if ( pair.collectionBoxNode.isCompleted() ) {
                pair.collectionBoxNode.undo( false );
            }
        }
    }

    //When the user drags an un-filled fraction node.  If you are looking for the code that is called when a fraction card is dropped (say in a target) that is called fractionCardNodeDroppedInCollectionBox()
    public void endDrag( final FractionNode fractionNode ) {

        //if fraction graphic overlaps the toolbox when dropped, animate back to the toolbox position (but only if empty)
        if ( toolboxNode.getGlobalFullBounds().intersects( fractionNode.getGlobalFullBounds() ) && fractionNode.isEmpty() ) {
            fractionNode.animateToToolbox();
        }

        if ( fractionNode.getGlobalFullBounds().getMaxX() > minimumCollectionBoxX() && !fractionLab ) {
            fractionNode.animateNearCenterOfScreen();
        }
    }

    //Find the leftmost side of the leftmost collection box, for layout and sending back objects to the play area if dropped across the line.
    public double minimumCollectionBoxX() {
        return getCollectionBoxPairs().map( new F<NumberSceneCollectionBoxPair, Double>() {
            @Override public Double f( final NumberSceneCollectionBoxPair n ) {
                return n.collectionBoxNode.getGlobalFullBounds().getMinX();
            }
        } ).minimum( doubleOrd );
    }

    //Called when a NumberCardNode is dropped.  Checks to see if it should be returned to toolbox or added to a fraction.
    public void endDrag( final NumberCardNode numberCardNode ) {
        boolean hitFraction = false;
        for ( FractionNode f : fractionNodes ) {
            final PhetPPath topBox = f.numerator.box.shape;
            final PhetPPath bottomBox = f.denominator.box.shape;
            final PhetPPath wholeBox = f.whole.box.shape;
            if ( f.numerator.isEnabled() && numberCardNode.getGlobalFullBounds().intersects( topBox.getGlobalFullBounds() ) && topBox.getVisible() && !f.isInToolboxPosition() ) {
                numberDroppedOnFraction( f, numberCardNode, topBox );
                hitFraction = true;
                break;
            }
            if ( f.denominator.isEnabled() && numberCardNode.getGlobalFullBounds().intersects( bottomBox.getGlobalFullBounds() ) && bottomBox.getVisible() && !f.isInToolboxPosition() ) {
                numberDroppedOnFraction( f, numberCardNode, bottomBox );
                hitFraction = true;
                break;
            }
            if ( f.whole.isEnabled() && numberCardNode.getGlobalFullBounds().intersects( wholeBox.getGlobalFullBounds() ) && wholeBox.getVisible() && !f.isInToolboxPosition() ) {
                numberDroppedOnFraction( f, numberCardNode, wholeBox );
                hitFraction = true;
                break;
            }
        }

        //If it didn't hit a fraction, send back to its starting place--the user is not allowed to have free floating numbers in the play area
        if ( !hitFraction ) {
            numberCardNode.animateToTopOfStack( isFractionLab() );
        }

        draggedCardProperty.set( Option.<Integer>none() );
    }

    //Called when the user starts dragging a number card node, which adds another copy to the toolbox to create a seemingly endless supply.
    public void startDrag( final NumberCardNode node ) {
        draggedCardProperty.set( some( node.number ) );
        if ( fractionLab && !node.animating.get() ) {
            NumberCardNode copy = node.copy();
            addChild( copy );
            copy.setGlobalTranslation( node.getGlobalTranslation() );
            copy.setGlobalScale( node.getGlobalScale() );
            copy.moveToFront();
        }
        fireInteractionEvent();
    }

    //Called when a number node is dropped into a fraction, this method turning it into a card if it is complete
    private void numberDroppedOnFraction( final FractionNode fractionGraphic, final NumberCardNode numberCardNode, final PhetPPath box ) {
        centerOnBox( numberCardNode, box );
        box.setVisible( false );
        numberCardNode.setPickable( false );
        numberCardNode.setChildrenPickable( false );
        fractionGraphic.undoButton.setVisible( true );
        fractionGraphic.attachNumber( box, numberCardNode );
        if ( fractionGraphic.isComplete() ) {
            level.createdFractions.set( level.createdFractions.get().snoc( fractionGraphic.getValue() ) );

            FractionCardNode fractionCard = new FractionCardNode( fractionGraphic, getCollectionBoxPairs(), this );
            addChild( fractionCard );
            fractionCard.moveInBackOf( fractionGraphic );
        }
    }

    //Determine whether the play area is empty of fraction skeletons, so that one can be animated there if necessary.
    boolean allIncompleteFractionsInToolbox() {
        for ( FractionNode fractionGraphic : fractionNodes ) {
            if ( !fractionGraphic.isComplete() && !fractionGraphic.isInToolboxPosition() ) {
                return false;
            }
        }
        return true;
    }

    //Center the NumberCardNode on the specified box when it "snaps in" to a fraction.
    private void centerOnBox( final NumberCardNode numberNode, final PhetPPath box ) {
        Rectangle2D bounds = box.getGlobalFullBounds();
        bounds = rootNode.globalToLocal( bounds );
        numberNode.centerFullBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
    }

    //Called when a challenge is completed by dropping a correct fraction into a collection box.
    public void fractionCardNodeDroppedInCollectionBox( NumberCollectionBoxNode scoreCell ) {
        for ( VoidFunction1<ShapeSceneNode.DropResult> listener : dropListeners ) {
            listener.apply( new ShapeSceneNode.DropResult( true, scoreCell.mixedFraction.toFraction(), scoreCell.mixedFraction.toFraction(), -1, getTargetIndex( scoreCell ) ) );
        }
        level.filledTargets.increment();

        //Add a new fraction skeleton when the previous one is completed
        if ( !allTargetsComplete() ) {

            notifyOneCompleted();

            //If no fraction skeleton in play area, move one there
            if ( allIncompleteFractionsInToolbox() ) {
                FractionNode node = null;
                for ( FractionNode graphic : fractionNodes ) {
                    if ( graphic.isInToolboxPosition() ) {
                        node = graphic;
                    }
                }
                if ( node != null ) {
                    node.animateToCenterOfScreen();
                }
            }
        }

        //but if all filled up, then add a "next" button
        else {
            faceNodeDialog.setVisible( true );
            faceNodeDialog.animateToTransparency( 1f, BuildAFractionModule.ANIMATION_TIME );
            faceNodeDialog.setPickable( true );
            faceNodeDialog.setChildrenPickable( true );
            faceNodeDialog.moveToFront();

            notifyAllCompleted();
        }
    }

    private int getTargetIndex( NumberCollectionBoxNode scoreCell ) {List<NumberCollectionBoxNode> list = this.getCollectionBoxPairs().map( new F<NumberSceneCollectionBoxPair, NumberCollectionBoxNode>() {
        @Override public NumberCollectionBoxNode f( NumberSceneCollectionBoxPair numberSceneCollectionBoxPair ) {
            return numberSceneCollectionBoxPair.collectionBoxNode;
        }
    } );
        return list.elementIndex( Equal.<NumberCollectionBoxNode>anyEqual(), scoreCell ).some();
    }

    private boolean allTargetsComplete() {
        return numCompletedTargets() == getCollectionBoxPairs().length();
    }

    private int numCompletedTargets() {
        return getCollectionBoxPairs().map( new F<NumberSceneCollectionBoxPair, Boolean>() {
            @Override public Boolean f( final NumberSceneCollectionBoxPair pair ) {
                return pair.collectionBoxNode.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length();
    }

    //Called when the user presses "undo" on a collection box.
    public void numberCollectionBoxUndone() {
        level.filledTargets.decrement();

        //Only subtract from the score if the face dialog was showing.  Otherwise you can get a negative score by removing an item from the target container since this method is called
        //each time.
        faceNodeDialog.animateToTransparency( 0.0f, BuildAFractionModule.ANIMATION_TIME );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );
    }

    //Fix the z-ordering and location within stacks when something comes back to the toolbox to make sure the stacks look correct.
    public void updateStacks() {
        for ( Stack stack : stackList ) {
            stack.update();
        }
    }

    public Vector2D getCenterOfScreen() { return centerOfScreen; }

    public ObservableProperty<Option<Integer>> getDraggedCardProperty() { return draggedCardProperty; }

    //Called when the user starts dragging a fraction node, used to create another fraction node behind it on the toolbox, so there will be
    //a seemingly endless supply of fraction skeletons.
    public void startDrag( final FractionNode fractionNode ) {
        if ( fractionLab ) {
            if ( fractionNode.mixedNumber && countMixedFractionNodes() < 4 ) { addMixedFractionGraphicFactionLab( toolboxPositionY, offsetX ); }
            else if ( countNonMixedFractionNodes() < 4 ) {new CreateNonMixedFractionGraphicFractionLab( fractionLab ).invoke();}
        }
        fireInteractionEvent();
    }

    //Count the number of fraction nodes that do not contain a mixed number.
    private int countNonMixedFractionNodes() {
        int count = 0;
        for ( Object _child : getChildrenReference() ) {
            PNode child = (PNode) _child;
            if ( child instanceof FractionNode ) {
                FractionNode f = (FractionNode) child;
                if ( !f.mixedNumber ) { count++; }
            }
            else if ( child instanceof FractionCardNode ) {
                FractionCardNode fcn = (FractionCardNode) child;
                if ( !fcn.isMixedNumber() ) { count++; }
            }
        }
        return count;
    }

    //Count the number of fraction nodes that do contain a mixed number.
    private int countMixedFractionNodes() {
        int count = 0;
        for ( Object _child : getChildrenReference() ) {
            PNode child = (PNode) _child;
            if ( child instanceof FractionNode ) {
                FractionNode f = (FractionNode) child;
                if ( f.mixedNumber ) { count++; }
            }
            else if ( child instanceof FractionCardNode ) {
                FractionCardNode fcn = (FractionCardNode) child;
                if ( fcn.isMixedNumber() ) { count++; }
            }
        }
        return count;
    }

    public boolean isFractionLab() { return fractionLab; }

    //Determine whether the FractionNode has been correctly collected in a collection box.
    public boolean isInCollectionBox( final FractionNode fractionNode ) {
        return getCollectionBoxPairs().exists( new F<NumberSceneCollectionBoxPair, Boolean>() {
            @Override public Boolean f( final NumberSceneCollectionBoxPair p ) {
                return p.getCollectionBoxNode().isCompleted() && p.getCollectionBoxNode().getCompletedFraction() == fractionNode;
            }
        } );
    }

    //Get the location where a card should be placed given its stack and location in the stack.
    public Vector2D getLocation( final int stackIndex, final int cardIndex, NumberCardNode cardNode ) {

        final double cardDeltaX = 4;
        final double cardOffset = cardIndex * cardDeltaX;
        final double cardDeltaY = 4;
        return new Vector2D( toolboxNode.getMinX() + LEFT_RIGHT_INSET + getStackOffset( stackIndex ) + cardOffset,
                             toolboxNode.getCenterY() - cardNode.getFullBounds().getHeight() / 2 + cardIndex * cardDeltaY );
    }

    public void movedNonStackCardsToFront() {
    }

    //How far over the stack should be in the x-coordinate.
    private double getStackOffset( final int stackIndex ) {
        double stackOffset = 0;
        for ( List<Integer> stack : stacks.take( stackIndex ) ) {
            stackOffset += stack.head().toString().length() < 2 ? singleDigitCardSize.width : doubleDigitCardSize.width;
            stackOffset += spaceBetweenStacks;
        }
        return stackOffset + ( isFractionLab() ? 72 : 0 );
    }

    public void fractionCardNodeDroppedInWrongCollectionBox( Fraction value, NumberCollectionBoxNode scoreCell ) {
        for ( VoidFunction1<ShapeSceneNode.DropResult> listener : dropListeners ) {
            listener.apply( new ShapeSceneNode.DropResult( false, value, scoreCell.mixedFraction.toFraction(), -1, getTargetIndex( scoreCell ) ) );
        }
    }

    //Created with IDEA's "create value method", used to create and add regular (non-mixed) fraction nodes to the toolbox.
    private class CreateNonMixedFractionGraphicFractionLab {
        private final boolean fractionLab;
        private double toolboxPositionX;
        private double toolboxPositionY;

        public CreateNonMixedFractionGraphicFractionLab( final boolean fractionLab ) {this.fractionLab = fractionLab;}

        public double getToolboxPositionX() {
            return toolboxPositionX;
        }

        public double getToolboxPositionY() {
            return toolboxPositionY;
        }

        public CreateNonMixedFractionGraphicFractionLab invoke() {
            final FractionNode toolboxFractionGraphic = new FractionNode( NumberSceneNode.this, level.hasMixedNumbers() );

            //Put it to the right of the numbers in the toolbox

            toolboxPositionX = fractionLab ? toolboxNode.getMinX() + 23 :
                               toolboxNode.getMaxX() + 3 - toolboxFractionGraphic.getFullBounds().getWidth();
            toolboxPositionY = toolboxNode.getCenterY() - toolboxFractionGraphic.getFullBounds().getHeight() / 2;
            toolboxFractionGraphic.setToolboxPosition( toolboxPositionX, toolboxPositionY );
            toolboxFractionGraphic.setOffset( toolboxPositionX, toolboxPositionY );
            addChild( toolboxFractionGraphic );
            fractionNodes.add( toolboxFractionGraphic );

            toolboxFractionGraphic.moveInFrontOf( toolboxNode );
            return this;
        }
    }
}