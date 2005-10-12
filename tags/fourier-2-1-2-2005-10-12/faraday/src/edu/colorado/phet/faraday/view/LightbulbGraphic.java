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
import edu.colorado.phet.faraday.model.Lightbulb;


/**
 * LightBulbGraphic is the graphical representation of a lightbulb.
 * The bulb's relative intensity can be set.
 * Registration point is at bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightbulbGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // These parameters affect drawing order.
    private static final double BULB_LAYER = 1;
    private static final double RAYS_LAYER = 2;
    
    // Radius of the lightbulb in the lightbulb graphic.
    private static final double BULB_RADIUS = 30.0;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Lightbulb _lightbulbModel;
    private double _previousIntensity;
    private LightRaysGraphic _raysGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param lightbulbModel
     */
    public LightbulbGraphic( Component component, Lightbulb lightbulbModel ) {
        super( component );
        
        assert( component != null );
        assert( lightbulbModel !=  null );
        
        _lightbulbModel = lightbulbModel;
        _lightbulbModel.addObserver( this );

        // Lightbulb
        {
            PhetImageGraphic lightBulbGraphic = new PhetImageGraphic( component, FaradayConfig.LIGHTBULB_IMAGE );
            addGraphic( lightBulbGraphic, BULB_LAYER );
            int rx = lightBulbGraphic.getImage().getWidth() / 2;
            int ry = lightBulbGraphic.getImage().getHeight();
            lightBulbGraphic.setRegistrationPoint( rx, ry );
        }
        
        // Rays
        {
            _raysGraphic = new LightRaysGraphic( component, BULB_RADIUS );
            addGraphic( _raysGraphic, RAYS_LAYER );
            _raysGraphic.setRegistrationPoint( 0, 90 );
        }
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _lightbulbModel.removeObserver( this );
        _lightbulbModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Synchronize the view with the model.
     * 
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        setVisible( _lightbulbModel.isEnabled() );
        
        if ( isVisible() ) {
            
            // Get the light intensity, a value in the range 0...+1.
            double intensity = _lightbulbModel.getIntensity();

            if ( intensity != _previousIntensity ) {
                _raysGraphic.setIntensity( intensity );
                _previousIntensity = intensity;
                setBoundsDirty();
                repaint(); 
            }
        }
        
    } // update
}
