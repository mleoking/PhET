// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class PullerNode extends PNode {
    private final Vector2D initialOffset;
    public final PColor color;
    private final PhetPPath attachmentNode;

    public PullerNode( final PColor color, final PSize size, int item, final double scale, Vector2D offset, final PullerContext context ) {
        this.color = color;
        final BufferedImage image = ForcesAndMotionBasicsResources.RESOURCES.getImage( "pull_figure_" + sizeText( size ) + color.name() + "_" + item + ".png" );
        addChild( new PImage( image ) );
        setScale( scale );
        setOffset( offset.x, offset.y );
        initialOffset = offset;

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( final PInputEvent event ) {
                super.mouseDragged( event );
                final PDimension delta = event.getDeltaRelativeTo( PullerNode.this.getParent() );
                PullerNode.this.translate( delta.width / PullerNode.this.getScale(), delta.height / PullerNode.this.getScale() );
//                System.out.println( event.getPickedNode().getOffset() );
                context.drag( PullerNode.this );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                PullerNode.this.animateToPositionScaleRotation( initialOffset.x, initialOffset.y, scale, 0, TugOfWarCanvas.ANIMATION_DURATION );
            }
        } );

        final double w = 10;
        attachmentNode = new PhetPPath( new Ellipse2D.Double( -w / 2, -w / 2, w, w ), new BasicStroke( 2 ), Color.green ) {{
            setOffset( color == PColor.BLUE ?
                       image.getWidth() - w / 2 :
                       w / 2, image.getHeight() - 100 );
        }};
        addChild( attachmentNode );
    }

    private static String sizeText( final PSize size ) {
        return size == PSize.LARGE ? "lrg_" :
               size == PSize.SMALL ? "small_" :
               "";
    }

    public Point2D getGlobalAttachmentPoint() {
        return attachmentNode.getGlobalFullBounds().getCenter2D();
    }

}