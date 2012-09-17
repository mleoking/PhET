package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.Effect;
import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.forcesandmotionbasics.AbstractForcesAndMotionBasicsCanvas;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.KnotNode._free;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.BLUE;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.RED;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize.*;

/**
 * @author Sam Reid
 */
public class TugOfWarCanvas extends AbstractForcesAndMotionBasicsCanvas implements PullerContext {

    public static final long ANIMATION_DURATION = 300;
    private final List<KnotNode> blueKnots;
    private final List<KnotNode> redKnots;

    public TugOfWarCanvas( final Context context ) {

        setBackground( new Color( 209, 210, 212 ) );
        //use view coordinates since nothing compex happening in model coordinates.

        //for a canvas height of 710, the ground is at 452 down from the top
        final int width = 10000;

        //Reverse bottom and top because using view coordinates
        final int grassY = 452;
        addChild( new SkyNode( createIdentity(), new Rectangle2D.Double( -width / 2, -width / 2 + grassY, width, width / 2 ), grassY, SkyNode.DEFAULT_TOP_COLOR, SkyNode.DEFAULT_BOTTOM_COLOR ) );

        final PImage grassNode = new PImage( Images.GRASS );
        grassNode.setOffset( -2, grassY - 2 );
        addChild( grassNode );

        final ControlPanelNode controlPanelNode = new ControlPanelNode(
                new VBox( 2, VBox.LEFT_ALIGNED,

                          //Nudge "show" to the right so it will align with checkboxes
                          new HBox( 5, new PhetPPath( new Rectangle2D.Double( 0, 0, 0, 0 ) ), new PhetPText( "Show", CONTROL_FONT ) ),
                          new PSwing( new JCheckBox( "Values" ) {{setFont( CONTROL_FONT );}} ), new PSwing( new JCheckBox( "Sum of Forces" ) {{
                    setFont( CONTROL_FONT );
                }} ) ), new Color( 227, 233, 128 ), new BasicStroke( 2 ), Color.black );
        controlPanelNode.setOffset( STAGE_SIZE.width - controlPanelNode.getFullWidth() - INSET, INSET );
        addChild( controlPanelNode );

        addChild( new ResetAllButtonNode( new Resettable() {
            public void reset() {
                context.reset();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getMaxY() + INSET );
            setConfirmationEnabled( false );
        }} );

        PImage cart = new PImage( Images.CART );
        cart.setOffset( STAGE_SIZE.width / 2 - cart.getFullBounds().getWidth() / 2, grassY - cart.getFullBounds().getHeight() + 4 );


        final PImage rope = new PImage( Images.ROPE );
        rope.setOffset( STAGE_SIZE.width / 2 - rope.getFullBounds().getWidth() / 2, cart.getFullBounds().getCenterY() - rope.getFullBounds().getHeight() / 2 );

        addChild( rope );
        addChild( cart );

        final double IMAGE_SCALE = 0.75;
        Vector2D largePosition = Vector2D.v( 88.38995568685374, 488.15361890694203 );
        Vector2D mediumPosition = Vector2D.v( 151.66912850812423, 513.264401772526 );
        Vector2D smallPosition1 = Vector2D.v( 215.9527326440175, 558.463810930576 );
        Vector2D smallPosition2 = Vector2D.v( 263.1610044313148, 559.4682422451999 );
        final PNode largeRedPuller = puller( BLUE, LARGE, IMAGE_SCALE, largePosition, this );
        addChild( largeRedPuller );
        addChild( puller( BLUE, MEDIUM, IMAGE_SCALE, mediumPosition, this ) );
        addChild( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition1, this ) );
        addChild( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition2, this ) );

        final double offset = largeRedPuller.getFullBounds().getWidth();
        addChild( puller( RED, LARGE, IMAGE_SCALE, reflect( largePosition, offset ), this ) );
        addChild( puller( RED, MEDIUM, IMAGE_SCALE, reflect( mediumPosition, offset ), this ) );
        addChild( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition1, offset ), this ) );
        addChild( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition2, offset ), this ) );

        double w = 10;
        blueKnots = ImageMetrics.blueKnots.map( new F<Double, KnotNode>() {
            @Override public KnotNode f( final Double knotLocation ) {
                return new KnotNode( knotLocation, Color.blue, rope.getFullBounds() );
            }
        } );
        redKnots = ImageMetrics.redKnots.map( new F<Double, KnotNode>() {
            @Override public KnotNode f( final Double knotLocation ) {
                return new KnotNode( knotLocation, Color.red, rope.getFullBounds() );
            }
        } );

        addChildren( blueKnots.append( redKnots ) );
    }

    private Vector2D reflect( final Vector2D position, final double width ) {
        double distanceFromCenter = STAGE_SIZE.width / 2 - position.x;
        double newX = STAGE_SIZE.width / 2 + distanceFromCenter - width;
        return new Vector2D( newX, position.y );
    }

    public static PullerNode puller( PColor color, PSize size, final double scale, final Vector2D v, PullerContext context ) {
        return new PullerNode( color, size, 0, scale, v, context );
    }

    public void drag( final PullerNode pullerNode ) {
        //find closest knot node
        List<KnotNode> knots = pullerNode.color == BLUE ? blueKnots : redKnots;
        knots.foreach( new Effect<KnotNode>() {
            @Override public void e( final KnotNode knotNode ) {
                knotNode.setHighlighted( false );
            }
        } );
        List<KnotNode> free = knots.filter( _free ).filter( new F<KnotNode, Boolean>() {
            @Override public Boolean f( final KnotNode knotNode ) {
                return knotPullerDistance( knotNode, pullerNode ) < 80;
            }
        } );
        if ( free.length() > 0 ) {
            KnotNode closest = free.minimum( FJUtils.ord( new F<KnotNode, Double>() {
                @Override public Double f( final KnotNode k ) {
                    return knotPullerDistance( k, pullerNode );
                }
            } ) );
            closest.setHighlighted( true );
        }
    }

    private double knotPullerDistance( final KnotNode k, final PullerNode p ) {return k.getGlobalFullBounds().getCenter2D().distance( p.getGlobalAttachmentPoint() );}

    public static enum PColor {BLUE, RED}

    public static enum PSize {SMALL, MEDIUM, LARGE}
}