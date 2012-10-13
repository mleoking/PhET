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
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PullerColor;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PullerSize;
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
    public final PullerColor color;
    private final PhetPPath attachmentNode;
    private final PullerSize size;
    public final double scale;
    public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
    private final int LAST_PULLER_IMAGE_INDEX = 3;
    private KnotNode knot;
    public static final F<PullerNode, Double> _weight = new F<PullerNode, Double>() {
        @Override public Double f( final PullerNode pullerNode ) {

            //Average human mass is 76-83 kg, so the average human weight is between 745 and 813 Newtons.
            //Read more: http://wiki.answers.com/Q/What_does_an_average_human_weigh_in_newtons#ixzz26k003Tsu
            return pullerNode.size == PullerSize.SMALL ? 400.0 :
                   pullerNode.size == PullerSize.MEDIUM ? 600.0 :
                   pullerNode.size == PullerSize.LARGE ? 1000.0 :
                   null;
        }
    };
    public static final F<PullerNode, Boolean> _isBlue = new F<PullerNode, Boolean>() {
        @Override public Boolean f( final PullerNode pullerNode ) {
            return pullerNode.color == PullerColor.BLUE;
        }
    };
    public static final F<PullerNode, Boolean> _isRed = new F<PullerNode, Boolean>() {
        @Override public Boolean f( final PullerNode pullerNode ) {
            return pullerNode.color == PullerColor.RED;
        }
    };
    public static final F<PullerNode, PBounds> _getFullBounds = new F<PullerNode, PBounds>() {
        @Override public PBounds f( final PullerNode pullerNode ) {
            return pullerNode.getFullBounds();
        }
    };
    private final SimpleObserver imageUpdater;

    public PullerNode( final IUserComponent component, final PullerColor color, final PullerSize size, final double scale, Vector2D offset, final PullerContext context, final ObservableProperty<Mode> mode ) {
        this.color = color;
        this.size = size;
        this.scale = scale;
        final BufferedImage standingImage = pullerImage( 0 );
        final PImage imageNode = new PImage( pullerImage( 0 ) );
        addChild( imageNode );
        imageUpdater = new SimpleObserver() {
            public void update() {
                if ( knot != null && ( mode.get() == Mode.GOING || mode.get() == Mode.PAUSED ) ) {
                    final BufferedImage pullingImage = pullerImage( LAST_PULLER_IMAGE_INDEX );
                    imageNode.setImage( pullingImage );

                    //Padding accounts for the fact that the hand is no longer at the edge of the image when the puller is pulling, because the foot sticks out
                    double padding = size == PullerSize.LARGE ? 40 :
                                     size == PullerSize.MEDIUM ? 20 :
                                     size == PullerSize.SMALL ? 10 :
                                     Integer.MAX_VALUE;
                    if ( color == PullerColor.BLUE ) {
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
            setOffset( color == PullerColor.BLUE ?
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

    private BufferedImage pullerImage( final int imageIndex ) {
        return ForcesAndMotionBasicsResources.RESOURCES.getImage( "pull_figure_" + sizeText( size ) + color.name() + "_" + imageIndex + ".png" );
    }

    private static String sizeText( final PullerSize size ) {
        return size == PullerSize.LARGE ? "lrg_" :
               size == PullerSize.SMALL ? "small_" :
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
        return size == PullerSize.SMALL ? 10 * 5 :
               size == PullerSize.MEDIUM ? 20 * 5 :
               size == PullerSize.LARGE ? 30 * 5 :
               Double.NaN;
    }

}