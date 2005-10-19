/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.charts.FlattenedChart;
import edu.colorado.phet.shaper.model.FourierSeries;
import edu.colorado.phet.shaper.model.Harmonic;


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
    private static final Dimension BACKGROUND_SIZE = new Dimension( 355, 195 );
    private static final Color BACKGROUND_COLOR = new Color( 195, 195, 195 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    // Chart parameters
    private static final double X_MIN = ShaperConstants.MIN_HARMONICS;
    private static final double X_MAX = ShaperConstants.MAX_HARMONICS;
    private static final double Y_MIN = -ShaperConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAX = +ShaperConstants.MAX_HARMONIC_AMPLITUDE;
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 275, 130 );
    
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
    private JButton _resetButton;
    private PhetGraphic _resetButtonGraphic;
    
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
        backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        backgroundGraphic.setStroke( BACKGROUND_STROKE );
        backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        backgroundGraphic.setLocation( 0, 0 );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        
        // Title
        String title = SimStrings.get( "AmplitudesView.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( 40, BACKGROUND_SIZE.height/2 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Flattened Chart
        {
            AmplitudesChart chartGraphic = new AmplitudesChart( component, CHART_RANGE, CHART_SIZE );
            chartGraphic.setLocation( 0, 0 );
            chartGraphic.setRegistrationPoint( 0, 0 );

            int xOffset = 25; // distance between the left edge and the chart's origin.
            int yOffset = 0;
            _flattenedChart = new FlattenedChart( component, chartGraphic, xOffset, yOffset );
            addGraphic( _flattenedChart, CHART_LAYER );
            _flattenedChart.setRegistrationPoint( xOffset, CHART_SIZE.height / 2 ); // at the chart's origin
            _flattenedChart.setLocation( 60, 50 + ( CHART_SIZE.height / 2 ) - yOffset );
        }
        
        // Container for amplitude sliders -- sliders to be added in update.
        _slidersGraphic = new GraphicLayerSet( component );
        addGraphic( _slidersGraphic, SLIDERS_LAYER );
        
        // Reset button
        {
            _resetButton = new JButton( "Reset" );
            _resetButton.setOpaque( false );
            _resetButtonGraphic = PhetJComponent.newInstance( component, _resetButton );
            addGraphic( _resetButtonGraphic, BUTTONS_LAYER );
            _resetButtonGraphic.setLocation( 0, 0 );
            _resetButtonGraphic.scale( 0.7 );
            
            _resetButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    // Set all the harmonic amplitudes to zero.
                    int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();
                    for ( int i = 0; i < numberOfHarmonics; i++ ) {
                        _fourierSeries.getHarmonic( i ).setAmplitude( 0 );
                    }
                }
            } );
        }
        
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
    // Accessors
    //----------------------------------------------------------------------------
    
    public ArrayList getSliders() {
        return _sliders;
    }
    
    public FourierSeries getFourierSeries() {
        return _fourierSeries;
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

            int totalSpace = ( ShaperConstants.MAX_HARMONICS + 1 ) * SLIDER_SPACING;
            int barWidth = ( CHART_SIZE.width - totalSpace ) / ShaperConstants.MAX_HARMONICS;
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
                int y = _flattenedChart.getLocation().y;
                slider.setLocation( x, y );
                
                _previousNumberOfHarmonics = numberOfHarmonics;
            }
        }
    }
}