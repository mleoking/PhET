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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.model.HarmonicSeries;


/**
 * AmplitudesGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudesGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double OUTLINE_LAYER = 1;
    private static final double LABELS_LAYER = 2;
    private static final double SLIDERS_LAYER = 3;
    
    private static final int OUTLINE_WIDTH = 550;
    private static final int OUTLINE_HEIGHT = 175;
    
    private static final int SPACING = 10; // space between sliders
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HarmonicSeries _harmonicSeriesModel;
    private Rectangle _outlineRectangle;
    private PhetShapeGraphic _outlineGraphic;
    private GraphicLayerSet _sliders;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public AmplitudesGraphic( Component component, HarmonicSeries harmonicSeriesModel ) {
        super( component );
        
        _harmonicSeriesModel = harmonicSeriesModel;
        _harmonicSeriesModel.addObserver( this );
        
        _outlineGraphic = new PhetShapeGraphic( component );
        _outlineRectangle = new Rectangle( 0, -OUTLINE_HEIGHT/2, OUTLINE_WIDTH, OUTLINE_HEIGHT );
        _outlineGraphic.setShape( _outlineRectangle );
        _outlineGraphic.setBorderColor( Color.BLACK );
        _outlineGraphic.setStroke( new BasicStroke( 1f ) );
        addGraphic( _outlineGraphic, OUTLINE_LAYER );
        
        _sliders = new GraphicLayerSet( component );
        addGraphic( _sliders, SLIDERS_LAYER );
        
        // Interactivity
        _outlineGraphic.setIgnoreMouse( true );
        
        update();
    }
    
    public void finalize() {
        _harmonicSeriesModel.removeObserver( this );
        _harmonicSeriesModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        
        _sliders.clear();
        
        int numberOfHarmonics = _harmonicSeriesModel.getNumberOfHarmonics();

        int totalSpace = ( numberOfHarmonics + 1 ) * SPACING;
        int barWidth = ( OUTLINE_WIDTH - totalSpace ) / numberOfHarmonics;
        double deltaWavelength = ( VisibleColor.MAX_WAVELENGTH -  VisibleColor.MIN_WAVELENGTH ) / ( numberOfHarmonics - 1 );
        
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            
            // Get the ith harmonic.
            Harmonic harmonic =  _harmonicSeriesModel.getHarmonic(i);
            
            // Create a slider to control the harmonic's amplitude.
            HarmonicSlider slider = new HarmonicSlider( getComponent(), harmonic );
            _sliders.addGraphic( slider );
            
            // Slider color, from the visible spectrum.
            slider.setMaxSize( barWidth, OUTLINE_HEIGHT );
            double wavelength = VisibleColor.MAX_WAVELENGTH - ( i * deltaWavelength );
            Color trackColor = VisibleColor.wavelengthToColor( wavelength );
            slider.setTrackColor( trackColor );
            
            // Slider location.
            int x = ( ( i + 1 ) * SPACING ) + ( i * barWidth ) + ( barWidth / 2 );
            slider.setLocation( x, 0 );
        }
    }
}
