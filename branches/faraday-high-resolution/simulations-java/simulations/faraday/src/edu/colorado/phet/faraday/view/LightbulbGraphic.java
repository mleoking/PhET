/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.util.VariableAlphaImageGraphic;


/**
 * LightBulbGraphic is the graphical representation of a lightbulb.
 * The bulb's relative intensity can be set.
 * Registration point is at bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LightbulbGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // These parameters affect drawing order.
    private static final double BULB_LAYER = 1;
    private static final double RAYS_LAYER = 2;
    
    
    private static final double BULB_RADIUS = 30.0; // Radius of the glass, must be aligned with image by trial & error.
    private static final int DISTANCE_BULB_IS_SCREWED_INTO_BASE = 10; // must be aligned with rays via trial & error
    
    // Range of alpha modulation for bulb image (alpha range is 0-1)
    private static final float GLASS_MIN_ALPA = 0.25f;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Lightbulb _lightbulbModel;
    private double _previousIntensity;
    private VariableAlphaImageGraphic _glassGraphic;
    private LightRaysGraphic _raysGraphic;
    private double _glassMinAlpha;
    private double _glassGlowScale;
    
    //----------------------------------------------------------------------------
    // Constructors
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

        // Glass
        BufferedImage glassImage = FaradayResources.getImage( FaradayConstants.LIGHTBULB_GLASS_IMAGE );
        _glassGraphic = new VariableAlphaImageGraphic( component, glassImage );
        addGraphic( _glassGraphic, BULB_LAYER );
        
        // Cap
        BufferedImage capImage = FaradayResources.getImage( FaradayConstants.LIGHTBULB_CAP_IMAGE );
        PhetImageGraphic capGraphic = new PhetImageGraphic( component, capImage );
        addGraphic( capGraphic, BULB_LAYER );
        
        // Base
        BufferedImage baseImage = FaradayResources.getImage( FaradayConstants.LIGHTBULB_BASE_IMAGE );
        PhetImageGraphic baseGraphic = new PhetImageGraphic( component, baseImage );
        addGraphic( baseGraphic, BULB_LAYER );
        
        // Locations
        int rx = baseGraphic.getImage().getWidth() / 2;
        int ry = baseGraphic.getImage().getHeight();
        baseGraphic.setRegistrationPoint( rx, ry );
        rx = capGraphic.getImage().getWidth() / 2;
        ry = capGraphic.getImage().getHeight();
        capGraphic.setRegistrationPoint( rx, ry );
        capGraphic.setLocation( 0, -( baseGraphic.getImage().getHeight() - DISTANCE_BULB_IS_SCREWED_INTO_BASE ) );
        rx = _glassGraphic.getImage().getWidth() / 2;
        ry = _glassGraphic.getImage().getHeight();
        _glassGraphic.setRegistrationPoint( rx, ry );
        _glassGraphic.setLocation( 0, -( baseGraphic.getImage().getHeight() + capGraphic.getImage().getHeight() - DISTANCE_BULB_IS_SCREWED_INTO_BASE ) );
        
        // Rays
        _raysGraphic = new LightRaysGraphic( component, BULB_RADIUS );
        addGraphic( _raysGraphic, RAYS_LAYER );
        _raysGraphic.setRegistrationPoint( 0, 90 );
        
        _glassMinAlpha = GLASS_MIN_ALPA;
        _glassGlowScale = 1.0;
        
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _lightbulbModel.removeObserver( this );
        _lightbulbModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the minimum alpha compositing value for the glass.
     * This is the value used when the lightbulb's intensity is zero.
     * 
     * @param minAlpha
     */
    public void setGlassMinAlpha( double minAlpha ) {
        if ( minAlpha < 0 || minAlpha > 1 ) {
            throw new IllegalArgumentException( "0 <= a <= 1 required: " + minAlpha );
        }
        if ( minAlpha != _glassMinAlpha ) {
            _glassMinAlpha = minAlpha;
            forceUpdate();
        }
    }

    /**
     * Gets the minimum alpha compositing value for the glass.
     * This is the value used when the lightbulb's intensity is zero.
     * 
     * @return
     */
    public double getGlassMinAlpha() {
        return _glassMinAlpha;
    }
    
    /**
     * Sets the scaling factor that determines how much the bulb glows.
     * Larger values will cause the bulb to reach it's maximum glow sooner.
     * 
     * @param scale
     */
    public void setGlassGlowScale( double scale ) {
        if ( scale < 0 ) {
            throw new IllegalArgumentException( "scale must be >= 0: " + scale );
        }
        if ( scale != _glassGlowScale ) {
            _glassGlowScale = scale;
            forceUpdate();
        }
    }
    
    /**
     * Gets the scaling factor that determines how much the bulb glows.
     * Larger values will cause the bulb to reach it's maximum glow sooner.
     * 
     * @return
     */
    public double getGlassGlowScale() {
        return _glassGlowScale;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    private void forceUpdate() {
        _previousIntensity = -1;
        update();
    }
    
    /**
     * Synchronize the view with the model.
     * 
     * @see edu.colorado.phet.common.phetcommon.util.SimpleObserver#update()
     */
    public void update() {
        
        setVisible( _lightbulbModel.isEnabled() );
        
        if ( isVisible() ) {
            
            // Get the light intensity, a value in the range 0...+1.
            double intensity = _lightbulbModel.getIntensity();

            if ( intensity != _previousIntensity ) {
                
                _raysGraphic.setIntensity( intensity );
                
                // modulate alpha channel of the glass to make it appear to glow
                float alpha = (float)( _glassMinAlpha + ( _glassGlowScale * ( 1f - _glassMinAlpha ) * (float)intensity ) );
                if ( alpha > 1f ) {
                    alpha = 1f;
                }
                _glassGraphic.setAlpha( alpha );
                
                _previousIntensity = intensity;
                setBoundsDirty();
                repaint(); 
            }
        }
        
    }
}
