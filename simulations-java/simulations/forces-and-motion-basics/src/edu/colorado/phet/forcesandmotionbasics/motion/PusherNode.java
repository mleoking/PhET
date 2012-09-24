// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class PusherNode extends PNode {
    public PusherNode( final PNode skateboard, final double grassY, final DoubleProperty appliedForce ) {
        final PImage pusher = new PImage( Images.PUSHER_STRAIGHT_ON );
        pusher.scale( 0.8 );
        addChild( pusher );

        appliedForce.addObserver( new VoidFunction1<Double>() {
            public void apply( final Double appliedForce ) {
                if ( appliedForce == 0 ) {
                    pusher.setImage( Images.PUSHER_STRAIGHT_ON );
                }
                else {
                    int index = getImageIndex( appliedForce );
                    pusher.setImage( ForcesAndMotionBasicsResources.RESOURCES.getImage( "pusher_" + index + ".png" ) );
                }
                //translate right another 15 for crate
                pusher.setOffset( skateboard.getFullBounds().getX() - pusher.getFullBounds().getWidth() + 33, grassY - pusher.getFullBounds().getHeight() );
            }
        } );
    }

    private int getImageIndex( final Double appliedForce ) {
        int maxImageIndex = 14;
        return (int) Math.round( Math.abs( appliedForce ) / 100.0 * ( maxImageIndex - 0.5 ) );
    }
}