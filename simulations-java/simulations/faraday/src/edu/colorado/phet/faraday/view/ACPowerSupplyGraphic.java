/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.*;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.FaradaySlider;
import edu.colorado.phet.faraday.model.ACPowerSupply;


/**
 * ACPowerSupplyGraphic is the graphical representation of an AC power supply.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ACPowerSupplyGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Debugging
    private static final boolean DEBUG = false;
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double SLIDER_LAYER = 2;
    private static final double VALUE_LAYER = 3;
    private static final double WAVE_LAYER = 4;
    private static final double CURSOR_LAYER = 5;

    // Amplitude & frequency values
    private static final Font VALUE_FONT = new PhetFont( 12 );
    private static final Color VALUE_COLOR = Color.GREEN;
    private static final String AMPLITUDE_FORMAT = "{0}%";
    private static final String FREQUENCY_FORMAT = "{0}%";
    
    // Sine wave
    private static final Dimension WAVE_VIEWPORT_SIZE = new Dimension( 156, 122 );
    private static final Point WAVE_ORIGIN = new Point( 133, 103 );
    
    // Cursor
    private static final Color CURSOR_COLOR = Color.RED;
    private static final Stroke CURSOR_STROKE = new BasicStroke( 1f );
    private static final double CURSOR_WRAP_AROUND_TOLERANCE = Math.toRadians( 5 /* degrees */ );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ACPowerSupply _acPowerSupplyModel;
    private FaradaySlider _maxAmplitudeSlider;
    private FaradaySlider _frequencySlider;
    private PhetTextGraphic2 _maxAmplitudeValue;
    private PhetTextGraphic2 _frequencyValue;
    private PhetTextGraphic2 _debugAmplitudeValue; // set DEBUG=true to display this
    private SineWaveGraphic _waveGraphic;
    private PhetShapeGraphic _cursorGraphic;
    private double _previousMaxAmplitude;
    private double _previousFrequency;
    private double _cursorAngle;
    
    //----------------------------------------------------------------------------
    // Constructors
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
        
        // Max Amplitude slider
        {
            _maxAmplitudeSlider = new FaradaySlider( component, 100 /* track length */ );            
            
            _maxAmplitudeSlider.setMinimum( (int) ( 100.0 * FaradayConstants.AC_MAXAMPLITUDE_MIN ) );
            _maxAmplitudeSlider.setMaximum( (int) ( 100.0 * FaradayConstants.AC_MAXAMPLITUDE_MAX ) );
            _maxAmplitudeSlider.setValue( (int) ( 100.0 * _acPowerSupplyModel.getMaxAmplitude() ) );
            
            _maxAmplitudeSlider.centerRegistrationPoint();
            _maxAmplitudeSlider.rotate( -Math.PI / 2 );  // rotate -90 degrees
            _maxAmplitudeSlider.setLocation( 32, 130 );
            _maxAmplitudeSlider.addChangeListener( new SliderListener() );
        }
        
        // Max Amplitude value
        {
            _maxAmplitudeValue = new PhetTextGraphic2( component, VALUE_FONT, "", VALUE_COLOR );
            _maxAmplitudeValue.setLocation( 45, 57 );
        }
        
        // Amplitude value  (debug)
        {
            _debugAmplitudeValue = new PhetTextGraphic2( component, VALUE_FONT, "", Color.RED );
            _debugAmplitudeValue.setLocation( 90, 57 );
            _debugAmplitudeValue.setVisible( DEBUG );
        }
        
        // Frequency slider
        {
            _frequencySlider = new FaradaySlider( component, 100 /* track length */ );            

            _frequencySlider.setMinimum( (int) ( 100.0 * FaradayConstants.AC_FREQUENCY_MIN ) );
            _frequencySlider.setMaximum( (int) ( 100.0 * FaradayConstants.AC_FREQUENCY_MAX ) );
            _frequencySlider.setValue( (int) ( 100.0 * _acPowerSupplyModel.getFrequency() ) );
            
            _frequencySlider.centerRegistrationPoint();
            _frequencySlider.setLocation( 102, 190 );
            _frequencySlider.addChangeListener( new SliderListener() );
        }

        // Frequency value
        {
            _frequencyValue = new PhetTextGraphic2( component, VALUE_FONT, " ", VALUE_COLOR );
            _frequencyValue.setLocation( 210, 193 );
        }
        
        // Sine Wave
        {
            _waveGraphic = new SineWaveGraphic( component, WAVE_VIEWPORT_SIZE );
            // Configure cycles so that minimum frequency shows 1 cycle.
            _waveGraphic.setMaxCycles( FaradayConstants.AC_FREQUENCY_MAX / FaradayConstants.AC_FREQUENCY_MIN );
            _waveGraphic.setLocation( WAVE_ORIGIN );
        }
        
        // Cursor
        {
            int yOffset = WAVE_VIEWPORT_SIZE.height / 2;
            Shape shape = new Line2D.Double( 0, -yOffset, 0, yOffset );
            _cursorGraphic = new PhetShapeGraphic( component );
            _cursorGraphic.setShape( shape );
            _cursorGraphic.setBorderColor( CURSOR_COLOR );
            _cursorGraphic.setStroke( CURSOR_STROKE );
            // Locate at the left edge of the sine wave.
            int x = WAVE_ORIGIN.x - ( WAVE_VIEWPORT_SIZE.width / 2 );
            int y = WAVE_ORIGIN.y;
            _cursorGraphic.setLocation( x, y );
            _cursorGraphic.setVisible( false );
        }
        
        addGraphic( background, BACKGROUND_LAYER );
        addGraphic( _maxAmplitudeSlider, SLIDER_LAYER );
        addGraphic( _frequencySlider, SLIDER_LAYER );
        addGraphic( _debugAmplitudeValue, CURSOR_LAYER );
        addGraphic( _maxAmplitudeValue, VALUE_LAYER );
        addGraphic( _frequencyValue, VALUE_LAYER );
        addGraphic( _waveGraphic, WAVE_LAYER );
        addGraphic( _cursorGraphic, CURSOR_LAYER );
        
        // Registration point is the bottom center.
        int rx = getWidth() / 2;
        int ry = getHeight();
        setRegistrationPoint( rx, ry );
        
        // DEBUG -- draw a red rectangle around bounds
//        {
//            Shape shape = new Rectangle2D.Double( 0, 0, getWidth(), getHeight() );
//            PhetShapeGraphic boundsGraphic = new PhetShapeGraphic( component );
//            boundsGraphic.setShape( shape );
//            boundsGraphic.setStroke( new BasicStroke( 2f ) );
//            boundsGraphic.setBorderColor( Color.RED );
//            addGraphic( boundsGraphic, 10 );
//        }
        
        _cursorAngle = 0.0;
        _previousMaxAmplitude = _previousFrequency = -2;  // any invalid value is fine... 
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _acPowerSupplyModel.removeObserver( this );
        _acPowerSupplyModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     * 
     * NOTES:<br>
     * If maxAmplitude and frequency are both unchanged, then we assume that 
     * the model's amplitude has changed as the result of a simulation clock tick.<br>
     * If either the maxAmplitude or frequency has changed, then we assume that 
     * the user moved a slider and caused the model to be updated.
     */
    public void update() {
        
        setVisible( _acPowerSupplyModel.isEnabled() );
        if ( isVisible() ) {
            
            double amplitude = _acPowerSupplyModel.getAmplitude();
            double maxAmplitude = _acPowerSupplyModel.getMaxAmplitude();
            double frequency = _acPowerSupplyModel.getFrequency();
            
            if ( maxAmplitude == _previousMaxAmplitude && frequency == _previousFrequency ) {
                // The simulation clock ticked.
                
                // Update the moving cursor.
                updateCursor();
                
                // Update the current amplitude display.
                if ( _debugAmplitudeValue.isVisible() ) {
                    // Format the text
                    int value = (int) ( amplitude * 100 );
                    Object[] args = { new Integer( value ) };
                    String text = MessageFormat.format( AMPLITUDE_FORMAT, args );
                    _debugAmplitudeValue.setText( text );
                    
                    // Right justify
                    int rx = _debugAmplitudeValue.getBounds().width;
                    int ry = _debugAmplitudeValue.getBounds().height;
                    _debugAmplitudeValue.setRegistrationPoint( rx, ry ); // lower right
                }
            }
            else {
                // maxAmplitude and/or frequency was changed.

                // Reset the cursor.
                _cursorAngle = 0.0;
                _cursorGraphic.setVisible( false );

                // Update the sine wave.
                {
                    _waveGraphic.setAmplitude( maxAmplitude );
                    _waveGraphic.setFrequency( frequency );
                    _waveGraphic.update();
                }
                
                // Update the max amplitude display.
                if ( maxAmplitude != _previousMaxAmplitude ) {
     
                    // Format the text
                    int value = (int) ( maxAmplitude * 100 );
                    Object[] args = { new Integer( value ) };
                    String text = MessageFormat.format( AMPLITUDE_FORMAT, args );
                    _maxAmplitudeValue.setText( text );

                    // Right justify
                    int rx = _maxAmplitudeValue.getBounds().width;
                    int ry = _maxAmplitudeValue.getBounds().height;
                    _maxAmplitudeValue.setRegistrationPoint( rx, ry ); // lower right
                    
                    // Position the slider
                    if ( value != _maxAmplitudeSlider.getValue() ) {
                        _maxAmplitudeSlider.setValue( value );
                    }
                }

                // Update the frequency display.
                if ( frequency != _previousFrequency ) {

                    // Format the text
                    int value = (int) ( 100 * frequency );
                    Object[] args = { new Integer( value ) };
                    String text = MessageFormat.format( FREQUENCY_FORMAT, args );
                    _frequencyValue.setText( text );

                    // Right justify
                    int rx = _frequencyValue.getBounds().width;
                    int ry = _frequencyValue.getBounds().height;
                    _frequencyValue.setRegistrationPoint( rx, ry );
                    
                    // Position the slider
                    if ( value != _frequencySlider.getValue() ) {
                        _frequencySlider.setValue( value );
                    }
                }
                
                // Save the new values.
                _previousMaxAmplitude = maxAmplitude;
                _previousFrequency = frequency;
            }
            
            repaint();
        }
    }
    
    /*
     * Updates the cursor.
     * It is assumed that this will be called each time the simulation clock ticks.
     */
    private void updateCursor()
    {      
        double startAngle = _waveGraphic.getStartAngle();
        double endAngle = _waveGraphic.getEndAngle();
        double stepAngle = _acPowerSupplyModel.getStepAngle();
        
        // Add the stepAngle first, because ACPowerSupply.stepInTime adds its delta first.
        _cursorAngle += stepAngle;
        
        if ( _cursorAngle < startAngle ) {
            // The cursor is to the left of the visible waveform.
            _cursorGraphic.setVisible( false );
        }
        else if ( _cursorAngle >= endAngle ) {
            // The cursor is to the right of the visible waveform.
            _cursorGraphic.setVisible( false );
            // Wrap around.
            _cursorAngle = ( _cursorAngle % ( 2 * Math.PI ) );
            if ( _cursorAngle > startAngle + CURSOR_WRAP_AROUND_TOLERANCE ) {
                _cursorAngle -= ( 2 * Math.PI );
            }
        }
        
        // This is a new if statement in case wrap around occurred.
        if ( _cursorAngle >= startAngle && _cursorAngle < endAngle ) {
            // The cursor is on the visible waveform.
            _cursorGraphic.setVisible( true );
            double xStart = ( WAVE_ORIGIN.x - ( WAVE_VIEWPORT_SIZE.width / 2 ) );
            double percent = ( _cursorAngle - startAngle ) / ( endAngle - startAngle );
            double x =  xStart + ( percent * WAVE_VIEWPORT_SIZE.width );
            int y = WAVE_ORIGIN.y;
            _cursorGraphic.setLocation( (int) x, y );
        }
        
        // DEBUG output
//      System.out.println( "ACPowerSupplyGraphic.updateCursor:" +
//      		                "  cursorAngle=" + Math.toDegrees( _cursorAngle ) +
//      		                "  stepAngle=" + Math.toDegrees( stepAngle ) +
//                          "  amplitude=" + _acPowerSupplyModel.getAmplitude() * 100 + "%" );
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
            if ( event.getSource() == _maxAmplitudeSlider ) {
                // Read the value.
                double maxAmplitude = _maxAmplitudeSlider.getValue() / 100.0;
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
        private static final Font TITLE_FONT = new PhetFont( 15 );
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
            RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            graphicLayerSet.setRenderingHints( hints );
            
            // AC panel
            BufferedImage powerSupplyImage = FaradayResources.getImage( FaradayConstants.AC_POWER_SUPPLY_IMAGE );
            PhetImageGraphic panel = new PhetImageGraphic( component, powerSupplyImage );
            graphicLayerSet.addGraphic( panel, PANEL_LAYER );
            
            // Title label
            {
                PhetTextGraphic title = new PhetTextGraphic( component, TITLE_FONT, FaradayStrings.TITLE_AC_POWER_SUPPLY, TITLE_COLOR );
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
                    int x = TICK_SPACING;
                    int y = TICK_LENGTH / 2;
                    while ( x < xLength / 2 ) {

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
                        
                        x += TICK_SPACING;
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
                    int y = TICK_SPACING;
                    while ( y < yLength / 2 ) {

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
                        
                        y += TICK_SPACING;
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
