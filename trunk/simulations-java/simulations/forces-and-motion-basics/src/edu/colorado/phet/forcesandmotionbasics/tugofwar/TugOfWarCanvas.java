package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.forcesandmotionbasics.AbstractForcesAndMotionBasicsCanvas;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;

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

        ControlPanelNode controlPanelNode = new ControlPanelNode(
                new VBox( 2, VBox.LEFT_ALIGNED,

                          //Nudge "show" to the right so it will align with checkboxes
                          new HBox( 5, new PhetPPath( new Rectangle2D.Double( 0, 0, 0, 0 ) ), new PhetPText( "Show", CONTROL_FONT ) ),
                          new PSwing( new JCheckBox( "Values" ) {{setFont( CONTROL_FONT );}} ), new PSwing( new JCheckBox( "Sum of Forces" ) {{
                    setFont( CONTROL_FONT );
                }} ) ), new Color( 227, 233, 128 ), new BasicStroke( 2 ), Color.black );
        controlPanelNode.setOffset( STAGE_SIZE.width - controlPanelNode.getFullWidth() - INSET, INSET );
        addChild( controlPanelNode );

        PImage cart = new PImage( Images.CART );
        cart.setOffset( STAGE_SIZE.width / 2 - cart.getFullBounds().getWidth() / 2, grassY - cart.getFullBounds().getHeight() + 4 );


        PImage rope = new PImage( Images.ROPE );
        rope.setOffset( STAGE_SIZE.width / 2 - rope.getFullBounds().getWidth() / 2, cart.getFullBounds().getCenterY() - rope.getFullBounds().getHeight() / 2 );

        addChild( rope );
        addChild( cart );
    }
}