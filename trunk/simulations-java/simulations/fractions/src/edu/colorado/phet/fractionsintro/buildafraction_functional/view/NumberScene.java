package edu.colorado.phet.fractionsintro.buildafraction_functional.view;

import fj.Equal;
import fj.F;
import fj.data.List;
import fj.data.Option;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.buildafraction_functional.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.DraggableFraction;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.DraggableNumber;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.DraggableNumberID;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.DraggableObject;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.FractionID;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.Mode;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.TargetCell;
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
import static edu.colorado.phet.fractionsintro.buildafraction_functional.model.BuildAFractionState.RELEASE_ALL;
import static edu.colorado.phet.fractionsintro.buildafraction_functional.view.BuildAFractionCanvas.controlPanelStroke;
import static fj.data.List.range;

/**
 * @author Sam Reid
 */
public class NumberScene extends PNode {

    //The draggable containers
    public final RichPNode fractionLayer = new RichPNode();

    //The draggable numbers
    public final RichPNode numberLayer = new RichPNode();

    //The model
    private final BuildAFractionModel model;
    private final List<ScoreBoxNode> scoreBoxes;

    public NumberScene( final BuildAFractionModel model, final SettableProperty<Mode> mode, final BuildAFractionCanvas canvas ) {
        this.model = model;

        final PNode radioButtonControlPanel = BuildAFractionCanvas.createModeControlPanel( mode );
        addChild( radioButtonControlPanel );
        fractionLayer.addPropertyChangeListener( PNode.PROPERTY_CHILDREN, new PropertyChangeListener() {
            public void propertyChange( final PropertyChangeEvent evt ) {
                System.out.println( "evt = " + evt );
            }
        } );
        scoreBoxes = model.state.get().targetCells.map( new F<TargetCell, ScoreBoxNode>() {
            @Override public ScoreBoxNode f( final TargetCell targetCell ) {

                //If these representationBox are all the same size, then 2-column layout will work properly
                final int numerator = targetCell.index + 1;
                PNode representationBox = new PatternNode( FilledPattern.sequentialFill( Pattern.sixFlower( 18 ), numerator ), Color.red );
                return new ScoreBoxNode( numerator, 6, representationBox, model, targetCell );
            }
        } );
        final Collection<ScoreBoxNode> nodes = scoreBoxes.toCollection();
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
                    return numberTool( i, model, canvas, i * spacing, numberLayer );
                }
            };
            addChild( new FNode( range( 0, 10 ).map( toNumberTool ) ) {{
                centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
            }} );

            setOffset( ( AbstractFractionsCanvas.STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }} );

        //Adding this listener before calling the update allows us to get the ChangeObserver callback.
        final DraggableFraction draggableFraction = DraggableFraction.createDefault();
        fractionLayer.addChild( new DraggableFractionNode( draggableFraction.getID(), model, canvas ) );
        model.update( new ModelUpdate() {
            public BuildAFractionState update( final BuildAFractionState state ) {
                return state.addDraggableFraction( draggableFraction );
            }
        } );

        //instead of these passive observers, register for a callback on the FractionID added method on the model
        model.addObserver( new ChangeObserver<BuildAFractionState>() {
            public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
                //find what added
                final F<DraggableFraction, FractionID> getID = new F<DraggableFraction, FractionID>() {
                    @Override public FractionID f( final DraggableFraction draggableFraction ) {
                        return draggableFraction.id;
                    }
                };
                List<FractionID> orig = oldValue.draggableFractions.map( getID );
                List<FractionID> next = newValue.draggableFractions.map( getID );
                List<FractionID> added = next.minus( Equal.<FractionID>anyEqual(), orig );
                System.out.println( "added = " + added );
                for ( FractionID id : added ) {
                    fractionLayer.addChild( new DraggableFractionNode( id, model, canvas ) );
                }
            }
        } );

        addChild( fractionLayer );
        addChild( numberLayer );
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
    public void draggableNumberNodeReleased( final DraggableNumberNode node ) {

        Option<DraggableFractionNode> target = getDraggableNumberNodeDropTarget( node );
        if ( target.isSome() ) {
            boolean numerator = node.getGlobalFullBounds().getCenterY() < target.some().getGlobalFullBounds().getCenterY();

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
        for ( PNode node : fractionLayer.getChildren() ) {
            DraggableFractionNode draggableFractionNode = (DraggableFractionNode) node;
            if ( draggableFractionNode.getGlobalFullBounds().intersects( draggableNumberNode.getGlobalFullBounds() ) ) {
                return Option.some( draggableFractionNode );
            }
        }
        return Option.none();
    }

    public DraggableFractionNode getDraggableFractionNode( final FractionID fractionID ) {
        System.out.println( "getExistingIDs() = " + getExistingIDs() );

        for ( PNode node : fractionLayer.getChildren() ) {
            DraggableFractionNode draggableFractionNode = (DraggableFractionNode) node;
            if ( draggableFractionNode.id.equals( fractionID ) ) {
                return draggableFractionNode;
            }
        }

        throw new RuntimeException( "No graphic found for FractionID = " + fractionID + ", existing existingIDs: " + getExistingIDs() );
    }

    private ArrayList<FractionID> getExistingIDs() {
        ArrayList<FractionID> existingIDs = new ArrayList<FractionID>();
        for ( PNode node : fractionLayer.getChildren() ) {
            existingIDs.add( ( (DraggableFractionNode) node ).id );
        }
        return existingIDs;
    }

    public void fractionNodeDropped( final FractionID id ) {
        //see if it overlaps a target cell
        model.releaseFraction( id );

        final DraggableFractionNode node = getDraggableFractionNode( id );
        final List<ScoreBoxNode> matches = scoreBoxes.filter( new F<ScoreBoxNode, Boolean>() {
            @Override public Boolean f( final ScoreBoxNode scoreCell ) {
                return scoreCell.getGlobalFullBounds().intersects( node.getGlobalFullBounds() );
            }
        } ).sort( FJUtils.ord( new F<ScoreBoxNode, Double>() {
            @Override public Double f( final ScoreBoxNode scoreCell ) {
                return area( scoreCell.getGlobalFullBounds().createIntersection( node.getGlobalFullBounds() ) );
            }
        } ) ).reverse();
        if ( matches.isNotEmpty() ) {
            ScoreBoxNode scoreBox = matches.head();

            if ( scoreBox.fraction.approxEquals( model.getFractionValue( id ) ) ) {

                Point2D point = new Point2D.Double( scoreBox.getGlobalFullBounds().getCenterX(), scoreBox.getGlobalFullBounds().getCenterY() );
                point = globalToLocal( point );

                //TODO: figure out coordinate frames to get rid of magic numbers
                model.moveFractionToTargetCell( id, new Vector2D( point ).minus( new Vector2D( 60, 65 ) ), scoreBox.targetCell );
            }
        }
    }

    private double area( final Rectangle2D rectangle2D ) { return rectangle2D.getWidth() * rectangle2D.getHeight(); }
}