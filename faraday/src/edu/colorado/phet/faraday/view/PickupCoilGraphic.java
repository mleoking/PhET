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
import java.awt.Rectangle;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.model.LightBulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.VoltMeter;

/**
 * PickupCoilGraphic is the graphical representation of a pickup coil,
 * with devices (lightbulb and voltmeter ) for displaying the effects of induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _pickupCoilModel;
    private CoilGraphic _coilGraphic;
    private LightBulbGraphic _lightBulbGraphic;
    private VoltMeterGraphic _voltMeterGraphic;
    private CompositePhetGraphic _foreground, _background;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param pickupCoilModel the pickup coil model
     * @param lightBulbModel the lightbulb model
     * @param voltMeterModel the voltmeter model
     */
    public PickupCoilGraphic( 
            Component component, 
            PickupCoil pickupCoilModel, 
            LightBulb lightBulbModel,
            VoltMeter voltMeterModel ) {
        
        assert ( component != null );
        assert ( pickupCoilModel != null );
        assert ( lightBulbModel != null );
        assert ( voltMeterModel != null );
        
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
        
        // Graphics components
        _coilGraphic = new CoilGraphic( component, pickupCoilModel );
        _lightBulbGraphic = new LightBulbGraphic( component, lightBulbModel, pickupCoilModel.getMagnet() );
        _voltMeterGraphic = new VoltMeterGraphic( component, voltMeterModel, pickupCoilModel.getMagnet() );
        
        // Foreground composition
        _foreground = new CompositePhetGraphic( component );
        _foreground.addGraphic( _coilGraphic.getForeground() );
        _foreground.addGraphic( _lightBulbGraphic );
        _foreground.addGraphic( _voltMeterGraphic );
        
        // Background composition
        _background = new CompositePhetGraphic( component );
        _background.addGraphic( _coilGraphic.getBackground() );
        
        // Interactivity
        _foreground.setCursorHand();
        _background.setCursorHand();
        TranslationListener listener = new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                double x = _pickupCoilModel.getX() + e.getDx();
                double y = _pickupCoilModel.getY() + e.getDy();
                _pickupCoilModel.setLocation( x, y );
            }
        };
        _foreground.addTranslationListener( listener );
        _background.addTranslationListener( listener );
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _pickupCoilModel.removeObserver( this );
        _pickupCoilModel = null;
        _coilGraphic.finalize();
        _lightBulbGraphic.finalize();
        _voltMeterGraphic.finalize();
    }
 
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the PhetGraphic that contains the foreground elements of the coil.
     * 
     * @return the foreground graphics
     */
    public PhetGraphic getForeground() {
        return _foreground;
    }
    
    /**
     * Gets the PhetGraphic that contains the background elements of the coil.
     * 
     * @return the background graphics
     */
    public PhetGraphic getBackground() {
        return _background;
    }
    
    /**
     * Gets the coil graphic.
     * This is intended for use in debugging, or for connecting a control panel.
     */
    public CoilGraphic getCoilGraphic() {
        return _coilGraphic;
    }
 
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {

        // Location
        _foreground.setLocation( (int) _pickupCoilModel.getX(), (int) _pickupCoilModel.getY() );
        _background.setLocation( (int) _pickupCoilModel.getX(), (int) _pickupCoilModel.getY() );
        
        // Position the lightbulb and voltmeter at the top of the coil.
        _foreground.clearTransform();
        _background.clearTransform();
        Rectangle bounds = new Rectangle( _coilGraphic.getForeground().getBounds() );
        bounds.union( _coilGraphic.getBackground().getBounds() );
        int x = -10;
        int y = -( bounds.height / 2 ) - 5;
        _lightBulbGraphic.setLocation( x, y );
        _voltMeterGraphic.setLocation( x, y );
        
        // Direction (do this *after* positioning lightbulb and voltmeter!)
        _foreground.rotate( _pickupCoilModel.getDirection() );
        _background.rotate( _pickupCoilModel.getDirection() );
        
        _foreground.repaint();
        _background.repaint();
    }
}