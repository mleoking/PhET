package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.forcesandmotionbasics.AbstractForcesAndMotionBasicsCanvas;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.BLUE;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.RED;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize.*;

/**
 * @author Sam Reid
 */
public class TugOfWarCanvas extends AbstractForcesAndMotionBasicsCanvas {
    public TugOfWarCanvas() {

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
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getMaxY() + INSET );
            setConfirmationEnabled( false );
        }} );

        PImage cart = new PImage( Images.CART );
        cart.setOffset( STAGE_SIZE.width / 2 - cart.getFullBounds().getWidth() / 2, grassY - cart.getFullBounds().getHeight() + 4 );


        PImage rope = new PImage( Images.ROPE );
        rope.setOffset( STAGE_SIZE.width / 2 - rope.getFullBounds().getWidth() / 2, cart.getFullBounds().getCenterY() - rope.getFullBounds().getHeight() / 2 );

        addChild( rope );
        addChild( cart );

        final double IMAGE_SCALE = 0.75;
        Vector2D largePosition = Vector2D.v( 88.38995568685374, 488.15361890694203 );
        Vector2D mediumPosition = Vector2D.v( 151.66912850812423, 513.264401772526 );
        Vector2D smallPosition1 = Vector2D.v( 215.9527326440175, 558.463810930576 );
        Vector2D smallPosition2 = Vector2D.v( 263.1610044313148, 559.4682422451999 );
        final PImage largeRedPuller = puller( BLUE, LARGE, IMAGE_SCALE, largePosition );
        addChild( largeRedPuller );
        addChild( puller( BLUE, MEDIUM, IMAGE_SCALE, mediumPosition ) );
        addChild( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition1 ) );
        addChild( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition2 ) );

        final double offset = largeRedPuller.getFullBounds().getWidth();
        addChild( puller( RED, LARGE, IMAGE_SCALE, reflect( largePosition, offset ) ) );
        addChild( puller( RED, MEDIUM, IMAGE_SCALE, reflect( mediumPosition, offset ) ) );
        addChild( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition1, offset ) ) );
        addChild( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition2, offset ) ) );
    }

    private Vector2D reflect( final Vector2D position, final double width ) {
        double distanceFromCenter = STAGE_SIZE.width / 2 - position.x;
        double newX = STAGE_SIZE.width / 2 + distanceFromCenter - width;
        return new Vector2D( newX, position.y );
    }

    public static PImage puller( PColor color, PSize size, final double scale ) {
        return puller( color, size, scale, Vector2D.ZERO );
    }

    public static PImage puller( PColor color, PSize size, final double scale, final Vector2D v ) {
        return new PImage( pullerImage( color, size, 0 ) ) {{
            setScale( scale );
            setOffset( v.x, v.y );
            addInputEventListener( new DebugDragLocation() );
        }};
    }

    private static BufferedImage pullerImage( final PColor color, final PSize size, int item ) {
        return ForcesAndMotionBasicsResources.RESOURCES.getImage( "pull_figure_" + sizeText( size ) + color.name() + "_" + item + ".png" );
    }

    private static String sizeText( final PSize size ) {
        return size == LARGE ? "lrg_" :
               size == SMALL ? "small_" :
               "";
    }

    static enum PColor {BLUE, RED}

    static enum PSize {SMALL, MEDIUM, LARGE}
}