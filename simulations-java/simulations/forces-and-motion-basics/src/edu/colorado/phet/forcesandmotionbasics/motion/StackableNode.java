package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;

import java.awt.Image;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
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
    public final BooleanProperty onSkateboard = new BooleanProperty( false );
    private final double mass;

    public static F<StackableNode, Boolean> _isOnSkateboard = new F<StackableNode, Boolean>() {
        @Override public Boolean f( final StackableNode stackableNode ) {
            return stackableNode.onSkateboard.get();
        }
    };
    public static F<StackableNode, Double> _mass = new F<StackableNode, Double>() {
        @Override public Double f( final StackableNode stackableNode ) {
            return stackableNode.mass;
        }
    };
    public final int pusherOffset;
    private BufferedImage flippedStackedImage;

    //Remember the last image shown before applied force is set to 0.0 so that the character will keep facing the same direction.
    private Image lastStackedImage;

    public StackableNode( final StackableNodeContext context, final BufferedImage image, final double mass, final int pusherOffset ) {
        this( context, image, mass, pusherOffset, false, image );
    }

    public StackableNode( final StackableNodeContext context, final BufferedImage stackedImage, final double mass, final int pusherOffset, final boolean faceDirectionOfAppliedForce, final BufferedImage toolboxImage ) {
        this.mass = mass;
        this.pusherOffset = pusherOffset;
        this.flippedStackedImage = BufferedImageUtils.flipX( stackedImage );
        lastStackedImage = stackedImage;
        addChild( new PImage( toolboxImage ) {
            {
                onSkateboard.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( final Boolean aBoolean ) {
                        updateImage();
                    }
                } );
                if ( faceDirectionOfAppliedForce ) {
                    context.getAppliedForce().addObserver( new VoidFunction1<Double>() {
                        public void apply( final Double aDouble ) {
                            updateImage();
                        }
                    } );
                }
            }

            private void updateImage() {
                final Image chosenImage = chooseImage();
                if ( onSkateboard.get() && faceDirectionOfAppliedForce && context.getAppliedForce().get() != 0 ) {
                    lastStackedImage = chosenImage;
                }
                setImage( chosenImage );
            }

            private Image chooseImage() {
                if ( !onSkateboard.get() ) {
                    return toolboxImage;
                }
                else {
                    if ( faceDirectionOfAppliedForce ) {
                        if ( context.getAppliedForce().get() > 0 ) { return flippedStackedImage; }
                        else if ( context.getAppliedForce().get() < 0 ) { return stackedImage; }
                        else { return lastStackedImage;}
                    }
                    else {
                        return stackedImage;
                    }
                }
            }
        } );
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
}