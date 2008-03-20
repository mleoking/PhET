/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;


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
 */
public class SineWaveGraphic extends PhetShapeGraphic {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Wave must be constrained to this viewport.
    private Dimension _viewportSize;
    // Maximum number of cycles to draw.
    private double _maxCycles;
    // The wave's amplitude.
    private double _amplitude;
    // The wave's frequency.
    private double _frequency;
    // The angle at the leftmost point on the wave.
    private double _startAngle;
    // The angle at the rightmost point on the wave.
    private double _endAngle;
    // Paths that describe the two halves of the sine wave.
    private GeneralPath _positivePath, _negativePath;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param component
     * @param viewportSize
     */
    public SineWaveGraphic( Component component, Dimension viewportSize ) {
        super( component );
        assert( viewportSize != null );
        
        _viewportSize = viewportSize;
        _maxCycles = 5;
        _positivePath = new GeneralPath();
        _negativePath = new GeneralPath();
        
        setBorderColor( Color.GREEN );
        setStroke( new BasicStroke( 1f ) );
        update();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the number of cycles that will be displayed when the frequency == 1.0.
     * 
     * @param maxCycles
     */
    public void setMaxCycles( double maxCycles ) {
        assert( maxCycles > 0 );
        _maxCycles = maxCycles;
    }
    
    /**
     * Gets the number of cycles that will be displayed when the frequency == 1.0.
     * 
     * @return the number of cycles
     */
    public double getMaxCycles() {
        return _maxCycles;
    }
    
    /**
     * Sets the amplitude of the displayed wave.
     * 
     * @param amplitude a value in the range -1...+1 inclusive
     */
    public void setAmplitude( double amplitude ) {
        assert( _amplitude >= -1 && _amplitude <= +1 );
        _amplitude = amplitude;
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
     * Sets the frequency of the displayed wave.
     * 
     * @param frequency a value in the range 0...1 inclusive (off...fastest)
     */
    public void setFrequency( double frequency ) {
        assert( frequency >= 0 && frequency <= 1 );
        _frequency = frequency;
    }
    
    /**
     * Gets the frequency.
     * 
     * @return the frequency
     */
    public double getFrequency() {
        return _frequency;
    }
    
    /**
     * Gets the start angle, the angle at the leftmost point on the wave.
     * 
     * @return the start angle, in radians
     */
    public double getStartAngle() {
        return _startAngle;
    }
    
    /**
     * Gets the end angle, the angle at the rightmost point on the wave.
     * 
     * @return the end angle, in radians
     */
    public double getEndAngle() {
        return _endAngle;
    }
    
    //----------------------------------------------------------------------------
    // Drawing
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current paramter settings.
     * The sine wave is approximated using a set of line segments.
     * The zero crossing (180 phase) of the center-most cycle is always at the origin.
     * <p>
     * NOTE! As a performance optimization, you must call this method explicitly
     * after changing parameter values.
     */
    public void update() {
        
        if ( isVisible() ) {
            
            // Number of wave cycles to fill the viewport at the current frequency.
            final double numCycles = _frequency * _maxCycles;
            // Change in angle per change in X.
            final double deltaAngle = ( 2 * Math.PI * numCycles ) / _viewportSize.width;

            // Start with 180 degree phase angle at (0,0).
            final double phaseAngle = Math.PI;
            _positivePath.reset();
            _positivePath.moveTo( 0, 0 );
            _negativePath.reset();
            _negativePath.moveTo( 0, 0 );

            // Work outwards in positive and negative X directions.
            double angle = 0;
            for ( double x = 1; x <= _viewportSize.width / 2.0; x++ ) {
                angle = phaseAngle + ( x * deltaAngle );
                double y = _amplitude * Math.sin( angle ) * _viewportSize.height / 2;
                _positivePath.lineTo( (float) x, (float) -y );  // +Y is up
                _negativePath.lineTo( (float) -x, (float) y );  // +Y is up
            }
            
            // Set the shape.
            _positivePath.append( _negativePath, false );
            setShape( _positivePath );

            // Make the start & end angle positive values, maintaining phase.
            _startAngle = ( ( 2 * Math.PI ) - ( angle % ( 2 * Math.PI) ) ) % ( 2 * Math.PI );
            _endAngle = _startAngle + ( 2 * ( angle - phaseAngle ) );
   
            repaint();
        }
    }
}
