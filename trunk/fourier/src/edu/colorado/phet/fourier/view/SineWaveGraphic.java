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
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.model.FourierComponent;


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
public class SineWaveGraphic extends CompositePhetGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean USE_COSINES = false;
    private static final double PHASE_ANGLE = 0.0;
    
    private static final Color DEFAULT_WAVE_COLOR = Color.BLACK;
    private static final Stroke DEFAULT_WAVE_STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model we're viewing
    private FourierComponent _fourierComponent;
    // Wave must be constrained to this viewport.
    private Dimension _viewportSize;
    // Graphicss for the two halves of the wave
    PhetShapeGraphic _positivePathGraphic, _negativePathGraphic;
    // Paths that describe the two halves of the wave.
    private GeneralPath _positivePath, _negativePath;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public SineWaveGraphic( Component component, FourierComponent fourierComponent ) {
        super( component );
        
        assert( fourierComponent != null );
        _fourierComponent = fourierComponent;
        _fourierComponent.addObserver( this );
        
        _viewportSize = new Dimension( 200, 100 );
        
        _positivePath = new GeneralPath();
        _positivePathGraphic = new PhetShapeGraphic( component );
        _positivePathGraphic.setShape( _positivePath );
        _positivePathGraphic.setBorderColor( DEFAULT_WAVE_COLOR );
        _positivePathGraphic.setStroke( DEFAULT_WAVE_STROKE );
        addGraphic( _positivePathGraphic );
        
        _negativePath = new GeneralPath();
        _negativePathGraphic = new PhetShapeGraphic( component );
        _negativePathGraphic.setShape( _negativePath );
        _negativePathGraphic.setBorderColor( DEFAULT_WAVE_COLOR );
        _negativePathGraphic.setStroke( DEFAULT_WAVE_STROKE );
        addGraphic( _negativePathGraphic );
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _fourierComponent.removeObserver( this );
        _fourierComponent = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color used to draw the wave.
     * 
     * @param color the color
     */
    public void setColor( Color color ) {
        _positivePathGraphic.setBorderColor( color );
        _negativePathGraphic.setBorderColor( color );
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
        if ( width != _viewportSize.width || height != _viewportSize.height ) {
            _viewportSize.setSize( width, height );
            update();
        }
    }
    
    //----------------------------------------------------------------------------
    // Drawing
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current paramter settings.
     * The sine wave is approximated using a set of line segments.
     * The origin is at the center of the viewport.
     * <p>
     * NOTE! As a performance optimization, you must call this method explicitly
     * after changing parameter values.
     */
    public void update() {

        if ( isVisible() ) {
            
            _positivePath.reset();
            _negativePath.reset();
            
            int numberOfCycles = _fourierComponent.getOrder() + 1;
            double amplitude = _fourierComponent.getAmplitude();
            
            // Change in angle per change in X.
            final double deltaAngle = ( 2.0 * Math.PI * numberOfCycles ) / _viewportSize.width;

            // Starting point
            {
                double radians = 0;
                if ( USE_COSINES ) {
                    radians = Math.cos( PHASE_ANGLE );
                }
                else {
                    radians = Math.sin( PHASE_ANGLE );
                }
                double yStart = amplitude * radians * ( _viewportSize.height / 2.0 );
                _positivePath.moveTo( 0, (float) -yStart ); // +Y is up
                _negativePath.moveTo( 0, (float) -yStart ); // +Y is up
            }

            // Work outwards in positive and negative X directions.
            for ( double x = 1; x <= ( _viewportSize.width / 2.0 ); x++ ) {
                double positiveAngle = PHASE_ANGLE + ( x * deltaAngle );
                double negativeAngle = PHASE_ANGLE - ( x * deltaAngle );
                double positiveRadians = 0;
                double negativeRadians = 0;
                if ( USE_COSINES) {
                    positiveRadians = Math.cos( positiveAngle );
                    negativeRadians = Math.cos( negativeAngle );
                }
                else {
                    positiveRadians = Math.sin( positiveAngle );
                    negativeRadians = Math.sin( negativeAngle );
                }
                double positiveY = amplitude * positiveRadians * ( _viewportSize.height / 2.0 );
                double negativeY = amplitude * negativeRadians * ( _viewportSize.height / 2.0 );
                _positivePath.lineTo( (float) x, (float) -positiveY );  // +Y is up
                _negativePath.lineTo( (float) -x, (float) -negativeY );  // +Y is up
            }
            
            _positivePathGraphic.setShape( _positivePath );
            _negativePathGraphic.setShape( _negativePath );
   
            repaint();
        }
    }
}
