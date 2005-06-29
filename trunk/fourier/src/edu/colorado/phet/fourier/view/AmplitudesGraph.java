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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetFlattenedGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * AmplitudesGraph is the control interface for setting the amplitudes
 * of the harmonics in a Fourier series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudesGraph extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double FLATTENED_LAYER = 1;
    private static final double SLIDERS_LAYER = 4;
    
    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 210 );
    private static final Color BACKGROUND_COLOR = new Color( 195, 195, 195 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -20; // from origin
    
    // Axis parameters
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Font AXIS_TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;
    
    // Range labels
    private static final boolean RANGE_LABELS_VISIBLE = false;
    private static final NumberFormat RANGE_LABELS_FORMAT = new DecimalFormat( "0.00" );
    
    // X axis
    private static final double X_MIN = FourierConfig.MIN_HARMONICS;
    private static final double X_MAX = FourierConfig.MAX_HARMONICS;
    
    // Y axis
    private static final double Y_MIN = -FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAX = +FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAJOR_TICK_SPACING = 0.5;
    private static final double Y_MINOR_TICK_SPACING = 0.1;
    private static final Stroke Y_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font Y_MAJOR_TICK_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 12 );
    
    // Gridlines
    private static final boolean MAJOR_GRIDLINES_ENABLED = true;
    private static final boolean MINOR_GRIDLINES_ENABLED = false;
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.5f );
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    
    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 650, 130 );
    
    // Sliders parameters
    private static final int SLIDER_SPACING = 10; // space between sliders
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private GraphicLayerSet _slidersGraphic;
    private ArrayList _sliders; // array of AmplitudeSlider
    private EventListenerList _listenerList;
    private EventPropagator _eventPropagator;
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
    public AmplitudesGraph( Component component, FourierSeries fourierSeries ) {
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
        backgroundGraphic.setLocation( -100, -125 );
        
        // Title
        String title = SimStrings.get( "AmplitudesGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
        
        // Chart
        Chart chartGraphic = new Chart( component, CHART_RANGE, CHART_SIZE ); 
        {
            chartGraphic.setLocation( 0, -( CHART_SIZE.height / 2 ) );
            
            // X axis
            {
                chartGraphic.getXAxis().setStroke( AXIS_STROKE );
                chartGraphic.getXAxis().setColor( AXIS_COLOR );

                String xTitle = String.valueOf( MathStrings.C_MODE );
                PhetTextGraphic xAxisTitleGraphic = new PhetTextGraphic( component, AXIS_TITLE_FONT, xTitle, AXIS_TITLE_COLOR );
                chartGraphic.setXAxisTitle( xAxisTitleGraphic );
                
                // No ticks, labels or gridlines
                chartGraphic.getHorizontalTicks().setVisible( false );
                chartGraphic.getXAxis().setMajorTicksVisible( false );
                chartGraphic.getXAxis().setMinorTicksVisible( false );
                chartGraphic.getXAxis().setMajorTickLabelsVisible( false );
                chartGraphic.getXAxis().setMinorTickLabelsVisible( false );
                chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( false );
                chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( false );
            }
            
            // Y axis
            {
                chartGraphic.getYAxis().setStroke( AXIS_STROKE );
                chartGraphic.getYAxis().setColor( AXIS_COLOR );
                
                // No ticks or labels on the axis
                chartGraphic.getYAxis().setMajorTicksVisible( false );
                chartGraphic.getYAxis().setMajorTickLabelsVisible( false );
                chartGraphic.getYAxis().setMinorTicksVisible( false );
                chartGraphic.getYAxis().setMinorTickLabelsVisible( false );

                // Range labels
                chartGraphic.getVerticalTicks().setRangeLabelsVisible( RANGE_LABELS_VISIBLE );
                chartGraphic.getVerticalTicks().setRangeLabelsNumberFormat( RANGE_LABELS_FORMAT );
                
                // Major ticks and labels to the left of the chart
                chartGraphic.getVerticalTicks().setMajorTicksVisible( true );
                chartGraphic.getVerticalTicks().setMajorTickLabelsVisible( true );
                chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                chartGraphic.getVerticalTicks().setMajorTickStroke( Y_MAJOR_TICK_STROKE );
                chartGraphic.getVerticalTicks().setMajorTickFont( Y_MAJOR_TICK_FONT );

                // Major gridlines
                chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( MAJOR_GRIDLINES_ENABLED );
                chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

                // Minor gridlines
                chartGraphic.getHorizonalGridlines().setMinorGridlinesVisible( MINOR_GRIDLINES_ENABLED );
                chartGraphic.getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
                chartGraphic.getHorizonalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
                chartGraphic.getHorizonalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
            }
        }
        
        // Flatten all of the static graphics.
        PhetFlattenedGraphic flattenedGraphic = new PhetFlattenedGraphic( component );
        addGraphic( flattenedGraphic, FLATTENED_LAYER );
        flattenedGraphic.addGraphic( backgroundGraphic );
        flattenedGraphic.addGraphic( titleGraphic );
        flattenedGraphic.addGraphic( chartGraphic );
        flattenedGraphic.flatten();
        flattenedGraphic.setLocation( 0, 0 );
        
        // Container for amplitude sliders -- sliders to be added in update.
        _slidersGraphic = new GraphicLayerSet( component );
        addGraphic( _slidersGraphic, SLIDERS_LAYER );
        
        // Interactivity
        {
            flattenedGraphic.setIgnoreMouse( true );
        }
        
        // Misc initialization
        {
            _sliders = new ArrayList();
            _listenerList = new EventListenerList();
            _eventPropagator = new EventPropagator();
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
            
            //FourierLog.trace( "AmplitudesGraph.update" );
            
            _slidersGraphic.clear();

            int totalSpace = ( FourierConfig.MAX_HARMONICS + 1 ) * SLIDER_SPACING;
            int barWidth = ( CHART_SIZE.width - totalSpace ) / FourierConfig.MAX_HARMONICS;
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
                    slider.addHarmonicFocusListener( _eventPropagator ); // notify when the slider gets focus
                    slider.addChangeListener( _eventPropagator ); // notify when the slider value is changed
                }
                _slidersGraphic.addGraphic( slider );

                // Slider size.
                slider.setMaxSize( barWidth, CHART_SIZE.height );

                // Slider location.
                int x = ( ( i + 1 ) * SLIDER_SPACING ) + ( i * barWidth ) + ( barWidth / 2 );
                slider.setLocation( x, 0 );
                
                _previousNumberOfHarmonics = numberOfHarmonics;
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Adds a HarmonicFocusListener.
     * 
     * @param listener
     */
    public void addHarmonicFocusListener( HarmonicFocusListener listener ) {
        _listenerList.add( HarmonicFocusListener.class, listener );
    }
  
    /**
     * Removes a HarmonicFocusListener.
     * 
     * @param listener
     */
    public void removeHarmonicFocusListener( HarmonicFocusListener listener ) {
        _listenerList.remove( HarmonicFocusListener.class, listener );
    }
    
    /**
     * Adds a ChangeListener.
     * 
     * @param listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }
  
    /**
     * Removes a ChangeListener.
     * 
     * @param listener
     */
    public void removeChangeListenerListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }
    
    /*
     * Propogates events from AmplitudeSliders to listeners.
     */
    private class EventPropagator implements ChangeListener, HarmonicFocusListener {

        /* Invoked when an amplitude slider is moved. */
        public void stateChanged( ChangeEvent event ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == ChangeListener.class ) {
                    ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
                }
            }
        }
        
        /* Invoked when an amplitude slider gains focus. */
        public void focusGained( HarmonicFocusEvent event ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == HarmonicFocusListener.class ) {
                    ( (HarmonicFocusListener) listeners[i + 1] ).focusGained( event );
                }
            }
        }

        /* Invoked when an amplitude slider loses focus. */
        public void focusLost( HarmonicFocusEvent event ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == HarmonicFocusListener.class ) {
                    ( (HarmonicFocusListener) listeners[i + 1] ).focusLost( event );
                }
            }
        }
    }
}