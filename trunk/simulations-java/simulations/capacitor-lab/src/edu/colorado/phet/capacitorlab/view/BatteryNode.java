/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
        final VoltageSliderNode sliderNode = new VoltageSliderNode( voltageRange, trackLength );
        addChild( sliderNode );
        sliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                battery.setVoltage( sliderNode.getVoltage() );
            }
        });
        
        // layout
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = -imageNode.getFullBoundsReference().getHeight() / 2;
        imageNode.setOffset( x, y );
        x = imageNode.getXOffset() + ( imageNode.getFullBoundsReference().getWidth() - sliderNode.getFullBoundsReference().getWidth() ) / 2; // horizontally centered
        y = imageNode.getYOffset() + 60; // set by visual inspection, depends on images
        sliderNode.setOffset( x, y );
        
        // observe model
        battery.addVoltageObserver( new SimpleObserver() {
            public void update() {
                sliderNode.setVoltage( battery.getVoltage() );
            }
        } );
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
}
