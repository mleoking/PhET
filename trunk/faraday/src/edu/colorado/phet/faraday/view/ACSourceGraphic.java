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

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
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
    private static final double WAVE_BACKGROUND_LAYER = 2;
    private static final double WAVE_LAYER = 3;
    private static final double WAVE_OVERLAY_LAYER = 4;
    private static final double LABEL_LAYER = 5;
    private static final double SLIDER_LAYER = 6;
    private static final double VALUE_LAYER = 7;
    
    private static final Font TITLE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
    private static final Color VALUE_COLOR = Color.GREEN;
    
    private static final double WAVE_SCALE_X = 0.17;
    private static final double WAVE_SCALE_Y = 0.11;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ACSource _acSourceModel;
    private PhetImageGraphic _acBoxGraphic;
    private FaradaySlider _amplitudeSlider;
    private FaradaySlider _frequencySlider;
    private PhetTextGraphic _amplitudeValue;
    private PhetTextGraphic _frequencyValue;
    private PhetShapeGraphic _waveGraphic;
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
        
        // Enable anti-aliasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( hints );
        
        // AC panel
        {
            _acBoxGraphic = new PhetImageGraphic( component, FaradayConfig.AC_SOURCE_IMAGE );
            addGraphic( _acBoxGraphic, BOX_LAYER );
        }   
        
        // Title label
        {
            String s = SimStrings.get( "ACSourceGraphic.title" );
            PhetTextGraphic title = new PhetTextGraphic( component, TITLE_FONT, s, TITLE_COLOR );
            addGraphic( title, LABEL_LAYER );
            title.centerRegistrationPoint();
            title.setLocation( _acBoxGraphic.getWidth() / 2, 36 );
        }
        
        // Amplitude slider
        {
            _amplitudeSlider = new FaradaySlider( component, 100 /* track length */ );            
            addGraphic( _amplitudeSlider, SLIDER_LAYER );
            
            _amplitudeSlider.setMinimum( (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MIN ) );
            _amplitudeSlider.setMaximum( (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MAX ) );
            _amplitudeSlider.setValue( (int) ( 100.0 * _acSourceModel.getMaxAmplitude() ) );
            
            _amplitudeSlider.centerRegistrationPoint();
            _amplitudeSlider.rotate( -Math.PI / 2 );  // rotate -90 degrees
            _amplitudeSlider.setLocation( 32, 130 );
            _amplitudeSlider.addChangeListener( new SliderListener() );
        }
        
        // Amplitude value
        {
            _amplitudeValue = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( _amplitudeValue, VALUE_LAYER );
            _amplitudeValue.setLocation( 45, 70 );
            
            _amplitudeFormat = SimStrings.get( "ACSourceGraphic.amplitude.format" );
        }
        
        // Frequency slider
        {
            _frequencySlider = new FaradaySlider( component, 100 /* track length */ );            
            addGraphic( _frequencySlider, SLIDER_LAYER );

            _frequencySlider.setMinimum( (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MIN ) );
            _frequencySlider.setMaximum( (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MAX ) );
            _frequencySlider.setValue( (int) ( 100.0 * _acSourceModel.getFrequency() ) );
            
            _frequencySlider.centerRegistrationPoint();
            _frequencySlider.setLocation( 102, 190 );
            _frequencySlider.addChangeListener( new SliderListener() );
        }

        // Frequency value
        {
            _frequencyValue = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( _frequencyValue, VALUE_LAYER );
            _frequencyValue.setLocation( 210, 207 );
            
            _frequencyFormat = SimStrings.get( "ACSourceGraphic.frequency.format" );
        }
        
        // Wave
        {
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            int x = 0;
            for ( int angle = 0; angle < 360; angle++ ) {
                int y = (int) ( 360 * Math.sin( Math.toRadians( angle ) ) );
                path.lineTo( x, -y );
                x += 2;
            }
            _waveGraphic = new PhetShapeGraphic( component );
            _waveGraphic.setShape( path );
            _waveGraphic.setBorderColor( Color.GREEN );
            _waveGraphic.setStroke( new BasicStroke( 10f ) );
            addGraphic( _waveGraphic, WAVE_LAYER );
            _waveGraphic.setRegistrationPoint( _waveGraphic.getWidth()/2, 0 );
            _waveGraphic.setLocation( 132, 100 );
            _waveGraphic.scale( WAVE_SCALE_X, WAVE_SCALE_Y );
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
            
            double maxAmplitude = _acSourceModel.getMaxAmplitude();
            double frequency = _acSourceModel.getFrequency();
            
            // Update the displayed amplitude.
            {
                // Format the text
                int value = (int) ( maxAmplitude * 100 );
                Object[] args = { new Integer( value ) };
                String text = MessageFormat.format( _amplitudeFormat, args );
                _amplitudeValue.setText( text );
                
                // Right justify
                int rx = _amplitudeValue.getBounds().width;
                int ry = _amplitudeValue.getBounds().height;
                _amplitudeValue.setRegistrationPoint( rx, ry ); // lower right
            }
            
            // Update the displayed frequency.
            {
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
            
            // Update the sin wave.
            _waveGraphic.clearTransform();
            double xScale = ( 1 - frequency + FaradayConfig.AC_FREQUENCY_MIN ) * WAVE_SCALE_X;  //HACK
            double yScale = maxAmplitude * WAVE_SCALE_Y;
            _waveGraphic.scale( xScale, yScale );
            
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
