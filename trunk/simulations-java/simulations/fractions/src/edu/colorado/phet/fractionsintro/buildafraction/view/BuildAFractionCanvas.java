package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.data.List;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction.model.Container;
import edu.colorado.phet.fractionsintro.buildafraction.model.ContainerID;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableFraction;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumber;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumberID;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableObject;
import edu.colorado.phet.fractionsintro.buildafraction.model.FractionID;
import edu.colorado.phet.fractionsintro.buildafraction.model.Mode;
import edu.colorado.phet.fractionsintro.common.util.DefaultP2;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.UpdateNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;
import static fj.data.List.range;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;

/**
 * Main simulation canvas for "build a fraction" tab
 * TODO: duplicated code in pieceTool pieceGraphic DraggablePieceNode
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas {
    private static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    private final RichPNode picturesContainerLayer;
    private final RichPNode numbersContainerLayer;
    private static final Color CREAM = new Color( 251, 255, 218 );
    private final BuildAFractionModel model;

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        setBackground( CREAM );
        final Stroke controlPanelStroke = new BasicStroke( 2 );

        final SettableProperty<Mode> mode = model.toProperty(
                new F<BuildAFractionState, Mode>() {
                    @Override public Mode f( final BuildAFractionState s ) {
                        return s.mode;
                    }
                },
                new F2<BuildAFractionState, Mode, BuildAFractionState>() {
                    @Override public BuildAFractionState f( final BuildAFractionState s, final Mode mode ) {
                        return s.withMode( mode );
                    }
                }
        );

        //The draggable containers
        picturesContainerLayer = new RichPNode();
        numbersContainerLayer = new RichPNode();

        //View to show when the user is guessing numbers (by creating pictures)
        final PNode numberView = new PNode() {{

            final PNode radioButtonControlPanel = createModeControlPanel( mode );

            addChild( radioButtonControlPanel );
            List<PNode> scoreBoxes = range( 0, 3 ).map( new F<Integer, PNode>() {
                @Override public PNode f( final Integer integer ) {

                    //If these representationBox are all the same size, then 2-column layout will work properly
                    PNode representationBox = new PhetPText( "3/7", new PhetFont( 28, true ) );
                    return new HBox( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 160, 120, 30, 30 ), controlPanelStroke, Color.darkGray ), representationBox );
                }
            } );
            final Collection<PNode> nodes = scoreBoxes.toCollection();
            final VBox rightControlPanel = new VBox( new PhetPText( MY_FRACTIONS, CONTROL_FONT ), new VBox( nodes.toArray( new PNode[nodes.size()] ) ) ) {{
                setOffset( STAGE_SIZE.width - getFullWidth() - INSET, STAGE_SIZE.height / 2 - this.getFullHeight() / 2 );
            }};
            addChild( rightControlPanel );

            ////Add a piece container toolbox the user can use to get containers
            addChild( new RichPNode() {{
                final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 125, 30, 30 ), controlPanelStroke, Color.darkGray );
                addChild( border );
                final F<Integer, PNode> toBar = new F<Integer, PNode>() {
                    @Override public PNode f( final Integer i ) {
                        return barTool( new Container( ContainerID.nextID(), new DraggableObject( rowColumnToPoint( i % 2, i / 2 ), false ), i + 1 ), model, BuildAFractionCanvas.this );
                    }
                };
                addChild( new FNode( range( 0, 8 ).map( toBar ) ) {{
                    centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
                }} );
                setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, STAGE_SIZE.height - INSET - this.getFullHeight() );
            }} );

            //Bucket view at the bottom of the screen
            addChild( new RichPNode() {{
                final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 150, 30, 30 ), controlPanelStroke, Color.darkGray );
                addChild( border );

                //            BucketView bucketView = new BucketView( new Bucket( 150, -50, new Dimension2DDouble( 200, 100 ), Color.blue, "pieces" ), ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 1, -1 ) );
                //            addChild( bucketView.getHoleNode() );
                //            addChild( bucketView.getFrontNode() );

                final F<Integer, PNode> toBar = new F<Integer, PNode>() {
                    @Override public PNode f( final Integer i ) {
                        return pieceTool( new Container( ContainerID.nextID(), new DraggableObject( rowColumnToPoint( i % 2, i / 2 ), false ), i + 1 ), model, BuildAFractionCanvas.this );
                    }
                };
                addChild( new FNode( range( 0, 8 ).map( toBar ) ) {{
                    centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
                }} );

                setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, radioButtonControlPanel.getFullBounds().getMaxY() + INSET );
            }} );

            addChild( numbersContainerLayer );
        }};

        final PNode pictureView = new PNode() {{

            final PNode radioButtonControlPanel = createModeControlPanel( mode );
            addChild( radioButtonControlPanel );

            List<PNode> scoreBoxes = range( 0, 3 ).map( new F<Integer, PNode>() {
                @Override public PNode f( final Integer integer ) {

                    //If these representationBox are all the same size, then 2-column layout will work properly
                    final int numerator = integer + 1;
                    PNode representationBox = new PatternNode( FilledPattern.sequentialFill( Pattern.sixFlower( 18 ), numerator ), Color.red );
                    return new HBox( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 140, 150, 30, 30 ), controlPanelStroke, Color.darkGray ) {{
                        //Light up if the user matched
                        model.addObserver( new ChangeObserver<BuildAFractionState>() {
                            @Override public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
                                if ( newValue.containsMatch( numerator, 6 ) != oldValue.containsMatch( numerator, 6 ) ) {
                                    setStrokePaint( newValue.containsMatch( numerator, 6 ) ? Color.red : Color.darkGray );
                                }
                                if ( newValue.containsMatch( numerator, 6 ) ) {
//                                    System.out.println( "model.state.get().time = " + model.state.get().time );
                                    setStroke( new BasicStroke( 4, CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 20, 10 }, (float) ( model.state.get().time * 60 ) ) );
                                }
                            }
                        } );
                    }}, representationBox );
                }
            } );
            final Collection<PNode> nodes = scoreBoxes.toCollection();
            final VBox rightControlPanel = new VBox( new PhetPText( MY_FRACTIONS, CONTROL_FONT ), new VBox( nodes.toArray( new PNode[nodes.size()] ) ) ) {{
                setOffset( STAGE_SIZE.width - getFullWidth() - INSET, INSET );
            }};
            addChild( rightControlPanel );

            //Add a piece container toolbox the user can use to get containers
            addChild( new RichPNode() {{
                final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 160, 30, 30 ), controlPanelStroke, Color.darkGray );
                addChild( border );
                final double spacing = 60;
                final F<Integer, PNode> toNumberTool = new F<Integer, PNode>() {
                    @Override public PNode f( final Integer i ) {
                        return numberTool( i, model, BuildAFractionCanvas.this, i * spacing );
                    }
                };
                final PNode fractionTool = fractionTool( -spacing, model, BuildAFractionCanvas.this );
                fractionTool.translate( 0, -fractionTool.getFullBounds().getHeight() / 4 * 0.7 );//Fudge factor to line them up
                addChild( new FNode( range( 0, 10 ).map( toNumberTool ).cons( fractionTool ) ) {{
                    centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
                }} );

                setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, STAGE_SIZE.height - INSET - this.getFullHeight() );
            }} );

            addChild( picturesContainerLayer );
        }};

        //When the mode changes, update the toolboxes
        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode node ) {
                node.addChild( mode.get() == Mode.NUMBERS ? numberView : pictureView );
            }
        }, mode ) );

        //Reset all button
        addChild( new ResetAllButtonNode( model, this, 18, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
            setOffset( STAGE_SIZE.width - this.getFullWidth() - INSET, STAGE_SIZE.height - this.getFullHeight() - INSET );
        }} );
    }

    private PNode createModeControlPanel( final SettableProperty<Mode> mode ) {
        return new HBox( radioButton( Components.picturesRadioButton, Strings.PICTURES, mode, Mode.NUMBERS ),
                         radioButton( Components.numbersRadioButton, Strings.NUMBERS, mode, Mode.PICTURES ) ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
    }

    public static Vector2D rowColumnToPoint( int row, int column ) {
        final int spacingX = 15;
        final int spacingY = 15;
        final double x = column * ( barWidth + spacingX );
        final double y = row * ( barHeight + spacingY );
        return new Vector2D( x, y );
    }

    final static double barWidth = 145;
    static final double barHeight = 40;

    public static PNode barGraphic( final Container container ) {
        return new PNode() {{
            final double sliceWidth = barWidth / container.numSegments;
            List<PNode> nodes = range( 0, container.numSegments ).map( new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, barHeight ), TRANSPARENT, new BasicStroke( 2 ), black );
                }
            } );
            addChild( new FNode( nodes ) );
            setOffset( container.getPosition().toPoint2D() );
        }};
    }

    public static PNode pieceGraphic( final Container container ) {
        return new PNode() {{
            final double sliceWidth = barWidth / container.numSegments;
            List<PNode> nodes = range( 0, 1 ).map( new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, barHeight ), Color.green );
                }
            } );
            addChild( new FNode( nodes ) );
            setOffset( container.getPosition().toPoint2D() );
        }};
    }

    public static PNode pieceTool( final Container container, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        return new PNode() {{
            addChild( pieceGraphic( container ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {

                    //Find out where to put the bar in stage coordinate frame, transform through the root node.
                    PBounds bounds = getGlobalFullBounds();
                    Rectangle2D localBounds = canvas.rootNode.globalToLocal( bounds );

                    final Container c = new Container( ContainerID.nextID(), new DraggableObject( new Vector2D( localBounds.getX(), localBounds.getY() ), true ), container.numSegments );
                    canvas.numbersContainerLayer.addChild( new DraggablePieceNode( c.getID(), model, canvas ) );
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addContainer( c );
                        }
                    } );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    model.update( RELEASE_ALL );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    model.dragContainer( event.getDeltaRelativeTo( canvas.rootNode ) );
                }
            } );
        }};
    }

    public static PNode barTool( final Container container, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        return new PNode() {{
            addChild( barGraphic( container ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {

                    //Find out where to put the bar in stage coordinate frame, transform through the root node.
                    PBounds bounds = getGlobalFullBounds();
                    Rectangle2D localBounds = canvas.rootNode.globalToLocal( bounds );

                    final Container c = new Container( ContainerID.nextID(), new DraggableObject( new Vector2D( localBounds.getX(), localBounds.getY() ), true ), container.numSegments );
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addContainer( c );
                        }
                    } );
                    canvas.numbersContainerLayer.addChild( new DraggableContainerNode( c.getID(), model, canvas ) );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    model.update( RELEASE_ALL );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    model.dragContainer( event.getDeltaRelativeTo( canvas.rootNode ) );
                }
            } );
        }};
    }

    public static PNode numberTool( final int number, final BuildAFractionModel model, final BuildAFractionCanvas canvas, final double offsetX ) {
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
                    canvas.picturesContainerLayer.addChild( draggableNumberNode );

                    //Change the model
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addNumber( draggableNumber );
                        }
                    } );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    canvas.draggableNumberNodeReleased( draggableNumberNode );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    model.dragNumber( event.getDeltaRelativeTo( canvas.rootNode ) );
                }
            } );
            setOffset( offsetX, 0 );
        }};
    }

    public static PNode fractionTool( final double offsetX, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        return new PNode() {{
            addChild( emptyFractionGraphic( false ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                private FractionID id;

                @Override public void mousePressed( final PInputEvent event ) {

                    //Find out where to put the bar in stage coordinate frame, transform through the root node.
                    PBounds bounds = getGlobalFullBounds();
                    Rectangle2D localBounds = canvas.rootNode.globalToLocal( bounds );

                    final DraggableFraction draggableFraction = new DraggableFraction( FractionID.nextID(), new DraggableObject( new Vector2D( localBounds.getX(), localBounds.getY() ), true ), Option.<DraggableNumberID>none(), Option.<DraggableNumberID>none() );
                    id = draggableFraction.getID();

                    //Adding this listener before calling the update allows us to get the ChangeObserver callback.
                    canvas.picturesContainerLayer.addChild( new DraggableFractionNode( draggableFraction.getID(), model, canvas ) );

                    //Change the model
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addDraggableFraction( draggableFraction );
                        }
                    } );
                }

                @Override public void mouseReleased( final PInputEvent event ) { model.update( RELEASE_ALL ); }

                @Override public void mouseDragged( final PInputEvent event ) { model.dragFraction( id, event.getDeltaRelativeTo( canvas.rootNode ) ); }
            } );
            setOffset( offsetX, 0 );
        }};
    }

    public static PNode emptyFractionGraphic( boolean showNumeratorOutline, boolean showDenominatorOutline ) {
        final VBox box = new VBox( box( showNumeratorOutline ), divisorLine(), box( showDenominatorOutline ) );

        //Show a background behind it to make the entire shape draggable
        final PhetPPath background = new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), TRANSPARENT );
        return new RichPNode( background, box );
    }

    public static PNode emptyFractionGraphic( boolean isUserDraggingZero ) { return emptyFractionGraphic( true, !isUserDraggingZero ); }

    private static PNode divisorLine() { return new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black ); }

    private static PhetPPath box( boolean showOutline ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40, 50 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : TRANSPARENT );
    }

    private PNode radioButton( IUserComponent component, final String text, final SettableProperty<Mode> mode, Mode value ) {
        return new PSwing( new PropertyRadioButton<Mode>( component, text, mode, value ) {{
            setOpaque( false );
            setFont( AbstractFractionsCanvas.CONTROL_FONT );
        }} );
    }

    public static PNode numberGraphic( final int some ) { return new PhetPText( "" + some, new PhetFont( 64, true ) ); }

    //Find what draggable fraction node the specified DraggableNumberNode is over for purposes of snapping/attaching
    public Option<DraggableFractionNode> getDraggableNumberNodeDropTarget( final DraggableNumberNode draggableNumberNode ) {
        for ( PNode node : picturesContainerLayer.getChildren() ) {
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
        for ( PNode node : picturesContainerLayer.getChildren() ) {
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
}