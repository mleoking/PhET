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
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.FaradaySlider;
import edu.colorado.phet.faraday.model.ACPowerSupply;


/**
 * ACPowerSupplyGraphic is the graphical representation of an AC power supply.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACPowerSupplyGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double SLIDER_LAYER = 2;
    private static final double VALUE_LAYER = 3;
    private static final double WAVE_LAYER = 4;
    private static final double CURSOR_LAYER = 5;

    // Amplitude & frequency values
    private static final Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
    private static final Color VALUE_COLOR = Color.GREEN;
    
    // Sine wave
    private static final Dimension WAVE_VIEWPORT_SIZE = new Dimension( 156, 122 );
    private static final Point WAVE_ORIGIN = new Point( 133, 103 );
    
    // Cursor
    private static final Color CURSOR_COLOR = Color.RED;
    private static final Stroke CURSOR_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ACPowerSupply _acPowerSupplyModel;
    private FaradaySlider _amplitudeSlider;
    private FaradaySlider _frequencySlider;
    private PhetTextGraphic _amplitudeValue;
    private PhetTextGraphic _frequencyValue;
    private SineWaveGraphic _waveGraphic;
    private PhetShapeGraphic _cursorGraphic;
    private String _amplitudeFormat;
    private String _frequencyFormat;
    private double _previousMaxAmplitude;
    private double _previousFrequency;
    
    //----------------------------------------------------------------------------
    // Constructors and finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param acPowerSupplyModel
     */
    public ACPowerSupplyGraphic( Component component, ACPowerSupply acPowerSupplyModel ) {
        
        super( component );
        
        assert( component != null );
        assert( acPowerSupplyModel != null );
        
        _acPowerSupplyModel = acPowerSupplyModel;
        _acPowerSupplyModel.addObserver( this );
        
        // Enable anti-aliasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( hints );
        
        // Background, contains all of the static graphic components.
        BackgroundGraphic background = new BackgroundGraphic( component );
        addGraphic( background, BACKGROUND_LAYER );
        
        // Amplitude slider
        {
            _amplitudeSlider = new FaradaySlider( component, 100 /* track length */ );            
            addGraphic( _amplitudeSlider, SLIDER_LAYER );
            
            _amplitudeSlider.setMinimum( (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MIN ) );
            _amplitudeSlider.setMaximum( (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MAX ) );
            _amplitudeSlider.setValue( (int) ( 100.0 * _acPowerSupplyModel.getMaxAmplitude() ) );
            
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
            
            _amplitudeFormat = SimStrings.get( "ACPowerSupplyGraphic.amplitude.format" );
        }
        
        // Frequency slider
        {
            _frequencySlider = new FaradaySlider( component, 100 /* track length */ );            
            addGraphic( _frequencySlider, SLIDER_LAYER );

            _frequencySlider.setMinimum( (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MIN ) );
            _frequencySlider.setMaximum( (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MAX ) );
            _frequencySlider.setValue( (int) ( 100.0 * _acPowerSupplyModel.getFrequency() ) );
            
            _frequencySlider.centerRegistrationPoint();
            _frequencySlider.setLocation( 102, 190 );
            _frequencySlider.addChangeListener( new SliderListener() );
        }

        // Frequency value
        {
            _frequencyValue = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( _frequencyValue, VALUE_LAYER );
            _frequencyValue.setLocation( 210, 207 );
            
            _frequencyFormat = SimStrings.get( "ACPowerSupplyGraphic.frequency.format" );
        }
        
        // Sine Wave
        {
            _waveGraphic = new SineWaveGraphic( component, WAVE_VIEWPORT_SIZE );
            // Configure cycles so that minimum frequency shows 1 cycle.
            _waveGraphic.setMaxCycles( FaradayConfig.AC_FREQUENCY_MAX / FaradayConfig.AC_FREQUENCY_MIN );
            _waveGraphic.setLocation( WAVE_ORIGIN );
            addGraphic( _waveGraphic, WAVE_LAYER );
        }
        
        // Cursor
        {
            int yOffset = WAVE_VIEWPORT_SIZE.height / 2;
            Shape shape = new Line2D.Double( 0, -yOffset, 0, yOffset );
            _cursorGraphic = new PhetShapeGraphic( component );
            _cursorGraphic.setShape( shape );
            _cursorGraphic.setBorderColor( CURSOR_COLOR );
            _cursorGraphic.setStroke( CURSOR_STROKE );
            _cursorGraphic.setLocation( WAVE_ORIGIN );
            addGraphic( _cursorGraphic, CURSOR_LAYER );
        }
        
        // Registration point is the bottom center.
        int rx = getWidth() / 2;
        int ry = getHeight();
        setRegistrationPoint( rx, ry );
        
        _previousMaxAmplitude = _previousFrequency = -2;  // any invalid value is fine... 
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _acPowerSupplyModel.removeObserver( this );
        _acPowerSupplyModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        setVisible( _acPowerSupplyModel.isEnabled() );
        if ( isVisible() ) {
            
            double maxAmplitude = _acPowerSupplyModel.getMaxAmplitude();
            double frequency = _acPowerSupplyModel.getFrequency();
            
            // Update the displayed amplitude.
            if ( maxAmplitude != _previousMaxAmplitude ) {
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
            if ( frequency != _previousFrequency ) {
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
            
            // Update the sine wave.
            if ( maxAmplitude != _previousMaxAmplitude || frequency != _previousFrequency ) {
                _waveGraphic.setAmplitude( maxAmplitude );
                _waveGraphic.setFrequency( frequency );
                _waveGraphic.update();
            }
            
            // Update the cursor position on every clock tick.
            {
                int x = _cursorGraphic.getX() + 1;
                int y = _cursorGraphic.getY();
                if ( x > WAVE_ORIGIN.x + ( WAVE_VIEWPORT_SIZE.width / 2 ) ) {
                    x = WAVE_ORIGIN.x - ( WAVE_VIEWPORT_SIZE.width / 2  );
                }
                _cursorGraphic.setLocation( x, y );
            }
            
            _previousMaxAmplitude = maxAmplitude;
            _previousFrequency = frequency;
            
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * SliderListener handles changes to the amplitude slider.
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
                _acPowerSupplyModel.setMaxAmplitude( maxAmplitude );
            }
            else if ( event.getSource() == _frequencySlider ) {
                // Read the value.
                double frequency = _frequencySlider.getValue() / 100.0;
                // Upate the model.
                _acPowerSupplyModel.setFrequency( frequency );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * BackgroundGraphic creates a background image from a bunch of static 
     * graphic components.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class BackgroundGraphic extends PhetImageGraphic {
        
        // Layers
        private static final double PANEL_LAYER = 1;
        private static final double TITLE_LAYER = 2;
        private static final double AXES_LAYER = 3;
        private static final double TICK_LAYER = 4;
        
        // Title
        private static final Font TITLE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
        private static final Color TITLE_COLOR = Color.WHITE;
        
        // Axes
        private static final Stroke AXES_STROKE = new BasicStroke( 1f );
        private static final Color AXES_COLOR = new Color( 255, 255, 255, 100 ); // transparent white
        
        // Tick marks
        private static final Stroke TICK_STROKE = AXES_STROKE;
        private static final Color TICK_COLOR = AXES_COLOR;
        private static final int TICK_SPACING = 10; // pixels
        private static final int TICK_LENGTH = 8; // pixels
        
        /**
         * Sole constructor.
         * 
         * @param component
         */
        public BackgroundGraphic( Component component ) {
            super( component );
            
            // This will be flattened after we've added graphics to it.
            GraphicLayerSet graphicLayerSet = new GraphicLayerSet( component );
            
            // AC panel
            PhetImageGraphic panel = new PhetImageGraphic( component, FaradayConfig.AC_POWER_SUPPLY_IMAGE );
            graphicLayerSet.addGraphic( panel, PANEL_LAYER );
            
            // Title label
            {
                String s = SimStrings.get( "ACPowerSupplyGraphic.title" );
                PhetTextGraphic title = new PhetTextGraphic( component, TITLE_FONT, s, TITLE_COLOR );
                graphicLayerSet.addGraphic( title, TITLE_LAYER );
                title.centerRegistrationPoint();
                title.setLocation( panel.getWidth() / 2, 36 );
            }
            
            // Axes
            {
                int xLength = WAVE_VIEWPORT_SIZE.width;
                int yLength = WAVE_VIEWPORT_SIZE.height;
                
                // X axis
                {
                    // Axis
                    PhetShapeGraphic xAxis = new PhetShapeGraphic( component );
                    xAxis.setShape( new Line2D.Double( -xLength / 2, 0, xLength / 2, 0 ) );
                    xAxis.setBorderColor( AXES_COLOR );
                    xAxis.setStroke( AXES_STROKE );
                    xAxis.setLocation( WAVE_ORIGIN );
                    graphicLayerSet.addGraphic( xAxis, AXES_LAYER );
                    
                    // Tick marks -- start at the origin and move out in both directions.
                    int x = 0;
                    int y = TICK_LENGTH / 2;
                    while ( x <= xLength / 2 ) {
                        x += TICK_SPACING;
                        
                        PhetShapeGraphic positiveTick = new PhetShapeGraphic( component );
                        positiveTick.setShape( new Line2D.Double( x, -y, x, y  ) );
                        positiveTick.setBorderColor( TICK_COLOR );
                        positiveTick.setStroke( TICK_STROKE );
                        positiveTick.setLocation( WAVE_ORIGIN );
                        graphicLayerSet.addGraphic( positiveTick, TICK_LAYER );
                        
                        PhetShapeGraphic negativeTick = new PhetShapeGraphic( component );
                        negativeTick.setShape( new Line2D.Double( -x, -y, -x, y ) );
                        negativeTick.setBorderColor( TICK_COLOR );
                        negativeTick.setStroke( TICK_STROKE );
                        negativeTick.setLocation( WAVE_ORIGIN );
                        graphicLayerSet.addGraphic( negativeTick, TICK_LAYER );
                    }
                }

                // Y axis
                {
                    // Axis
                    PhetShapeGraphic yAxis = new PhetShapeGraphic( component );
                    yAxis.setShape( new Line2D.Double( 0, -yLength / 2, 0, yLength / 2 ) );
                    yAxis.setBorderColor( AXES_COLOR );
                    yAxis.setStroke( AXES_STROKE );
                    yAxis.setLocation( WAVE_ORIGIN );
                    graphicLayerSet.addGraphic( yAxis, AXES_LAYER );
                    
                    // Tick marks -- start at the origin and move out in both directions.
                    int x = TICK_LENGTH / 2;
                    int y = 0;
                    while ( y <= yLength / 2 ) {
                        y += TICK_SPACING;

                        PhetShapeGraphic positiveTick = new PhetShapeGraphic( component );
                        positiveTick.setShape( new Line2D.Double( -x, y, x, y  ) );
                        positiveTick.setBorderColor( TICK_COLOR );
                        positiveTick.setStroke( TICK_STROKE );
                        positiveTick.setLocation( WAVE_ORIGIN );
                        graphicLayerSet.addGraphic( positiveTick, TICK_LAYER );
                        
                        PhetShapeGraphic negativeTick = new PhetShapeGraphic( component );
                        negativeTick.setShape( new Line2D.Double( -x, -y, x, -y ) );
                        negativeTick.setBorderColor( TICK_COLOR );
                        negativeTick.setStroke( TICK_STROKE );
                        negativeTick.setLocation( WAVE_ORIGIN );
                        graphicLayerSet.addGraphic( negativeTick, TICK_LAYER );
                    }
                }
            }
            
            // Flatten the graphic layer set.
            {
                Dimension size = graphicLayerSet.getSize();
                BufferedImage bufferedImage = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
                Graphics2D g2 = bufferedImage.createGraphics();
                graphicLayerSet.paint( g2 );
                setImage( bufferedImage );
            }
        }
    } // class Background
}
