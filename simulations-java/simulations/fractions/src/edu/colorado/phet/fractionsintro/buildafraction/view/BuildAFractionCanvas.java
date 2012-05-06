package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;

import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
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
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;
import static fj.data.List.range;

/**
 * Main simulation canvas for "build a fraction" tab
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas {
    private static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    private final BuildAFractionModel model;
    private final RichPNode containerLayer;

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        final Stroke stroke = new BasicStroke( 2 );
        final VBox radioButtonControlPanel = new VBox( 0, VBox.LEFT_ALIGNED,
                                                       radioButton( "Numbers" ),
                                                       radioButton( "Pictures" ) );

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

        //Add a piece container toolbox the user can use to get containers
        addChild( new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 125, 30, 30 ), stroke, Color.darkGray );
            addChild( border );
            final F<Integer, PNode> toBar = new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return barTool( new Container( ContainerID.nextID(), i + 1, rowColumnToPoint( i % 2, i / 2 ), false ), model, BuildAFractionCanvas.this );
                }
            };
            addChild( new FNode( range( 0, 8 ).map( toBar ) ) {{
                centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
            }} );
            setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, INSET );
        }} );

        //Bucket view at the bottom of the screen
        addChild( new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 150, 30, 30 ), stroke, Color.darkGray );
            addChild( border );

            BucketView bucketView = new BucketView( new Bucket( 150, -50, new Dimension2DDouble( 200, 100 ), Color.blue, "pieces" ), ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 1, -1 ) );
            addChild( bucketView.getHoleNode() );
            addChild( bucketView.getFrontNode() );

            setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, STAGE_SIZE.height - INSET - this.getFullHeight() );
        }} );

        //The draggable containers
        containerLayer = new RichPNode();
        addChild( containerLayer );
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
                    return new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, barHeight ), TRANSPARENT, new BasicStroke( 1 ), Color.black );
                }
            } );
            addChild( new FNode( nodes ) );
            setOffset( container.position.toPoint2D() );
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

                    final Container c = new Container( ContainerID.nextID(), container.numSegments, new Vector2D( localBounds.getX(), localBounds.getY() ), true );
                    model.update( new ModelUpdate() {
                        @Override public BuildAFractionState update( final BuildAFractionState state ) {
                            return state.addContainer( c );
                        }
                    } );
                    canvas.containerLayer.addChild( new DraggableBarNode( c.id, model, canvas ) );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    model.update( RELEASE_ALL );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    model.drag( event.getDeltaRelativeTo( canvas.rootNode ) );
                }
            } );
        }};
    }

    private PNode radioButton( final String text ) {
        return new PSwing( new JRadioButton( text ) {{
            setOpaque( false );
            setFont( AbstractFractionsCanvas.CONTROL_FONT );
        }} );
    }
}