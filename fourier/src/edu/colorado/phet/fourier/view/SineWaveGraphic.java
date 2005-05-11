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
public class SineWaveGraphic extends PhetShapeGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color DEFAULT_WAVE_COLOR = Color.BLACK;
    private static final Stroke DEFAULT_WAVE_STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model we're viewing
    private FourierComponent _fourierComponent;
    // Wave must be constrained to this viewport.
    private Dimension _viewportSize;
    // Paths that describe the two halves of the sine wave.
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
        _negativePath = new GeneralPath();
        
        setBorderColor( DEFAULT_WAVE_COLOR );
        setStroke( DEFAULT_WAVE_STROKE );
        
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
        setBorderColor( color );
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
            
            int numberOfCycles = _fourierComponent.getOrder() + 1;
            double amplitude = _fourierComponent.getAmplitude();
            
            // Change in angle per change in X.
            final double deltaAngle = ( 2 * Math.PI * numberOfCycles ) / _viewportSize.width;

            // Start with 0 degree phase angle at (0,0).
            final double phaseAngle = 0;
            _positivePath.reset();
            _positivePath.moveTo( 0, 0 );
            _negativePath.reset();
            _negativePath.moveTo( 0, 0 );

            // Work outwards in positive and negative X directions.
            double angle = 0;
            for ( double x = 1; x <= ( _viewportSize.width / 2.0 ); x++ ) {
                angle = phaseAngle + ( x * deltaAngle );
                double y = amplitude * Math.sin( angle ) * _viewportSize.height / 2;
                _positivePath.lineTo( (float) x, (float) -y );  // +Y is up
                _negativePath.lineTo( (float) -x, (float) y );  // +Y is up
            }
            
            // Set the shape.
            _positivePath.append( _negativePath, false );
            setShape( _positivePath );
   
            repaint();
        }
    }
}
