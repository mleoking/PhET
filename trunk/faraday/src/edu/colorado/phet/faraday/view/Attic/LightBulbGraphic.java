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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.LightBulb;


/**
 * LightBulbGraphic is the graphical representation of a lightbulb.
 * The bulb's relative intensity can be set.
 * Registration point is at bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulbGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double EMISSION_LAYER = 0;
    private static final double BULB_LAYER = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LightBulb _lightBulbModel;
    private PhetImageGraphic _lightEmission;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public LightBulbGraphic( Component component, LightBulb lightBulbModel ) {
        super( component );
        
        assert( component != null );
        assert( lightBulbModel !=  null );
        
        _lightBulbModel = lightBulbModel;
        _lightBulbModel.addObserver( this );
       
        // Light bulb
        {
            PhetImageGraphic lightBulb = new PhetImageGraphic( component, FaradayConfig.LIGHTBULB_IMAGE );
            super.addGraphic( lightBulb, BULB_LAYER );
            int rx = lightBulb.getImage().getWidth() / 2;
            int ry = lightBulb.getImage().getHeight();
            lightBulb.setRegistrationPoint( rx, ry );
        }
        
        // Light emission
        {
            _lightEmission = new PhetImageGraphic( component, FaradayConfig.LIGHT_EMISSION_IMAGE );
            super.addGraphic( _lightEmission, EMISSION_LAYER );
            int rx = _lightEmission.getImage().getWidth() / 2;
            int ry = _lightEmission.getImage().getHeight() / 2;
            _lightEmission.setRegistrationPoint( rx, ry );
            _lightEmission.setLocation( 0, -50 );
        }
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _lightBulbModel.removeObserver( this );
        _lightBulbModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        setVisible( _lightBulbModel.isEnabled() );
        if ( isVisible() ) {
            double intensity = _lightBulbModel.getIntensity();
            if ( intensity == 0 ) {
                _lightEmission.setVisible( false );
            }
            else {
                _lightEmission.setVisible( true );
                _lightEmission.clearTransform();
                _lightEmission.scale( intensity );
            }
        }
    }
}
