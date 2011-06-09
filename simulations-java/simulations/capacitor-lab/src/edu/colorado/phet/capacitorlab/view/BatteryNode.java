// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.control.VoltageSliderNode;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Visual representation of a DC battery, with a control for setting its voltage.
 * Image flips when the polarity of the voltage changes.
 * Origin is at center of this node's bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryNode extends PhetPNode {

    public BatteryNode( final Battery battery, DoubleRange voltageRange ) {

        // battery image, scaled to match model dimensions
        final PImage imageNode = new PImage( CLImages.BATTERY_UP );
        addChild( imageNode );

        // voltage slider
        double trackLength = 0.60 * imageNode.getFullBoundsReference().getHeight();
        final VoltageSliderNode sliderNode = new VoltageSliderNode( battery, voltageRange, trackLength );
        addChild( sliderNode );

        // layout
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = -imageNode.getFullBoundsReference().getHeight() / 2;
        imageNode.setOffset( x, y );
        x = imageNode.getXOffset() + ( ( imageNode.getFullBoundsReference().getWidth() - sliderNode.getFullBoundsReference().getWidth() ) / 2 ) + 5; // sort of centered
        y = imageNode.getYOffset() + 53; // set by visual inspection, depends on images
        sliderNode.setOffset( x, y );

        // when battery polarity changes, change the battery image
        battery.addPolarityObserver( new SimpleObserver() {
            public void update() {
                if ( battery.getPolarity() == Polarity.POSITIVE ) {
                    imageNode.setImage( CLImages.BATTERY_UP );
                }
                else {
                    imageNode.setImage( CLImages.BATTERY_DOWN );
                }
            }
        } );
    }

    // This method must be called if the model element has a longer lifetime than the view.
    public void cleanup() {
        //FUTURE battery.removePolarityObserver
    }
}
