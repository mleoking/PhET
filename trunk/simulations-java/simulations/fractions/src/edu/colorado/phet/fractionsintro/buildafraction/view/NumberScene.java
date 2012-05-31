package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.Ord;
import fj.data.List;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableFraction;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumber;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumberID;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableObject;
import edu.colorado.phet.fractionsintro.buildafraction.model.FractionID;
import edu.colorado.phet.fractionsintro.buildafraction.model.Mode;
import edu.colorado.phet.fractionsintro.common.util.DefaultP2;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;
import static edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas.controlPanelStroke;
import static fj.data.List.range;
import static java.awt.BasicStroke.CAP_ROUND;

/**
 * @author Sam Reid
 */
public class NumberScene extends PNode {

    public final RichPNode numbersContainerLayer;
    private final BuildAFractionModel model;

    public NumberScene( final BuildAFractionModel model, final SettableProperty<Mode> mode, final BuildAFractionCanvas canvas ) {
        this.model = model;

        //The draggable containers
        numbersContainerLayer = new RichPNode();

        final PNode radioButtonControlPanel = BuildAFractionCanvas.createModeControlPanel( mode );
        addChild( radioButtonControlPanel );

        List<PNode> scoreBoxes = range( 0, 3 ).map( new F<Integer, PNode>() {
            @Override public PNode f( final Integer integer ) {

                //If these representationBox are all the same size, then 2-column layout will work properly
                final int numerator = integer + 1;
                PNode representationBox = new PatternNode( FilledPattern.sequentialFill( Pattern.sixFlower( 18 ), numerator ), Color.red );
                return new HBox( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 140, 150, 30, 30 ), controlPanelStroke, Color.darkGray ) {{

                    //Light up if the user matched
                    model.addObserver( new ChangeObserver<BuildAFractionState>() {
                        public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
                            if ( newValue.containsMatch( numerator, 6 ) != oldValue.containsMatch( numerator, 6 ) ) {
                                setStrokePaint( newValue.containsMatch( numerator, 6 ) ? Color.red : Color.darkGray );
                                setStroke( controlPanelStroke );
                            }
                            if ( newValue.containsMatch( numerator, 6 ) ) {

                                //Pulsate for a few seconds then stay highlighted.
                                double matchTime = newValue.getMatchTimes( numerator, 6 ).maximum( Ord.<Double>comparableOrd() );
                                final double timeSinceMatch = newValue.time - matchTime;
                                final float strokeWidth = timeSinceMatch < 2 ? 2 + 8 * (float) Math.abs( Math.sin( model.state.get().time * 4 ) ) :
                                                          2 + 4;

                                //Block against unnecessary repainting for target cell highlighting
                                float originalWidth = ( (BasicStroke) getStroke() ).getLineWidth();
                                if ( originalWidth != strokeWidth ) {
                                    setStroke( new BasicStroke( strokeWidth, CAP_ROUND, BasicStroke.JOIN_ROUND, 1f ) );
                                }
                            }
                        }
                    } );
                }}, representationBox );
            }
        } );
        final Collection<PNode> nodes = scoreBoxes.toCollection();
        final VBox rightControlPanel = new VBox( new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT ), new VBox( nodes.toArray( new PNode[nodes.size()] ) ) ) {{
            setOffset( AbstractFractionsCanvas.STAGE_SIZE.width - getFullWidth() - AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
        addChild( rightControlPanel );

        //Add a piece container toolbox the user can use to get containers
        addChild( new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 160, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, controlPanelStroke, Color.darkGray );
            addChild( border );
            final double spacing = 60;
            final F<Integer, PNode> toNumberTool = new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return numberTool( i, model, canvas, i * spacing, numbersContainerLayer );
                }
            };
            addChild( new FNode( range( 0, 10 ).map( toNumberTool ) ) {{
                centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
            }} );

            setOffset( ( AbstractFractionsCanvas.STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }} );

        //Adding this listener before calling the update allows us to get the ChangeObserver callback.
        final DraggableFraction draggableFraction = new DraggableFraction( FractionID.nextID(), new DraggableObject( new Vector2D( 350, 350 ), true ), Option.<DefaultP2<DraggableNumberID, Double>>none(), Option.<DefaultP2<DraggableNumberID, Double>>none() );
        numbersContainerLayer.addChild( new DraggableFractionNode( draggableFraction.getID(), model, canvas ) );
        model.update( new ModelUpdate() {
            public BuildAFractionState update( final BuildAFractionState state ) {
                return state.addDraggableFraction( draggableFraction );
            }
        } );

        addChild( numbersContainerLayer );
    }

    public static PNode numberTool( final int number, final BuildAFractionModel model, final BuildAFractionCanvas canvas, final double offsetX, final PNode numberContainersLayer ) {
        return new PNode() {{
            addChild( numberGraphic( number ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                private DraggableNumberNode draggableNumberNode;

                @Override public void mousePressed( final PInputEvent event ) {

                    //Find out where to put the bar in stage coordinate frame, transform through the root node.
                    PBounds bounds = getGlobalFullBounds();
                    Rectangle2D localBounds = canvas.rootNode.globalToLocal( bounds );

                    final DraggableNumber draggableNumber = new DraggableNumber( DraggableNumberID.nextID(), new DraggableObject( new Vector2D( localBounds.getX(), localBounds.getY() ), true ), number, Option.<DefaultP2<FractionID, Boolean>>none() );

                    //Adding this listener before calling the update allows us to get the ChangeObserver callback.
                    //Store a reference so that we can check for overlap on release
                    draggableNumberNode = new DraggableNumberNode( draggableNumber.getID(), model, canvas );
                    numberContainersLayer.addChild( draggableNumberNode );

                    //Change the model
                    model.update( new ModelUpdate() {
                        public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addNumber( draggableNumber );
                        }
                    } );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    canvas.numberScene.draggableNumberNodeReleased( draggableNumberNode );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    model.dragNumber( event.getDeltaRelativeTo( canvas.rootNode ) );
                }
            } );
            setOffset( offsetX, 0 );
        }};
    }

    public static PNode numberGraphic( final int some ) { return new PhetPText( "" + some, new PhetFont( 64, true ) ); }

    //When the user drops a DraggableNumberNode (either from dragging from the toolbox or from a draggable node), this code
    //checks and attaches it to the target fractions (if any)
    public void draggableNumberNodeReleased( DraggableNumberNode node ) {

        Option<DraggableFractionNode> target = getDraggableNumberNodeDropTarget( node );
//                                System.out.println( "target = " + target );
        if ( target.isSome() ) {
            boolean numerator = node.getGlobalFullBounds().getCenterY() < target.some().getGlobalFullBounds().getCenterY();
//                                    System.out.println( "attaching, numerator = " + numerator );

            //Don't allow zero to attach to denominator
            final boolean triedToDivideByZero = !numerator && model.state.get().getDraggableNumber( node.id ).some().number == 0;

            //Make sure nothing already there
            final DraggableFraction targetModel = model.state.get().getDraggableFraction( target.some().id ).some();
            final boolean somethingInNumerator = targetModel.numerator.isSome();
            final boolean somethingInDenominator = targetModel.denominator.isSome();
            boolean somethingAlreadyThere = ( numerator && somethingInNumerator ) || ( !numerator && somethingInDenominator );

            if ( triedToDivideByZero || somethingAlreadyThere ) {
                //illegal, do not do
            }
            else {
                model.attachNumberToFraction( node.id, target.some().id, numerator );
            }
        }
        else {
            //                                model.draggableNumberNodeDropped( id );
            model.update( RELEASE_ALL );
        }
    }

    //Find what draggable fraction node the specified DraggableNumberNode is over for purposes of snapping/attaching
    public Option<DraggableFractionNode> getDraggableNumberNodeDropTarget( final DraggableNumberNode draggableNumberNode ) {
        for ( PNode node : numbersContainerLayer.getChildren() ) {
            //TODO: could split into 2 subnodes to segregate types
            if ( node instanceof DraggableFractionNode ) {
                DraggableFractionNode draggableFractionNode = (DraggableFractionNode) node;
                if ( draggableFractionNode.getGlobalFullBounds().intersects( draggableNumberNode.getGlobalFullBounds() ) ) {
                    return Option.some( draggableFractionNode );
                }
            }
        }
        return Option.none();
    }

    public DraggableFractionNode getDraggableFractionNode( final FractionID fractionID ) {
        for ( PNode node : numbersContainerLayer.getChildren() ) {
            //TODO: could split into 2 subnodes to segregate types
            if ( node instanceof DraggableFractionNode ) {
                DraggableFractionNode draggableFractionNode = (DraggableFractionNode) node;
                if ( draggableFractionNode.id.equals( fractionID ) ) {
                    return draggableFractionNode;
                }
            }
        }
        throw new RuntimeException( "Not found" );
    }

}