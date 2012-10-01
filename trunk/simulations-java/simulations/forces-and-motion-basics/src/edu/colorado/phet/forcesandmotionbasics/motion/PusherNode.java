// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.data.List;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images.PUSHER_STRAIGHT_ON;
import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.RESOURCES;
import static java.lang.Math.round;

/**
 * Node for tabs 2-3 that represents the character that pushes the object.  He is non-interactive, just an animated display.
 * This class is complex because the pusher has several modes depending on the model: standing, pushing, fallen (each in both directions).
 * Pushing has several degrees of gradation to show a stronger push.
 *
 * @author Sam Reid
 */
public class PusherNode extends PNode {

    //record the last direction of the push so that the pusher will stand in the right location.  For example:
    //After pushing left, pusher should stand to the right of the object.
    //This is initialized to a positive value because the pusher should stand to the left of the skateboard on initialization
    private double lastNonzeroAppliedForce = 1E-6;
    private final PImage pusher;
    private final DoubleProperty appliedForce;

    private BufferedImage pusherFallDownRight = flipX( Images.PUSHER_FALL_DOWN );
    private BufferedImage pusherFallDownLeft = Images.PUSHER_FALL_DOWN;

    public PusherNode( final BooleanProperty fallen, final PNode skateboard, final double grassY, final DoubleProperty appliedForce, final Property<List<StackableNode>> stack, final ObservableProperty<SpeedValue> speedValue ) {
        this.appliedForce = appliedForce;
        pusher = new PImage( PUSHER_STRAIGHT_ON );
        pusher.scale( 0.8 * 0.9 );
        addChild( pusher );

        final SimpleObserver update = new SimpleObserver() {
            public void update() {
                double appliedForce = PusherNode.this.appliedForce.get();
                if ( appliedForce != 0 ) {
                    lastNonzeroAppliedForce = appliedForce;
                }

                if ( appliedForce == 0 ) {
                    pusher.setImage( fallen.get() ? ( lastNonzeroAppliedForce > 0 ? pusherFallDownRight : pusherFallDownLeft ) :
                                     PUSHER_STRAIGHT_ON );

                    if ( lastNonzeroAppliedForce > 0 ) {
                        pusher.setOffset( skateboard.getFullBounds().getX() - pusher.getFullBounds().getWidth() + 0, grassY - pusher.getFullBounds().getHeight() );
                    }
                    else {
                        pusher.setOffset( skateboard.getFullBounds().getMaxX(), grassY - pusher.getFullBounds().getHeight() );
                    }
                }
                else {
                    if ( stack.get().length() > 0 ) {
                        final int offset = stack.get().index( 0 ).pusherOffset;
                        final BufferedImage image = RESOURCES.getImage( "pusher_" + getImageIndex( appliedForce ) + ".png" );
                        if ( appliedForce > 0 ) {
                            pusher.setImage( image );
                            //translate right another 15 for crate
                            pusher.setOffset( skateboard.getFullBounds().getX() - pusher.getFullBounds().getWidth() + offset, grassY - pusher.getFullBounds().getHeight() );
                        }
                        else {

                            //TODO: cache this buffered image op
                            pusher.setImage( flipX( image ) );
                            pusher.setOffset( skateboard.getFullBounds().getMaxX() - offset, grassY - pusher.getFullBounds().getHeight() );
                        }
                    }
                }
            }
        };
        appliedForce.addObserver( update );
        speedValue.addObserver( update );
        fallen.addObserver( update );
    }

    private int getImageIndex( final Double appliedForce ) {
        int maxImageIndex = 14;
        return (int) round( Math.abs( appliedForce ) / 100.0 * ( maxImageIndex - 0.5 ) );
    }
}