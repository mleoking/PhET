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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.FaradaySlider;
import edu.colorado.phet.faraday.control.GraphicSlider;
import edu.colorado.phet.faraday.model.Battery;


/**
 * BatteryGraphic is the graphical representation of a battery.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BatteryGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BATTERY_LAYER = 1;
    private static final double SLIDER_LAYER = 2;
    private static final double VALUE_LAYER = 3;
    
    private static final Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Point VALUE_POSITIVE_LOCATION = new Point( 158, 83 );
    private static final Point VALUE_NEGATIVE_LOCATION = new Point( 55, 83 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Battery _batteryModel;
    private PhetImageGraphic _batteryGraphic;
    private GraphicSlider _amplitudeSlider;
    private PhetTextGraphic _amplitudeValue;
    
    //----------------------------------------------------------------------------
    // Constructors and finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent component
     * @param batteryModel the battery that this graphic represents
     */
    public BatteryGraphic( Component component, Battery batteryModel ) {
        super( component );
        
        _batteryModel = batteryModel;
        _batteryModel.addObserver( this );
        
        // Battery image
        {
            _batteryGraphic = new PhetImageGraphic( component, FaradayConfig.BATTERY_IMAGE );
            addGraphic( _batteryGraphic, BATTERY_LAYER );
 
            // Registration point at top center.
            int rx = _batteryGraphic.getWidth() / 2;
            int ry = 0;
            _batteryGraphic.setRegistrationPoint( rx, ry );
            
            _batteryGraphic.setLocation( rx, 0 );
        }
        
        // Amplitude slider
        {
            _amplitudeSlider = new FaradaySlider( component, 100 /* track length */ );            
            addGraphic( _amplitudeSlider, SLIDER_LAYER );
            
            _amplitudeSlider.centerRegistrationPoint();
            _amplitudeSlider.setLocation( _batteryGraphic.getWidth() / 2, 42 );
            _amplitudeSlider.setMinimum( (int) -( _batteryModel.getMaxVoltage() ) );
            _amplitudeSlider.setMaximum( (int) ( _batteryModel.getMaxVoltage() ) );
            _amplitudeSlider.setValue( (int) ( _batteryModel.getAmplitude() * _batteryModel.getMaxVoltage() ) );
            _amplitudeSlider.addChangeListener( new SliderListener() );
        }
        
        // Amplitude value
        {
            _amplitudeValue = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( _amplitudeValue, VALUE_LAYER );
        }
        
        // Registration point is the bottom center.
        int rx = getWidth() / 2;
        int ry = getHeight();
        setRegistrationPoint( rx, ry );
        
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
            
            double voltage = _batteryModel.getVoltage();
            
            // Update the battery orientation.
            {
                _batteryGraphic.clearTransform();
                if ( voltage < 0 ) {
                    _batteryGraphic.scale( -1, 1 ); // horizontal reflection to indicate voltage polarity
                }
            }
            
            // Update the displayed value.
            {
                // Format the text
                Object[] args = { new Double( Math.abs(voltage) ) };
                String text = MessageFormat.format( SimStrings.get( "BatteryGraphic.voltage" ), args );
                _amplitudeValue.setText( text );

                // Move the voltage label to the positive end of the battery
                if ( voltage < 0 ) {
                    _amplitudeValue.setLocation( VALUE_NEGATIVE_LOCATION );
                }
                else {
                    _amplitudeValue.setLocation( VALUE_POSITIVE_LOCATION );
                }
                
                // Right justify
                int rx = _amplitudeValue.getBounds().width;
                int ry = _amplitudeValue.getBounds().height;
                _amplitudeValue.setRegistrationPoint( rx, ry );
            }
            
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * SliderListener hadnles changes to the amplitude slider.
     */
    private class SliderListener implements ChangeListener {
        
        /** Sole constructor */
        public SliderListener() {
            super();
        }

        /**
         * Handles amplitude slider changes.
         * 
         * @param event the event
         */
        public void stateChanged( ChangeEvent event ) {  
            if ( event.getSource() == _amplitudeSlider ) {
                // Read the value.
                int voltage = _amplitudeSlider.getValue();
                // Update the model.
                _batteryModel.setAmplitude( voltage / _batteryModel.getMaxVoltage() );
            }
        }
    }
}
