/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.control.BatterySliderNode;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeListener;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Visual representation of a DC battery, with a control for setting its voltage.
 * Image flips when the polarity of the voltage changes.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryNode extends PhetPNode {
    
    private final Battery battery;
    private final PImage image;
    private final BatterySliderNode sliderNode;
    
    public BatteryNode( Battery battery, DoubleRange voltageRange ) {
        
        this.battery = battery;
        battery.addBatteryChangeListener( new BatteryChangeListener() {
            public void voltageChanged( double oldVoltage, double newVoltage ) {
                updateNode();
            }
        });
        
        image = new PImage( CLImages.BATTERY_UP );
        addChild( image );
        
        sliderNode = new BatterySliderNode( voltageRange );
        addChild( sliderNode );
        
        updateNode();
    }
    
    private void updateNode() {
        if ( battery.getVoltage() >= 0 ) {
            image.setImage( CLImages.BATTERY_UP );
        }
        else {
            image.setImage( CLImages.BATTERY_DOWN );
        }
    }
    
    private void updateModel() {
        
    }

}
