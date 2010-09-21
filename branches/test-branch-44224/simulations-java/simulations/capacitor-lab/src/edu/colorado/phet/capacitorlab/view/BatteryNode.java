/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.control.VoltageSliderNode;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
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
    
    private final Battery battery;
    private final PImage imageNode;
    private final VoltageSliderNode sliderNode;
    
    public BatteryNode( Battery battery, DoubleRange voltageRange ) {
        
        this.battery = battery;
        battery.addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void voltageChanged() {
                updateNode();
            }
        });
        
        imageNode = new PImage( CLImages.BATTERY_UP );
        addChild( imageNode );
        
        sliderNode = new VoltageSliderNode( voltageRange );
        addChild( sliderNode );
        sliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateModel();
            }
        });
        
        // layout
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = -imageNode.getFullBoundsReference().getHeight() / 2;
        imageNode.setOffset( x, y );
        x = imageNode.getXOffset() + ( imageNode.getFullBoundsReference().getWidth() - sliderNode.getFullBoundsReference().getWidth() ) / 2; // horizontally centered
        y = imageNode.getYOffset() + 60; // set by visual inspection, depends on images
        sliderNode.setOffset( x, y );
        
        updateNode();
    }
    
    private void updateNode() {
        if ( battery.getVoltage() >= 0 ) {
            imageNode.setImage( CLImages.BATTERY_UP );
        }
        else {
            imageNode.setImage( CLImages.BATTERY_DOWN );
        }
    }
    
    private void updateModel() {
        battery.setVoltage( sliderNode.getVoltage() );
    }

}
