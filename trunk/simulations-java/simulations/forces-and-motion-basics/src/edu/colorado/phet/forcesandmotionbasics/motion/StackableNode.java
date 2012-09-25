package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.activities.AnimateToScale;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class StackableNode extends PNode {

    private Vector2D initialOffset;
    private final double initialScale;
    private boolean onSkateboard;
    private final double mass;

    public static F<StackableNode, Boolean> _isOnSkateboard = new F<StackableNode, Boolean>() {
        @Override public Boolean f( final StackableNode stackableNode ) {
            return stackableNode.onSkateboard;
        }
    };
    public static F<StackableNode, Double> _mass = new F<StackableNode, Double>() {
        @Override public Double f( final StackableNode stackableNode ) {
            return stackableNode.mass;
        }
    };
    public final int pusherOffset;

    public StackableNode( final StackableNodeContext context, final BufferedImage image, final double mass, final int pusherOffset ) {
        this.mass = mass;
        this.pusherOffset = pusherOffset;
        addChild( new PImage( image ) );
        setScale( 0.8 );
        this.initialScale = getScale();
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                addActivity( new AnimateToScale( 1.0, StackableNode.this, 200 ) );

                context.stackableNodePressed( StackableNode.this );
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                final PDimension delta = event.getDeltaRelativeTo( StackableNode.this.getParent() );
                translate( delta.width, delta.height );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );
                context.stackableNodeDropped( StackableNode.this );
            }
        } );
    }

    public void animateHome() {
        animateToPositionScaleRotation( initialOffset.x, initialOffset.y, initialScale, 0, 200 );
    }

    public void setInitialOffset( final double x, final double y ) {
        this.initialOffset = Vector2D.v( x, y );
        setOffset( x, y );
    }

    public void setOnSkateboard( final boolean onSkateboard ) { this.onSkateboard = onSkateboard; }

    public boolean isOnSkateboard() { return onSkateboard;}
}