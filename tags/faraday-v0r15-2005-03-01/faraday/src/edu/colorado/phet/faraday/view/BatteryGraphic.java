/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Battery;


/**
 * BatteryGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BatteryGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Battery _batteryModel;
    
    //----------------------------------------------------------------------------
    // Constructors and finalizers
    //----------------------------------------------------------------------------
    
    public BatteryGraphic( Component component, Battery batteryModel ) {
        super( component, FaradayConfig.BATTERY_IMAGE );
        
        _batteryModel = batteryModel;
        _batteryModel.addObserver( this );
        
        // Registration point is the bottom center of the image.
        setRegistrationPoint( getImage().getWidth() / 2, getImage().getHeight() );
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _batteryModel.removeObserver( this );
    }
 
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {

        setVisible( _batteryModel.isEnabled() );
        if ( isVisible() ) {
            
            clearTransform();
            
            double voltage = _batteryModel.getVoltage();
            if ( voltage < 0 ) {
               scale( -1, 1 );  // horizontal reflection to indicate voltage polarity
            }
            
            scale( 0.25 ); // XXX
            
            repaint();
        }
    }
}
