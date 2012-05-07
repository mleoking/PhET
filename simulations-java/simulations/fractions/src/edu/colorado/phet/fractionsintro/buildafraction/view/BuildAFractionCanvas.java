package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction.model.Container;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumber;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableObject;
import edu.colorado.phet.fractionsintro.buildafraction.model.Mode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.view.UpdateNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.numbersRadioButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.picturesRadioButton;
import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;
import static fj.data.List.range;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;
import static java.awt.Color.red;

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

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        final Stroke stroke = new BasicStroke( 2 );

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

        final VBox radioButtonControlPanel = new VBox( 0, VBox.LEFT_ALIGNED,
                                                       radioButton( numbersRadioButton, "Numbers", mode, Mode.NUMBERS ),
                                                       radioButton( picturesRadioButton, "Pictures", mode, Mode.PICTURES ) );

        //IDEA: show the target in the box but grayed out and dotted line.  When the user has a match, it turns red dotted line.  When they drop it in, it fills in.
        //Would this have worked for build a molecule?
        List<PNode> scoreBoxes = range( 0, 4 ).map( new F<Integer, PNode>() {
            @Override public PNode f( final Integer integer ) {
                return new PhetPPath( new RoundRectangle2D.Double( 0, 0, 120, 120, 30, 30 ), stroke, Color.darkGray );
            }
        } );
        final Collection<PNode> nodes = scoreBoxes.toCollection();
        final VBox rightControlPanel = new VBox( radioButtonControlPanel, new Spacer( 0, 0, 10, 10 ), new PhetPText( MY_FRACTIONS, CONTROL_FONT ), new VBox( nodes.toArray( new PNode[nodes.size()] ) ) ) {{
            setOffset( STAGE_SIZE.width - getFullWidth() - INSET, INSET );
        }};
        addChild( rightControlPanel );

        //The draggable containers
        picturesContainerLayer = new RichPNode();
        numbersContainerLayer = new RichPNode();

        //View to show when the user is guessing numbers (by creating pictures)
        final PNode numberView = new PNode() {{
            ////Add a piece container toolbox the user can use to get containers
            addChild( new RichPNode() {{
                final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 125, 30, 30 ), stroke, Color.darkGray );
                addChild( border );
                final F<Integer, PNode> toBar = new F<Integer, PNode>() {
                    @Override public PNode f( final Integer i ) {
                        return barTool( new Container( new DraggableObject( ObjectID.nextID(), rowColumnToPoint( i % 2, i / 2 ), false ), i + 1 ), model, BuildAFractionCanvas.this );
                    }
                };
                addChild( new FNode( range( 0, 8 ).map( toBar ) ) {{
                    centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
                }} );
                setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, STAGE_SIZE.height - INSET - this.getFullHeight() );
            }} );

            //Bucket view at the bottom of the screen
            addChild( new RichPNode() {{
                final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 150, 30, 30 ), stroke, Color.darkGray );
                addChild( border );

                //            BucketView bucketView = new BucketView( new Bucket( 150, -50, new Dimension2DDouble( 200, 100 ), Color.blue, "pieces" ), ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 1, -1 ) );
                //            addChild( bucketView.getHoleNode() );
                //            addChild( bucketView.getFrontNode() );

                final F<Integer, PNode> toBar = new F<Integer, PNode>() {
                    @Override public PNode f( final Integer i ) {
                        return pieceTool( new Container( new DraggableObject( ObjectID.nextID(), rowColumnToPoint( i % 2, i / 2 ), false ), i + 1 ), model, BuildAFractionCanvas.this );
                    }
                };
                addChild( new FNode( range( 0, 8 ).map( toBar ) ) {{
                    centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
                }} );

                setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, INSET );
            }} );

            addChild( numbersContainerLayer );
        }};

        final PNode pictureView = new PNode() {{
            //Add a piece container toolbox the user can use to get containers
            addChild( new RichPNode() {{
                final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 125, 30, 30 ), stroke, Color.darkGray );
                addChild( border );

                final F<Integer, PNode> toNumberTool = new F<Integer, PNode>() {
                    @Override public PNode f( final Integer i ) {
                        return numberTool( i, model, BuildAFractionCanvas.this, i * 50 );
                    }
                };
                addChild( new FNode( range( 0, 10 ).map( toNumberTool ).cons( fractionTool( -50, model, BuildAFractionCanvas.this ) ) ) {{
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
    }

    public static Vector2D rowColumnToPoint( int row, int column ) {
        final int spacingX = 15;
        final int spacingY = 15;
        final double x = column * ( barWidth + spacingX );
        final double y = row * ( barHeight + spacingY );
        return new Vector2D( x, y );
    }

    final static double barWidth = 120;
    static final double barHeight = 25;

    public static PNode barGraphic( final Container container ) {
        return new PNode() {{
            final double sliceWidth = barWidth / container.numSegments;
            List<PNode> nodes = range( 0, container.numSegments ).map( new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, barHeight ), TRANSPARENT, new BasicStroke( 1 ), black );
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
                    return new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, barHeight ), Color.green, new BasicStroke( 1 ), black );
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

                    final Container c = new Container( new DraggableObject( ObjectID.nextID(), new Vector2D( localBounds.getX(), localBounds.getY() ), true ), container.numSegments );
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addContainer( c );
                        }
                    } );
                    canvas.numbersContainerLayer.addChild( new DraggablePieceNode( c.getID(), model, canvas ) );
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

                    final Container c = new Container( new DraggableObject( ObjectID.nextID(), new Vector2D( localBounds.getX(), localBounds.getY() ), true ), container.numSegments );
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addContainer( c );
                        }
                    } );
                    canvas.numbersContainerLayer.addChild( new DraggableBarNode( c.getID(), model, canvas ) );
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

    public static PNode numberTool( final int number, final BuildAFractionModel model, final BuildAFractionCanvas canvas, final int offsetX ) {
        return new PNode() {{
            addChild( numberGraphic( number ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {

                    //Find out where to put the bar in stage coordinate frame, transform through the root node.
                    PBounds bounds = getGlobalFullBounds();
                    Rectangle2D localBounds = canvas.rootNode.globalToLocal( bounds );

                    final DraggableNumber draggableNumber = new DraggableNumber( new DraggableObject( ObjectID.nextID(), new Vector2D( localBounds.getX(), localBounds.getY() ), true ), number );

                    //Adding this listener before calling the update allows us to get the ChangeObserver callback.
                    canvas.picturesContainerLayer.addChild( new DraggableNumberNode( draggableNumber.getID(), model, canvas ) );

                    //Change the model
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addNumber( draggableNumber );
                        }
                    } );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    model.update( RELEASE_ALL );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    model.dragNumber( event.getDeltaRelativeTo( canvas.rootNode ) );
                }
            } );
            setOffset( offsetX, 0 );
        }};
    }

    public static PNode fractionTool( final int offsetX, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        return new PNode() {{
            addChild( emptyFractionGraphic() );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {

                    //Find out where to put the bar in stage coordinate frame, transform through the root node.
                    PBounds bounds = getGlobalFullBounds();
                    Rectangle2D localBounds = canvas.rootNode.globalToLocal( bounds );

                    final DraggableFraction draggableFraction = new DraggableFraction( new DraggableObject( ObjectID.nextID(), new Vector2D( localBounds.getX(), localBounds.getY() ), true ) );

                    //Adding this listener before calling the update allows us to get the ChangeObserver callback.
                    canvas.picturesContainerLayer.addChild( new DraggableFractionNode( draggableFraction.getID(), model, canvas ) );

                    //Change the model
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addDraggableFraction( draggableFraction );
                        }
                    } );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    model.update( RELEASE_ALL );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    model.dragFraction( event.getDeltaRelativeTo( canvas.rootNode ) );
                }
            } );
            setOffset( offsetX, 0 );
        }};
    }

    public static PNode emptyFractionGraphic() { return new VBox( box(), divisorLine(), box() ); }

    private static PNode divisorLine() { return new PhetPPath( new Line2D.Double( 0, 0, 40, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black ); }

    private static PhetPPath box() {return new PhetPPath( new Rectangle2D.Double( 0, 0, 30, 40 ), new BasicStroke( 1, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 3 }, 0 ), red );}

    private PNode radioButton( IUserComponent component, final String text, final SettableProperty<Mode> mode, Mode value ) {
        return new PSwing( new PropertyRadioButton<Mode>( component, text, mode, value ) {{
            setOpaque( false );
            setFont( AbstractFractionsCanvas.CONTROL_FONT );
        }} );
    }

    public static PNode numberGraphic( final int some ) { return new PhetPText( "" + some, new PhetFont( 24, true ) ); }
}