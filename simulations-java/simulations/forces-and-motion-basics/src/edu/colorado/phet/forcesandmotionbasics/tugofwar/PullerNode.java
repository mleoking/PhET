// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize;
import edu.umd.cs.piccolo.PNode;
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
    private final PSize size;
    public final double scale;
    public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
    private KnotNode knot;
    public Double force;
    public static F<PullerNode, Double> _weight = new F<PullerNode, Double>() {
        @Override public Double f( final PullerNode pullerNode ) {
            return pullerNode.size == PSize.SMALL ? 10.0 :
                   pullerNode.size == PSize.MEDIUM ? 20.0 :
                   pullerNode.size == PSize.LARGE ? 30.0 :
                   null;
        }
    };

    public PullerNode( final PColor color, final PSize size, int item, final double scale, Vector2D offset, final PullerContext context ) {
        this.color = color;
        this.size = size;
        this.scale = scale;
        final BufferedImage image = ForcesAndMotionBasicsResources.RESOURCES.getImage( "pull_figure_" + sizeText( size ) + color.name() + "_" + item + ".png" );
        addChild( new PImage( image ) );
        setScale( scale );
        setOffset( offset.x, offset.y );
        initialOffset = offset;

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                context.startDrag( PullerNode.this );
            }

            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( PullerNode.this.getParent() );
                PullerNode.this.translate( delta.width / PullerNode.this.getScale(), delta.height / PullerNode.this.getScale() );
                context.drag( PullerNode.this );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( PullerNode.this );
            }
        } );

        final double w = 10;
        attachmentNode = new PhetPPath( new Ellipse2D.Double( -w / 2, -w / 2, w, w ), new BasicStroke( 2 ), TRANSPARENT ) {{
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

    public void animateHome() {
        animateToPositionScaleRotation( initialOffset.x, initialOffset.y, scale, 0, TugOfWarCanvas.ANIMATION_DURATION );
    }

    public void setKnot( final KnotNode knot ) {
        this.knot = knot;
    }

    public KnotNode getKnot() {
        return knot;
    }

    public double getForce() {
        return size == PSize.SMALL ? 10 :
               size == PSize.MEDIUM ? 20 :
               size == PSize.LARGE ? 30 :
               Double.NaN;
    }

}