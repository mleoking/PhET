/* Copyright 2004, University of Colorado */

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

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * LightBulbGraphic is the graphical representation of a lightbulb.
 * The bulb's relative intensity can be set.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulbGraphic extends CompositePhetGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetImageGraphic _lightEmission;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public LightBulbGraphic( Component component ) {
        super( component );
        
        // Light emission
        {
            _lightEmission = new PhetImageGraphic( component, FaradayConfig.LIGHT_EMISSION_IMAGE );
            super.addGraphic( _lightEmission );
            int x = _lightEmission.getImage().getWidth() / 2;
            int y = _lightEmission.getImage().getHeight() / 2;
            _lightEmission.setRegistrationPoint( x, y );
        }
        
        // Light bulb
        {
            PhetImageGraphic lightBulb = new PhetImageGraphic( component, FaradayConfig.LIGHTBULB_IMAGE );
            super.addGraphic( lightBulb );
            int x = lightBulb.getImage().getWidth() / 2;
            int y = 25;
            lightBulb.setRegistrationPoint( x, y );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the intensity of the light.
     * 
     * @param scale 0 for off, 1.0 for full intensity.
     * @throws IllegalArgumentExcecption if scale is out of range
     */
    public void setIntensity( double scale ) {
        if ( scale < 0 || scale > 1 ) {
            throw new IllegalArgumentException( "scale must be between 0 and 1: " + scale );
        }
        _lightEmission.clearTransform();
        _lightEmission.scale( scale );
    }
}
