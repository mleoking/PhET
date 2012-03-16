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
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCConstants;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;
import edu.colorado.phet.opticalquantumcontrol.charts.FlattenedChart;
import edu.colorado.phet.opticalquantumcontrol.model.FourierSeries;
import edu.colorado.phet.opticalquantumcontrol.model.Harmonic;


/**
 * AmplitudesView provides the interface for setting the amplitudes
 * of the harmonics in a Fourier series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudesView extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double SLIDERS_LAYER = 4;
    private static final double BUTTONS_LAYER = 5;
    
    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 355, 265 );
    private static final Color BACKGROUND_COLOR = new Color( 195, 195, 195 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLACK;
    
    // Chart parameters
    private static final double X_MIN = OQCConstants.MIN_HARMONICS;
    private static final double X_MAX = OQCConstants.MAX_HARMONICS;
    private static final double Y_MIN = -OQCConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAX = +OQCConstants.MAX_HARMONIC_AMPLITUDE;
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 275, 200 );
    
    // Sliders parameters
    private static final int SLIDER_SPACING = 10; // space between sliders
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private FlattenedChart _flattenedChart;
    private GraphicLayerSet _slidersGraphic;
    private ArrayList _sliders; // array of AmplitudeSlider
    private EventListenerList _listenerList;
    private int _previousNumberOfHarmonics;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param fourierSeries the model that this graphic controls
     */
    public AmplitudesView( Component component, FourierSeries fourierSeries ) {
        super( component );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Model
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        // Background
        PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component );
        backgroundGraphic.setShape( new RoundRectangle2D.Double( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height, 20, 20 ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        backgroundGraphic.setStroke( BACKGROUND_STROKE );
        backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        backgroundGraphic.setLocation( 0, 0 );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        
        // Title
        String title = OQCResources.AMPLITUDES;
        HTMLGraphic titleGraphic = new HTMLGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.setRegistrationPoint( titleGraphic.getWidth()/2, 0 ); // top center
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( 10, BACKGROUND_SIZE.height/2 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Flattened Chart
        {
            AmplitudesChart chartGraphic = new AmplitudesChart( component, CHART_RANGE, CHART_SIZE );
            chartGraphic.setLocation( 0, 0 );
            chartGraphic.setRegistrationPoint( 0, 0 );

            int xOffset = 25; // distance between the left edge and the chart's origin.
            int yOffset = 10;
            _flattenedChart = new FlattenedChart( component, chartGraphic, xOffset, yOffset );
            addGraphic( _flattenedChart, CHART_LAYER );
            _flattenedChart.setRegistrationPoint( xOffset, CHART_SIZE.height / 2 ); // at the chart's origin
            _flattenedChart.setLocation( 60, 50 + ( CHART_SIZE.height / 2 ) - yOffset );
        }
        
        // Container for amplitude sliders -- sliders to be added in update.
        _slidersGraphic = new GraphicLayerSet( component );
        addGraphic( _slidersGraphic, SLIDERS_LAYER );
        
        // Interactivity
        {
            backgroundGraphic.setIgnoreMouse( true );
            titleGraphic.setIgnoreMouse( true );
            _flattenedChart.setIgnoreMouse( true );
            
            // sliders handle their own interactivity
        }
        
        // Misc initialization
        {
            _sliders = new ArrayList();
            _listenerList = new EventListenerList();
        }
        
        reset();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
    }
    
    /**
     * Resets to the initial state.
     */
    public void reset() {
        _previousNumberOfHarmonics = 0; // force an update
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update() {

        int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();

        if ( numberOfHarmonics != _previousNumberOfHarmonics ) {
            
            _slidersGraphic.clear();

            int totalSpace = ( OQCConstants.MAX_HARMONICS + 1 ) * SLIDER_SPACING;
            int barWidth = ( CHART_SIZE.width - totalSpace ) / OQCConstants.MAX_HARMONICS;
            double deltaWavelength = ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH ) / ( numberOfHarmonics - 1 );

            for ( int i = 0; i < numberOfHarmonics; i++ ) {

                // Get the ith harmonic.
                Harmonic harmonic = _fourierSeries.getHarmonic( i );

                AmplitudeSlider slider = null;
                if ( i < _sliders.size() ) {
                    // Reuse an existing slider.
                    slider = (AmplitudeSlider) _sliders.get( i );
                    slider.setHarmonic( harmonic );
                }
                else {
                    // Allocate a new slider.
                    slider = new AmplitudeSlider( getComponent(), harmonic );
                    _sliders.add( slider );
                }
                _slidersGraphic.addGraphic( slider );

                // Slider size.
                slider.setMaxSize( barWidth, CHART_SIZE.height );

                // Slider location.
                int x = _flattenedChart.getLocation().x + ( ( i + 1 ) * SLIDER_SPACING ) + ( i * barWidth ) + ( barWidth / 2 );
                int y = _flattenedChart.getLocation().y + 10;
                slider.setLocation( x, y );
                
                _previousNumberOfHarmonics = numberOfHarmonics;
            }
        }
    }
}