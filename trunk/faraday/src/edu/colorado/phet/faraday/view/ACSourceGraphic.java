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
import edu.colorado.phet.faraday.model.ACSource;


/**
 * ACSourceGraphic is the graphical representation of an alternating current source.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACSourceGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BOX_LAYER = 1;
    private static final double SLIDER_LAYER = 2;
    private static final double VALUE_LAYER = 3;
    
    private static final Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    private static final Color VALUE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ACSource _acSourceModel;
    private PhetImageGraphic _acBoxGraphic;
    private FaradaySlider _amplitudeSlider;
    private FaradaySlider _frequencySlider;
    private PhetTextGraphic _amplitudeValue;
    private PhetTextGraphic _frequencyValue;
    private String _amplitudeFormat;
    private String _frequencyFormat;
    
    //----------------------------------------------------------------------------
    // Constructors and finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param acSourceModel
     */
    public ACSourceGraphic( Component component, ACSource acSourceModel ) {
        
        super( component );
        
        assert( component != null );
        assert( acSourceModel != null );
        
        _acSourceModel = acSourceModel;
        _acSourceModel.addObserver( this );
        
        // AC panel
        {
            _acBoxGraphic = new PhetImageGraphic( component, FaradayConfig.AC_SOURCE_IMAGE );
            addGraphic( _acBoxGraphic, BOX_LAYER );
        }
        
        // Amplitude slider
        {
            _amplitudeSlider = new FaradaySlider( component, 100 /* track length */ );            
            addGraphic( _amplitudeSlider, SLIDER_LAYER );
            
            _amplitudeSlider.centerRegistrationPoint();
            _amplitudeSlider.rotate( -Math.PI / 2 );  // rotate -90 degrees
            _amplitudeSlider.setLocation( 40, ( _acBoxGraphic.getHeight() / 2 ) + 5 );
            _amplitudeSlider.setMinimum( (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MIN ) );
            _amplitudeSlider.setMaximum( (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MAX ) );
            _amplitudeSlider.setValue( (int) ( 100.0 * _acSourceModel.getMaxAmplitude() ) );
            _amplitudeSlider.addChangeListener( new SliderListener() );
        }
        
        // Amplitude value
        {
            _amplitudeValue = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( _amplitudeValue, VALUE_LAYER );            
            _amplitudeValue.setLocation( 50, 45 );
            
            _amplitudeFormat = SimStrings.get( "ACSourceGraphic.amplitude" );
        }
        
        // Frequency slider
        {
            _frequencySlider = new FaradaySlider( component, 100 /* track length */ );            
            addGraphic( _frequencySlider, SLIDER_LAYER );
            
            _frequencySlider.centerRegistrationPoint();
            _frequencySlider.setLocation( ( _acBoxGraphic.getWidth() / 2 ) - 5, _acBoxGraphic.getHeight() - 10  );
            _frequencySlider.setMinimum( (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MIN ) );
            _frequencySlider.setMaximum( (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MAX ) );
            _frequencySlider.setValue( (int) ( 100.0 * _acSourceModel.getFrequency() ) );
            _frequencySlider.addChangeListener( new SliderListener() );
        }

        // Frequency value
        {
            _frequencyValue = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( _frequencyValue, VALUE_LAYER );
            _frequencyValue.setLocation( 198, 160 );
            
            _frequencyFormat = SimStrings.get( "ACSourceGraphic.frequency" );
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
        _acSourceModel.removeObserver( this );
        _acSourceModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        setVisible( _acSourceModel.isEnabled() );
        if ( isVisible() ) {
            
            // Update the displayed amplitude.
            {
                double maxAmplitude = _acSourceModel.getMaxAmplitude();
                
                // Format the text
                int value = (int) ( maxAmplitude * 100 );
                Object[] args = { new Integer( value ) };
                String text = MessageFormat.format( _amplitudeFormat, args );
                _amplitudeValue.setText( text );
                
                // Right justify
                int rx = _amplitudeValue.getBounds().width;
                int ry = _amplitudeValue.getBounds().height;
                _amplitudeValue.setRegistrationPoint( rx, ry );
            }
            
            // Update the displayed frequency.
            {
                double frequency = _acSourceModel.getFrequency();
                
                // Format the text
                int value = (int) ( 100 * frequency );
                Object[] args = { new Integer( value ) };
                String text = MessageFormat.format( _frequencyFormat, args );
                _frequencyValue.setText( text );
                
                // Right justify
                int rx = _frequencyValue.getBounds().width;
                int ry = _frequencyValue.getBounds().height;
                _frequencyValue.setRegistrationPoint( rx, ry );
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
                double maxAmplitude = _amplitudeSlider.getValue() / 100.0;
                // Update the model.
                _acSourceModel.setMaxAmplitude( maxAmplitude );
            }
            else if ( event.getSource() == _frequencySlider ) {
                // Read the value.
                double frequency = _frequencySlider.getValue() / 100.0;
                // Upate the model.
                _acSourceModel.setFrequency( frequency );
            }
        }
    }
}
