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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.PickupCoil;


/**
 * LightBulbGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulbGraphic extends CompositePhetGraphic implements SimpleObserver {

    private PickupCoil _pickupCoilModel;
    private PhetImageGraphic _lightEmission;
    
    /**
     * @param component
     */
    public LightBulbGraphic( Component component, PickupCoil pickupCoilModel ) {
        super( component );
        
        _pickupCoilModel = pickupCoilModel;
        
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
        
        update();
    }
    
    public void update() {
        
        if( isVisible() ) {
            //System.out.println( "emf = " + _pickupCoilModel.getEMF() ); // DEBUG
            double emf = Math.abs( _pickupCoilModel.getEMF() );
            double scale = MathUtil.clamp( 0, emf / 2000, 1 );
            _lightEmission.clearTransform();
            _lightEmission.scale( scale );
        }
    }

}
