// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.data.List;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class PusherNode extends PNode {

    //record the last direction of the push so that the pusher will stand in the right location.  For example:
    //After pushing left, pusher should stand to the right of the object.
    //This is initialized to a positive value because the pusher should stand to the left of the skateboard on initialization
    private double lastNonzeroAppliedForce = 1E-6;

    public PusherNode( final PNode skateboard, final double grassY, final DoubleProperty appliedForce, final Property<List<StackableNode>> stack ) {
        final PImage pusher = new PImage( Images.PUSHER_STRAIGHT_ON );
        pusher.scale( 0.8 );
        addChild( pusher );

        appliedForce.addObserver( new VoidFunction1<Double>() {
            public void apply( final Double appliedForce ) {
                if ( appliedForce != 0 ) {
                    lastNonzeroAppliedForce = appliedForce;
                }

                if ( appliedForce == 0 ) {
                    pusher.setImage( Images.PUSHER_STRAIGHT_ON );

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
                        final BufferedImage image = ForcesAndMotionBasicsResources.RESOURCES.getImage( "pusher_" + getImageIndex( appliedForce ) + ".png" );
                        if ( appliedForce > 0 ) {
                            pusher.setImage( image );
                            //translate right another 15 for crate
                            pusher.setOffset( skateboard.getFullBounds().getX() - pusher.getFullBounds().getWidth() + offset, grassY - pusher.getFullBounds().getHeight() );
                        }
                        else {

                            //TODO: cache this buffered image op
                            pusher.setImage( BufferedImageUtils.flipX( image ) );
                            pusher.setOffset( skateboard.getFullBounds().getMaxX() - offset, grassY - pusher.getFullBounds().getHeight() );
                        }
                    }
                }
            }
        } );
    }

    private int getImageIndex( final Double appliedForce ) {
        int maxImageIndex = 14;
        return (int) Math.round( Math.abs( appliedForce ) / 100.0 * ( maxImageIndex - 0.5 ) );
    }
}