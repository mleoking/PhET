package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;

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
import edu.colorado.phet.fractionsintro.buildafraction.model.Container;
import edu.colorado.phet.fractionsintro.buildafraction.model.ContainerID;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableObject;
import edu.colorado.phet.fractionsintro.buildafraction.model.Mode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;
import static edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas.*;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static fj.data.List.range;

/**
 * @author Sam Reid
 */
public class PictureScene extends PNode {
    public final RichPNode picturesContainerLayer;

    public PictureScene( final BuildAFractionModel model, final SettableProperty<Mode> mode, final BuildAFractionCanvas canvas ) {

        picturesContainerLayer = new RichPNode();
        final PNode radioButtonControlPanel = createModeControlPanel( mode );

        addChild( radioButtonControlPanel );
        List<PNode> scoreBoxes = range( 0, 3 ).map( new F<Integer, PNode>() {
            @Override public PNode f( final Integer integer ) {

                //If these representationBox are all the same size, then 2-column layout will work properly
                PNode representationBox = new PhetPText( "3/7", new PhetFont( 28, true ) );
                return new HBox( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 160, 120, 30, 30 ), BuildAFractionCanvas.controlPanelStroke, Color.darkGray ), representationBox );
            }
        } );
        final Collection<PNode> nodes = scoreBoxes.toCollection();
        final VBox rightControlPanel = new VBox( new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT ), new VBox( nodes.toArray( new PNode[nodes.size()] ) ) ) {{
            setOffset( STAGE_SIZE.width - getFullWidth() - INSET, STAGE_SIZE.height / 2 - this.getFullHeight() / 2 );
        }};
        addChild( rightControlPanel );

        ////Add a piece container toolbox the user can use to get containers
        addChild( new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 125, 30, 30 ), CONTROL_PANEL_BACKGROUND, controlPanelStroke, Color.darkGray );
            addChild( border );
            final F<Integer, PNode> toBar = new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return barTool( new Container( ContainerID.nextID(), new DraggableObject( rowColumnToPoint( i % 2, i / 2 ), false ), i + 1 ), model, canvas, picturesContainerLayer );
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
                    return pieceTool( new Container( ContainerID.nextID(), new DraggableObject( rowColumnToPoint( i % 2, i / 2 ), false ), i + 1 ), model, canvas, picturesContainerLayer );
                }
            };
            addChild( new FNode( range( 0, 8 ).map( toBar ) ) {{
                centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
            }} );

            setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, radioButtonControlPanel.getFullBounds().getMaxY() + INSET );
        }} );

        addChild( picturesContainerLayer );
    }

    public static PNode barTool( final Container container, final BuildAFractionModel model, final BuildAFractionCanvas canvas, final PNode picturesContainerLayer ) {
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
                        public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addContainer( c );
                        }
                    } );
                    picturesContainerLayer.addChild( new DraggableContainerNode( c.getID(), model, canvas ) );
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

    final static double barWidth = 145;
    static final double barHeight = 40;

    public static PNode barGraphic( final Container container ) {
        return new PNode() {{
            final double sliceWidth = barWidth / container.numSegments;
            List<PNode> nodes = range( 0, container.numSegments ).map( new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, barHeight ), TRANSPARENT, new BasicStroke( 2 ), Color.black );
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

    public static PNode pieceTool( final Container container, final BuildAFractionModel model, final BuildAFractionCanvas canvas, final PNode picturesContainerLayer ) {
        return new PNode() {{
            addChild( pieceGraphic( container ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {

                    //Find out where to put the bar in stage coordinate frame, transform through the root node.
                    PBounds bounds = getGlobalFullBounds();
                    Rectangle2D localBounds = canvas.rootNode.globalToLocal( bounds );

                    final Container c = new Container( ContainerID.nextID(), new DraggableObject( new Vector2D( localBounds.getX(), localBounds.getY() ), true ), container.numSegments );
                    picturesContainerLayer.addChild( new DraggablePieceNode( c.getID(), model, canvas ) );
                    model.update( new ModelUpdate() {
                        public BuildAFractionState update( final BuildAFractionState state ) {
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

    public static Vector2D rowColumnToPoint( int row, int column ) {
        final int spacingX = 15;
        final int spacingY = 15;
        final double x = column * ( barWidth + spacingX );
        final double y = row * ( barHeight + spacingY );
        return new Vector2D( x, y );
    }

}