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
 * VoltMeterGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltMeterGraphic extends CompositePhetGraphic implements SimpleObserver {

    private PhetImageGraphic _body;
    private PhetImageGraphic _needle;
    private PickupCoil _pickupCoilModel;

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public VoltMeterGraphic( Component component, PickupCoil pickupCoilModel ) {
        super( component );

        _pickupCoilModel = pickupCoilModel;

        // Create the graphics components.
        _body = new PhetImageGraphic( component, FaradayConfig.METER_BODY_IMAGE );
        _needle = new PhetImageGraphic( component, FaradayConfig.METER_NEEDLE_IMAGE );
        super.addGraphic( _body );
        super.addGraphic( _needle );

        // Set the needle's registration point.
        {
            int w = _needle.getImage().getWidth();
            int h = _needle.getImage().getHeight();
            int x = w / 2;
            int y = h - 5;
            _needle.setRegistrationPoint( x, y );
        }

        // Scale everything.
        super.scale( 0.3 );

        // Synchronize view with model.
        update();
    }

    public void update() {
        if( isVisible() ) {
            _needle.clearTransform();

            // Determine needle deflection based on EMF.
            double emf = _pickupCoilModel.getEMF();
            double angle = MathUtil.clamp( -90, (int) ( 90 * ( emf / 2000 ) ), 90 );
            _needle.rotate( Math.toRadians( angle ) );

            // Position the needle relative to the meter.
            {
                int w = _body.getImage().getWidth();
                int h = _body.getImage().getHeight();
                int x = w / 2 + 2;
                int y = h - 70;
                _needle.translate( x, y );
            }
        }
    }
}