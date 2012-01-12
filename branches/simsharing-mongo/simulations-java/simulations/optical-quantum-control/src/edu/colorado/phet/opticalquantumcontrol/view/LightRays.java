// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCConstants;
import edu.colorado.phet.opticalquantumcontrol.model.FourierSeries;


/**
 * LightRays
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightRays extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int WIDTH = 27;
    private static final int HEIGHT = 650;
    private static final int SPACING = 10;
    private static final int MAX_ALPHA = OQCConstants.MAX_LIGHT_ALPHA;
    private static final int MIN_ALPHA = OQCConstants.MIN_LIGHT_ALPHA;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private ArrayList _outputBars; // array of PhetShapeGraphic
    private ArrayList _outputRays; // array of PhetShapeGraphic
    private PhetShapeGraphic _outputBeam;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param fourierSeries
     */
    public LightRays( Component component, FourierSeries fourierSeries ) {
        super( component );
        
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        setIgnoreMouse( true );
        
        int numberOfHarmonics = fourierSeries.getNumberOfHarmonics();
        
        // Color bars from the input mirror to the amplitude sliders
        int x = 0;
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            Rectangle2D rectangle = new Rectangle2D.Double( x, 0, WIDTH, HEIGHT/2 );
            Color color = HarmonicColors.getInstance().getColor( i );
            Color alphaColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), MAX_ALPHA );
            PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( component );
            shapeGraphic.setShape( rectangle );
            shapeGraphic.setColor( alphaColor );
            addGraphic( shapeGraphic );
            x += WIDTH + SPACING;
        }
        
        // Color bars from the amplitude sliders to the output mirror
        _outputBars = new ArrayList();
        x = 0;
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            Rectangle2D rectangle = new Rectangle2D.Double( x, HEIGHT/2, WIDTH, HEIGHT/2 );
            PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( component );
            shapeGraphic.setShape( rectangle );
            addGraphic( shapeGraphic );
            x += WIDTH + SPACING;
            _outputBars.add( shapeGraphic );
        }
        
        // Rays from the input diffraction grating to the input mirror
        {
            Point focalPoint = new Point( 344, 221 );
            Point[] points = { 
                    new Point( 0 * (WIDTH+SPACING), 23 ), new Point( ( 0 * (WIDTH+SPACING) ) + WIDTH, 19 ), 
                    new Point( 1 * (WIDTH+SPACING), 19 ), new Point( ( 1 * (WIDTH+SPACING) ) + WIDTH, 15 ), 
                    new Point( 2 * (WIDTH+SPACING), 15 ), new Point( ( 2 * (WIDTH+SPACING) ) + WIDTH, 15 ), 
                    new Point( 3 * (WIDTH+SPACING), 15 ), new Point( ( 3 * (WIDTH+SPACING) ) + WIDTH, 15 ), 
                    new Point( 4 * (WIDTH+SPACING), 15 ), new Point( ( 4 * (WIDTH+SPACING) ) + WIDTH, 15 ), 
                    new Point( 5 * (WIDTH+SPACING), 15 ), new Point( ( 5 * (WIDTH+SPACING) ) + WIDTH, 19 ), 
                    new Point( 6 * (WIDTH+SPACING), 19 ), new Point( ( 6 * (WIDTH+SPACING) ) + WIDTH, 23 ) };

            int order = 0;
            for ( int i = 0; i < points.length; ) {
                // Path
                GeneralPath path = new GeneralPath();
                path.moveTo( focalPoint.x, focalPoint.y );
                path.lineTo( points[i].x, points[i].y );
                i++;
                path.lineTo( points[i].x, points[i].y );
                i++;
                path.closePath();
                // Ray
                PhetShapeGraphic ray = new PhetShapeGraphic( component );
                ray.setShape( path );
                Color color = HarmonicColors.getInstance().getColor( order++ );
                Color alphaColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), MAX_ALPHA );
                ray.setColor( alphaColor );
                ray.setLocation( 0, 0 );
                addGraphic( ray );
            }
        }

        // Rays from the output mirror to the output diffraction grating
        {
            Point focalPoint = new Point( 344, 423 );
            Point[] points = { 
                    new Point( 0 * (WIDTH+SPACING), 622 ), new Point( ( 0 * (WIDTH+SPACING) ) + WIDTH, 626 ), 
                    new Point( 1 * (WIDTH+SPACING), 626 ), new Point( ( 1 * (WIDTH+SPACING) ) + WIDTH, 630 ), 
                    new Point( 2 * (WIDTH+SPACING), 630 ), new Point( ( 2 * (WIDTH+SPACING) ) + WIDTH, 630 ), 
                    new Point( 3 * (WIDTH+SPACING), 630 ), new Point( ( 3 * (WIDTH+SPACING) ) + WIDTH, 630 ), 
                    new Point( 4 * (WIDTH+SPACING), 630 ), new Point( ( 4 * (WIDTH+SPACING) ) + WIDTH, 630 ), 
                    new Point( 5 * (WIDTH+SPACING), 630 ), new Point( ( 5 * (WIDTH+SPACING) ) + WIDTH, 626 ), 
                    new Point( 6 * (WIDTH+SPACING), 626 ), new Point( ( 6 * (WIDTH+SPACING) ) + WIDTH, 622 ) };
            
            _outputRays = new ArrayList();
            int order = 0;
            for ( int i = 0; i < points.length; ) {
                // Path
                GeneralPath path = new GeneralPath();
                path.moveTo( focalPoint.x, focalPoint.y );
                path.lineTo( points[i].x, points[i].y );
                i++;
                path.lineTo( points[i].x, points[i].y );
                i++;
                path.closePath();
                // Ray
                PhetShapeGraphic ray = new PhetShapeGraphic( component );
                ray.setShape( path );
                ray.setLocation( 0, 0 );
                addGraphic( ray );
                _outputRays.add( ray );
            }
        }
        
        // Beam from input pulse generator to input diffraction grating
        PhetShapeGraphic inputBeam = new PhetShapeGraphic( component );
        inputBeam.setShape( new Rectangle( 0, 0, 10, 130 ) );
        inputBeam.setColor( new Color( 255, 255, 255, MAX_ALPHA ) );
        inputBeam.rotate( Math.toRadians( 8 ) );
        inputBeam.setLocation( 361, 99 );
        inputBeam.setIgnoreMouse( true );
        addGraphic( inputBeam );
        
        // Beam from output diffraction grating to molecule
        _outputBeam = new PhetShapeGraphic( component );
        _outputBeam.setShape( new Rectangle( 0, 0, 10, 130 ) );
        _outputBeam.rotate( Math.toRadians( -180 - 8 ) );
        _outputBeam.setLocation( 361 + 10, 545 );
        _outputBeam.setIgnoreMouse( true );
        addGraphic( _outputBeam );
        
        update();
    }
    
    /**
     * Call this method before releasing all references to an object of this type.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the Fourier series changes, adjusts the alpha component of
     * light colors on the output side of the amplitude sliders.
     */
    public void update() {
        
        assert( MAX_ALPHA > MIN_ALPHA );
        
        int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();
        
        // Adjust rainbow colors
        double amplitudesSum = 0;
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            
            double amplitude = _fourierSeries.getHarmonic( i ).getAmplitude();
            int alpha = 0;
            if ( Math.abs( amplitude ) > 0 ) {
                alpha = MIN_ALPHA + (int) Math.abs( ( MAX_ALPHA - MIN_ALPHA ) * amplitude / OQCConstants.MAX_HARMONIC_AMPLITUDE );
            }
            Color color = HarmonicColors.getInstance().getColor( i );
            Color colorWithAlpha = new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha );
            
            PhetShapeGraphic bar = (PhetShapeGraphic) _outputBars.get( i );
            bar.setColor( colorWithAlpha );
            PhetShapeGraphic ray = (PhetShapeGraphic) _outputRays.get( i );
            ray.setColor( colorWithAlpha );
            
            amplitudesSum += Math.abs( amplitude );
        }
        
        // Adjust coherent (white) light beam
        int alpha = 0;
        if ( amplitudesSum > 0 ) {
            alpha = MIN_ALPHA + (int) Math.abs( ( MAX_ALPHA - MIN_ALPHA ) * amplitudesSum / ( numberOfHarmonics * OQCConstants.MAX_HARMONIC_AMPLITUDE ) );
        }
        Color color = new Color( 255, 255, 255, alpha );
        _outputBeam.setColor( color );
    }
}
