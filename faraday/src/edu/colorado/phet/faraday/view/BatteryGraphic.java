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
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.FSlider;
import edu.colorado.phet.faraday.model.Battery;


/**
 * BatteryGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BatteryGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Battery _batteryModel;
    private PhetImageGraphic _batteryGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors and finalizers
    //----------------------------------------------------------------------------
    
    public BatteryGraphic( Component component, Battery batteryModel ) {
        super( component );
        
        _batteryModel = batteryModel;
        _batteryModel.addObserver( this );
        
        // Battery image
        {
            _batteryGraphic = new PhetImageGraphic( component, FaradayConfig.BATTERY_IMAGE );
            addGraphic( _batteryGraphic );

            // Registration point is the bottom center of the image.
            int rx = _batteryGraphic.getImage().getWidth() / 2;
            int ry = _batteryGraphic.getImage().getHeight();
            _batteryGraphic.setRegistrationPoint( rx, ry );
        }
        
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
            
            _batteryGraphic.clearTransform();
            
            double voltage = _batteryModel.getVoltage();
            if ( voltage < 0 ) {
               _batteryGraphic.scale( -1, 1 );  // horizontal reflection to indicate voltage polarity
            }
            
            repaint();
        }
    }
}
