/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.dischargelamps.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.text.MessageFormat;
import java.text.DecimalFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.control.BatterySlider;
import edu.colorado.phet.dischargelamps.control.GraphicSlider;
import edu.colorado.phet.dischargelamps.model.Battery;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;


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
    private static final Point VALUE_POSITIVE_LOCATION = new Point( 162, 83 );
    private static final Point VALUE_NEGATIVE_LOCATION = new Point( 60, 83 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Battery _batteryModel;
    private PhetImageGraphic _batteryGraphic;
    private GraphicSlider _amplitudeSlider;
    private PhetTextGraphic _amplitudeValue;
    private DecimalFormat voltageFormat = new DecimalFormat( "#0.0" );

    
    //----------------------------------------------------------------------------
    // Constructors and finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     *
     * @param component    the parent component
     * @param batteryModel the battery that this graphic represents
     */
    public BatteryGraphic( Component component, Battery batteryModel ) {
        super( component );

//        _batteryModel = batteryModel;
//        _batteryModel.addObserver( this );
//
        // Battery image
        {
            _batteryGraphic = new PhetImageGraphic( component, DischargeLampsConfig.BATTERY_IMAGE );
            addGraphic( _batteryGraphic, BATTERY_LAYER );
 
            // Registration point at top center.
            int rx = _batteryGraphic.getWidth() / 2;
            int ry = 0;
            _batteryGraphic.setRegistrationPoint( rx, ry );

            _batteryGraphic.setLocation( rx, 0 );
        }
        
        // Amplitude slider
        {
            _amplitudeSlider = new BatterySlider( component, 100 /* track length */, batteryModel );
            addGraphic( _amplitudeSlider, SLIDER_LAYER );

            _amplitudeSlider.setMinimum( (int)-( batteryModel.getMaxVoltage() ) );
            _amplitudeSlider.setMaximum( (int)( batteryModel.getMaxVoltage() ) );
            _amplitudeSlider.setValue( (int)( batteryModel.getVoltage() * batteryModel.getMaxVoltage() ) );
            _amplitudeSlider.addTick( _amplitudeSlider.getMinimum() );
            _amplitudeSlider.addTick( _amplitudeSlider.getMaximum() );
            _amplitudeSlider.addTick( 0 );

            _amplitudeSlider.centerRegistrationPoint();
            _amplitudeSlider.setLocation( 105, 35 );
//            _amplitudeSlider.addChangeListener( new SliderListener() );
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

        // Now that evertyhing graphic is set up, attach to the battery model
        _batteryModel = batteryModel;
        _batteryModel.addObserver( this );


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
        if( isVisible() ) {

            double voltage = _batteryModel.getVoltage();
            // Update the battery orientation.
            {
                _batteryGraphic.clearTransform();
                if( voltage < 0 ) {
                    _batteryGraphic.scale( -1, 1 ); // horizontal reflection to indicate voltage polarity
                }
            }
            
            // Update the slider position.
            double value = _batteryModel.getVoltage() * _batteryModel.getMaxVoltage();
            if( value != _amplitudeSlider.getValue() ) {
                _amplitudeSlider.setValue( value / DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR );
            }

            // Update the displayed value.
            {
                // Format the text
                Object[] args = {voltageFormat.format( Math.abs( voltage ) * DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR )};
                String text = MessageFormat.format( SimStrings.get( "BatteryGraphic.voltage" ), args );
                _amplitudeValue.setText( text );

                // Move the voltage label to the positive end of the battery
                if( voltage < 0 ) {
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
    
//    //----------------------------------------------------------------------------
//    // Event handling
//    //----------------------------------------------------------------------------
//
//    /**
//     * SliderListener handles changes to the amplitude slider.
//     */
//    private class SliderListener implements ChangeListener {
//
//        /**
//         * Sole constructor
//         */
//        public SliderListener() {
//            super();
//        }
//
//        /**
//         * Handles amplitude slider changes.
//         *
//         * @param event the event
//         */
//        public void stateChanged( ChangeEvent event ) {
//            if( event.getSource() == _amplitudeSlider ) {
//                // Read the value.
//                double voltage = _amplitudeSlider.getValue();
//                // Update the model.
//                _batteryModel.setVoltage( voltage / _batteryModel.getMaxVoltage() * DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR );
//            }
//        }
//    }
}
