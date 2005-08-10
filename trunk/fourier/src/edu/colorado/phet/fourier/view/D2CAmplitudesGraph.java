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
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


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
    
    // Bars in the chart
    private static final int BAR_DARKEST_GRAY = 50; //dark gray
    private static final int BAR_LIGHTEST_GRAY = 220;  // light gray
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
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
    public D2CAmplitudesGraph( Component component, GaussianWavePacket wavePacket ) {
        super( component );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Model
        _wavePacket = wavePacket;
        _wavePacket.addObserver( this );
        
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
        _wavePacket.removeObserver( this );
        _wavePacket = null;
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
    
    /**
     * Sets the domain.
     * This changes the label on the x axis.
     * 
     * @param domain DOMAIN_SPACE or DOMAIN_TIME
     * @throws IllegalArgumentException if the domain is invalid or not supported
     */
    public void setDomain( int domain ) {
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            _xAxisTitleGraphic.setHTML( _xTitleSpace );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            _xAxisTitleGraphic.setHTML( _xTitleTime );
        }
        else {
            throw new IllegalArgumentException( "unsupported domain: " + domain );
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     * Called when the wave packet notifies us that it has been changed.
     */
    public void update() {
        
        System.out.println( "D2CAmplitudesGraph.update" ); //XXX
        
        _chartGraphic.removeAllDataSetGraphics();
        
        double k1 = _wavePacket.getK1();
        if ( k1 > 0 ) {
            
            double k0 = X_MAX / 2;
            double dk = _wavePacket.getDeltaK();
            int numberOfComponents = (int) ( X_MAX / k1 ); //XXX not correct
            int deltaColor = ( BAR_DARKEST_GRAY - BAR_LIGHTEST_GRAY ) / numberOfComponents;
            
            // Add a bar for each component.
            for ( int i = 0; i < numberOfComponents; i++ ) {

                // Compute the bar color (grayscale).
                int r = BAR_DARKEST_GRAY - ( i * deltaColor );
                int g = BAR_DARKEST_GRAY - ( i * deltaColor );
                int b = BAR_DARKEST_GRAY - ( i * deltaColor );
                Color barColor = new Color( r, g, b );
                
                // Compute the width of the bar.
                double barWidth = Math.PI / 4; //XXX not correct
                
                // Compute the bar graphic.
                BarPlot barPlot = new BarPlot( getComponent(), _chartGraphic );
                barPlot.setBarWidth( barWidth );
                barPlot.setFillColor( barColor );
                _chartGraphic.addDataSetGraphic( barPlot );

                // Set the bar's position (kn) and height (An).
                double kn = ( i + 1 ) * Math.PI / 2; //XXX not correct
                double An = GaussianWavePacket.getAmplitude( kn, k0, dk ) * k1; //XXX not correct
                DataSet dataSet = barPlot.getDataSet();
                dataSet.addPoint( new Point2D.Double( kn, An ) );
                System.out.println( "A" + (i+1) + "=" + An );//XXX
            }
        }
        else {
            //XXX do something else when k1=0
        }
    }
}