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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


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
public class SineWaveGraphic extends PhetShapeGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Change in X for each line segment drawn.
    private static final double DX = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Wave must be drawn in this viewport.
    private Dimension _viewportSize;
    // Maximum number of cycles to draw.
    private double _maxCycles;
    // The wave's amplitude.
    private double _amplitude;
    // The wave's frequency.
    private double _frequency;
    
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
        
        setBorderColor( Color.GREEN );
        setStroke( new BasicStroke( 1f ) );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the number of cycles that will be displayed when the frequency == 1.
     * 
     * @param minCycles
     */
    public void setMaxCycles( double maxCycles ) {
        assert( maxCycles > 0 );
        _maxCycles = maxCycles;
    }
    
    /**
     * Gets the number of cycles that will be displayed when the frequency == 1.
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
     * @param frequency a value in the range 0...1 inclusive (off-fastest)
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
    
    //----------------------------------------------------------------------------
    // Drawing
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current paramter settings.
     * As a performance optimization, you must call this method explicitly
     * after changing paramter values.
     */
    public void update() {
        
        if ( isVisible() ) {
            
            // Number of lines to fill the viewport.
            final double numLines = _viewportSize.width / DX;
            // Number of wave cycles to fill the viewport at the current frequency.
            final double numCycles = _frequency * _maxCycles;
            // Change in angle per change in X.
            final double deltaAngle = Math.toRadians( numCycles * 360 / numLines );
            
            /*
             * Approximate the sine wave using line segments.
             * Keep the zero-crossing of one cycle centered at the origin.
             * Flip the sign on the Y coordinates so that +Y is up.
             */
            {
                GeneralPath wavePath = new GeneralPath();
                
                // Move to the starting location, at the far left.
                double angle = ( Math.PI  ) - ( deltaAngle * numLines / 2 );
                double x = -( numLines / 2 );
                double y = _amplitude * Math.sin( angle ) * _viewportSize.height / 2;
                wavePath.moveTo( (float) x, (float) -y );
                
                // Add lines, moving from left to right.
                for ( int i = 0; i < numLines; i++ ) {
                    angle += deltaAngle;
                    x += DX;
                    y = _amplitude * Math.sin( angle ) * _viewportSize.height / 2;
                    wavePath.lineTo( (float) x, (float) -y ); 
                }
                
                setShape( wavePath );
            }
            
            repaint();
        }
    }
}
