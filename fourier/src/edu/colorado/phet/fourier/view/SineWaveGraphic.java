/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;


/**
 * SinWaveGraphic is the graphical representation of a sine wave.
 * <p>
 * A set of line segments is draw to approximate the curve.
 * The curve is constrained to be drawn within some viewport.
 * The amplitude determines the height of the curve, while
 * the frequency determines how many cycles of the curve will
 * be drawn.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SineWaveGraphic extends PhetShapeGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int WAVE_TYPE_SINE = 0;
    public static final int WAVE_TYPE_COSINE = 1;
    
    // Defaults
    private static final Dimension DEFAULT_VIEWPORT_SIZE = new Dimension( 200, 50 );
    private static final double DEFAULT_PHASE_ANGLE = 0.0;
    private static final int DEFAULT_WAVE_TYPE = WAVE_TYPE_SINE;
    private static final Color DEFAULT_WAVE_COLOR = Color.BLACK;
    private static final float DEFAULT_WAVE_LINE_WIDTH = 2f;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Number of cycles
    private int _numberOfCycles;
    // Amplitude
    private double _amplitude;
    // Wave must be constrained to this viewport.
    private Dimension _viewportSize;
    // Paths that describes the wave.
    private GeneralPath _path;
    // The phase angle at the origin
    private double _phaseAngle;
    // Type of wave (sine or cosine)
    private int _waveType;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public SineWaveGraphic( Component component ) {
        super( component );
        
        _numberOfCycles = 1;
        _amplitude = 0.0;
        _viewportSize = DEFAULT_VIEWPORT_SIZE;
        _phaseAngle = DEFAULT_PHASE_ANGLE;
        _waveType = DEFAULT_WAVE_TYPE;
        
        _path = new GeneralPath();
        setShape( _path );
        setBorderColor( DEFAULT_WAVE_COLOR );
        setStroke( new BasicStroke( DEFAULT_WAVE_LINE_WIDTH ) );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the number of cycles to display in the viewport.
     * 
     * @param numberOfCycles
     */
    public void setNumberOfCycles( int numberOfCycles ) {
        assert( numberOfCycles > 0 );
        if ( numberOfCycles != _numberOfCycles ) {
            _numberOfCycles = numberOfCycles;
            update();
        }
    }
    
    /**
     * Gets the number of cycles that will be displayed in the viewport.
     * 
     * @return the number of cycles
     */
    public int getNumberOfCycles() {
        return _numberOfCycles;
    }
    
    /**
     * Sets the amplitude.
     * The amplitude determines how much of the vertical space in the viewport will be filled.
     * 
     * @param amplitude, +1 to +1
     */
    public void setAmplitude( double amplitude ) {
        assert( amplitude >= -1 && amplitude <= +1 );
        if ( amplitude != _amplitude ) {
            _amplitude = amplitude;
            update();
        }
    }
    
    /**
     * Gets the amplitude.
     * 
     * @return the amplitude
     */
    public double getAmplitude() {
        return _amplitude;
    }
    
    /**
     * Sets the wave type (sine or cosine).
     * 
     * @param waveType WAVE_TYPE_SINE or WAVE_TYPE_COSINE
     */
    public void setWaveType( int waveType ) {
        assert( waveType == WAVE_TYPE_SINE || waveType == WAVE_TYPE_COSINE );
        if ( waveType != _waveType ) {
            _waveType = waveType;
            update();
        }
    }
    
    /**
     * Gets the wave type.
     * 
     * @return WAVE_TYPE_SINES or WAVE_TYPE_COSINE
     */
    public int getWaveType() {
        return _waveType;
    }
    
    /**
     * Sets the phase angle at the origin.
     * 
     * @param phaseAngle the phase angle, in radians
     */
    public void setPhaseAngle( double phaseAngle ) {
        if ( phaseAngle != phaseAngle ) {
            _phaseAngle = phaseAngle;
            update();
        }
    }
    
    /**
     * Gets the phase angle at the origin.
     * 
     * @return the phase angle, in radians
     */
    public double getPhaseAngle() {
        return _phaseAngle;
    }

    /**
     * Sets the color used to draw the wave.
     * 
     * @param color the color
     */
    public void setColor( Color color ) {
        assert( color != null );
        setBorderColor( color );
        repaint();
    }

    /**
     * Sets the line width used to draw the wave.
     * 
     * @param viewportSize
     */
    public void setLineWidth( float width ) {
        assert( width > 0 );
        setStroke( new BasicStroke( width ) );
        repaint();
    }
    
    /**
     * Sets the viewport size.
     * 
     * @param viewportSize the viewport size
     */
    public void setViewportSize( Dimension viewportSize ) {
        assert( viewportSize != null );
        setViewportSize( viewportSize.width, viewportSize.height );
    }
    
    /**
     * Sets the viewport size.
     * 
     * @param width the viewport width
     * @param height the viewport height
     */
    public void setViewportSize( int width, int height ) {
        assert( width > 0 && height > 0 );
        if ( width != _viewportSize.width || height != _viewportSize.height ) {
            _viewportSize.setSize( width, height );
            update();
        }
    }
    
    /**
     * Gets the viewport size.
     * 
     * @return the viewport size
     */
    public Dimension getViewportSize() {
        return new Dimension( _viewportSize );
    }
    
    //----------------------------------------------------------------------------
    // Drawing
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current parameter settings.
     * The wave is approximated using a set of line segments.
     * The origin is at the center of the viewport.
     * <p>
     * NOTE! As a performance optimization, you must call this
     * method explicitly after changing parameter values.
     */
    public void update() {

        if ( isVisible() ) {
            
            // Change in angle per change in x coordinate
            final double deltaAngle = ( 2.0 * Math.PI * _numberOfCycles ) / _viewportSize.width;

            // Start angle
            double startAngle = _phaseAngle - ( deltaAngle * ( _viewportSize.width / 2.0 ) );
            
            // Approximate the wave as a set of line segments.
            _path.reset();
            for ( int i = 0; i <= _viewportSize.width; i++ ) {
                double angle = startAngle + ( i * deltaAngle );
                double radians = ( _waveType == WAVE_TYPE_SINE ) ? Math.sin( angle ): Math.cos( angle );
                double x = -( _viewportSize.width / 2 - i );
                double y = ( _amplitude / FourierConfig.MAX_HARMONIC_AMPLITUDE ) * radians * ( _viewportSize.height / 2.0 );
                if ( i == 0 ) {
                    _path.moveTo( (float) x, (float) -y );  // +Y is up
                }
                else {
                    _path.lineTo( (float) x, (float) -y );  // +Y is up
                }
            }
            
            setShape( _path );
            repaint();
        }
    }
}