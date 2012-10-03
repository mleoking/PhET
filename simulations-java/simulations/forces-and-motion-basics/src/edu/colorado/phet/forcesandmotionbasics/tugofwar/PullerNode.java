// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.Mode;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Represent all of the red and blue team puller figures that start in the toolbox and can be dragged to the rope.
 *
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
    public static final F<PullerNode, Double> _weight = new F<PullerNode, Double>() {
        @Override public Double f( final PullerNode pullerNode ) {

            //Average human mass is 76-83 kg, so the average human weight is between 745 and 813 Newtons.
            //Read more: http://wiki.answers.com/Q/What_does_an_average_human_weigh_in_newtons#ixzz26k003Tsu
            return pullerNode.size == PSize.SMALL ? 200.0 :
                   pullerNode.size == PSize.MEDIUM ? 400.0 :
                   pullerNode.size == PSize.LARGE ? 800.0 :
                   null;
        }
    };
    public static final F<PullerNode, Boolean> _isBlue = new F<PullerNode, Boolean>() {
        @Override public Boolean f( final PullerNode pullerNode ) {
            return pullerNode.color == PColor.BLUE;
        }
    };
    public static final F<PullerNode, Boolean> _isRed = new F<PullerNode, Boolean>() {
        @Override public Boolean f( final PullerNode pullerNode ) {
            return pullerNode.color == PColor.RED;
        }
    };
    public static final F<PullerNode, PBounds> _getFullBounds = new F<PullerNode, PBounds>() {
        @Override public PBounds f( final PullerNode pullerNode ) {
            return pullerNode.getFullBounds();
        }
    };
    private final SimpleObserver imageUpdater;

    //REVIEW: Why have "item" (which is a vague name anyway) as a parameter if
    //puller nodes are always created in a standing position (i.e. item is
    //always 0)?
    public PullerNode( final IUserComponent component, final PColor color, final PSize size, final int item, final double scale, Vector2D offset, final PullerContext context, final ObservableProperty<Mode> mode ) {
        this.color = color;
        this.size = size;
        this.scale = scale;
        final BufferedImage standingImage = pullerImage( item );
        final PImage imageNode = new PImage( pullerImage( item ) );
        addChild( imageNode );
        imageUpdater = new SimpleObserver() {
            public void update() {
                if ( knot != null && mode.get() == Mode.GOING ) {
                    final BufferedImage pullingImage = pullerImage( 3 ); //REVIEW: Would be clearer if 3 were a constant, like "LAST_PULLER_IMAGE".
                    imageNode.setImage( pullingImage );

                    //Padding accounts for the fact that the hand is no longer at the edge of the image when the puller is pulling, because the foot sticks out
                    double padding = size == PSize.LARGE ? 40 :
                                     size == PSize.MEDIUM ? 20 :
                                     size == PSize.SMALL ? 10 :
                                     Integer.MAX_VALUE;
                    if ( color == PColor.BLUE ) {
                        //align bottom right
                        imageNode.setOffset( standingImage.getWidth() - pullingImage.getWidth() + padding,
                                             standingImage.getHeight() - pullingImage.getHeight() );
                    }
                    else {
                        //align bottom left
                        imageNode.setOffset( 0 - padding,
                                             standingImage.getHeight() - pullingImage.getHeight() );
                    }
                }
                else {
                    imageNode.setImage( standingImage );
                    imageNode.setOffset( 0, 0 );
                }
            }
        };
        mode.addObserver( imageUpdater );

        setScale( scale );
        setOffset( offset.x, offset.y );
        initialOffset = offset;

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( component, true ) {
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
                       standingImage.getWidth() - w / 2 :
                       w / 2,

                       //Set the vertical attachment point here: determines relative vertical position of the puller feet to the grass
                       //Should have front foot slightly above the grass and back foot slightly below
                       standingImage.getHeight() - 100 - 4 );
        }};
        addChild( attachmentNode );

        //Don't allow dragging if the system is moving or completed
        final VoidFunction1<Mode> listener = new VoidFunction1<Mode>() {
            public void apply( final Mode mode ) {
                updatePickable();
            }
        };
        mode.addObserver( listener );
        context.addCartPositionChangeListener( new VoidFunction0() {
            public void apply() {
                updatePickable();
            }
        } );
    }

    private void updatePickable() {
        boolean pickable = true;
        setPickable( pickable );
        setChildrenPickable( pickable );
    }

    //REVIEW: "item" is awfully vague, and it took me a while to figure out
    // that it was the index into the leaning animation sequence.  I would
    // suggest renaming and/or documenting to make this clearer.
    private BufferedImage pullerImage( final int item ) {
        return ForcesAndMotionBasicsResources.RESOURCES.getImage( "pull_figure_" + sizeText( size ) + color.name() + "_" + item + ".png" );
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
        imageUpdater.update();
    }

    public KnotNode getKnot() { return knot; }

    public double getForce() {
        return size == PSize.SMALL ? 10 :
               size == PSize.MEDIUM ? 20 :
               size == PSize.LARGE ? 30 :
               Double.NaN;
    }

}