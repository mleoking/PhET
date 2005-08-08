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

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.StringLabelTable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * D2CAmplitudesGraph is the Amplitudes view in the "Discrete To Continuous" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CAmplitudesGraph extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    
    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 195 );
    private static final Color BACKGROUND_COLOR = new Color( 195, 195, 195 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    // Chart parameters
    private static final double X_MIN = 0;
    private static final double X_MAX = 22 * Math.PI;
    private static final double Y_MIN = 0;
    private static final double Y_MAX = 1;
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 575, 160 );
    
    // Axis parameters
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Font AXIS_TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 12 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;
    
    // Range labels
    private static final boolean RANGE_LABELS_VISIBLE = false;
    private static final NumberFormat RANGE_LABELS_FORMAT = new DecimalFormat( "0.00" );
    
    // X axis 
    private static final double X_MAJOR_TICK_SPACING = 2 * Math.PI;
    private static final double X_MINOR_TICK_SPACING = Math.PI / 2;
    private static final Stroke X_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke X_MINOR_TICK_STROKE = new BasicStroke( 0.5f );
    private static final Font X_MAJOR_TICK_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 12 );
    
    // Y axis
    private static final double Y_MAJOR_TICK_SPACING = 0.2;
    private static final double Y_MINOR_TICK_SPACING = 0.1;
    private static final Stroke Y_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_TICK_STROKE = new BasicStroke( 0.5f );
    private static final Font Y_MAJOR_TICK_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 12 );
    
    // Gridlines
    private static final boolean MAJOR_GRIDLINES_ENABLED = true;
    private static final boolean MINOR_GRIDLINES_ENABLED = false;
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private Chart _chartGraphic;
    private HTMLGraphic _xAxisTitleGraphic;
    private int _previousNumberOfHarmonics;
    private String _xTitleSpace, _xTitleTime;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param fourierSeries the model that this graphic controls
     */
    public D2CAmplitudesGraph( Component component, FourierSeries fourierSeries ) {
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
        String title = SimStrings.get( "D2CAmplitudesGraph.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( 40, BACKGROUND_SIZE.height/2 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Chart
        _chartGraphic = new Chart( component, CHART_RANGE, CHART_SIZE );
        _chartGraphic.setRegistrationPoint( 0, CHART_SIZE.height / 2 ); // at the chart's origin
        _chartGraphic.setLocation( 60, 15 + (CHART_SIZE.height / 2) );
        addGraphic( _chartGraphic, CHART_LAYER );        
        
        // X axis
        {
            _chartGraphic.getXAxis().setStroke( AXIS_STROKE );
            _chartGraphic.getXAxis().setColor( AXIS_COLOR );

            // Axis title
            _xTitleSpace = SimStrings.get( "D2CAmplitudesGraph.xTitleSpace" );
            _xTitleTime = SimStrings.get( "D2CAmplitudesGraph.xTitleTime" );
            _xAxisTitleGraphic = new HTMLGraphic( component, AXIS_TITLE_FONT, "", AXIS_TITLE_COLOR );
            _xAxisTitleGraphic.setHTML( _xTitleSpace );
            _chartGraphic.setXAxisTitle( _xAxisTitleGraphic );
            
            // Major ticks with labels
            {
                _chartGraphic.getXAxis().setMajorTicksVisible( false );
                _chartGraphic.getHorizontalTicks().setMajorTicksVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizontalTicks().setMajorTickStroke( X_MAJOR_TICK_STROKE );
                _chartGraphic.getHorizontalTicks().setMajorTickFont( X_MAJOR_TICK_FONT );
                // Symbolic labels
                StringLabelTable xAxisLabels = new StringLabelTable( getComponent(), X_MAJOR_TICK_FONT, Color.BLACK );
                xAxisLabels.put( 0, "0" );
                xAxisLabels.put( 2 * Math.PI, "2" + MathStrings.C_PI );
                xAxisLabels.put( 4 * Math.PI, "4" + MathStrings.C_PI );
                xAxisLabels.put( 6 * Math.PI, "6" + MathStrings.C_PI );
                xAxisLabels.put( 8 * Math.PI, "8" + MathStrings.C_PI );
                xAxisLabels.put( 10 * Math.PI, "10" + MathStrings.C_PI );
                xAxisLabels.put( 12 * Math.PI, "12" + MathStrings.C_PI );
                xAxisLabels.put( 14 * Math.PI, "14" + MathStrings.C_PI );
                xAxisLabels.put( 16 * Math.PI, "16" + MathStrings.C_PI );
                xAxisLabels.put( 18 * Math.PI, "18" + MathStrings.C_PI );
                xAxisLabels.put( 20 * Math.PI, "20" + MathStrings.C_PI );
                xAxisLabels.put( 22 * Math.PI, "22" + MathStrings.C_PI );
                _chartGraphic.getHorizontalTicks().setMajorLabels( xAxisLabels );
            }
            
            // Minor ticks with no labels
            _chartGraphic.getXAxis().setMinorTicksVisible( false );
            _chartGraphic.getHorizontalTicks().setMinorTicksVisible( true );
            _chartGraphic.getHorizontalTicks().setMinorTickLabelsVisible( false );
            _chartGraphic.getHorizontalTicks().setMinorTickSpacing( X_MINOR_TICK_SPACING );
            _chartGraphic.getHorizontalTicks().setMinorTickStroke( X_MINOR_TICK_STROKE );
            
            // No major gridlines
            _chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( false );
          
            // No minor gridlines
            _chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( false );
        }
        
        // Y axis
        {
            _chartGraphic.getYAxis().setStroke( AXIS_STROKE );
            _chartGraphic.getYAxis().setColor( AXIS_COLOR );
            
            // Major ticks with labels
            _chartGraphic.getYAxis().setMajorTicksVisible( false );
            _chartGraphic.getVerticalTicks().setMajorTicksVisible( true );
            _chartGraphic.getVerticalTicks().setMajorTickLabelsVisible( true );
            _chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            _chartGraphic.getVerticalTicks().setMajorTickStroke( Y_MAJOR_TICK_STROKE );
            _chartGraphic.getVerticalTicks().setMajorTickFont( Y_MAJOR_TICK_FONT );

            // Minor ticks with no labels
            _chartGraphic.getYAxis().setMinorTicksVisible( false );
            _chartGraphic.getVerticalTicks().setMinorTicksVisible( true );
            _chartGraphic.getVerticalTicks().setMinorTickLabelsVisible( false );
            _chartGraphic.getVerticalTicks().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
            _chartGraphic.getVerticalTicks().setMinorTickStroke( Y_MINOR_TICK_STROKE );
            
            // Major gridlines
            _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( true );
            _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            _chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
            _chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

            // No minor gridlines
            _chartGraphic.getHorizonalGridlines().setMinorGridlinesVisible( false );
        }
        
        // Interactivity
        setIgnoreMouse( true ); // nothing in this view is interactive
        
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
    //
    //----------------------------------------------------------------------------
    
    public void setDomain( int domain ) {
        assert( FourierConstants.isValidDomain( domain ) );
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            _xAxisTitleGraphic.setHTML( _xTitleSpace );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            _xAxisTitleGraphic.setHTML( _xTitleTime );
        }
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
            // XXX
            _previousNumberOfHarmonics = numberOfHarmonics;
        }
    }
}