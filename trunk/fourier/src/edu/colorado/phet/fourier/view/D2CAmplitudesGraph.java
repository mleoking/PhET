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
import java.util.ArrayList;

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
import edu.colorado.phet.fourier.charts.D2CAmplitudesChart;
import edu.colorado.phet.fourier.charts.GeneralPathPlot;
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
    private static final double MATH_LAYER = 4;
    
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
    private static final double X_MAX = 24 * Math.PI; // this remains constant
    private static final double Y_MIN = 0;
    private static final double Y_MAX = 1; // this changes dynamically
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 575, 140 );
    
    // Bars in the chart
    private static final int BAR_DARKEST_GRAY = 0; //dark gray
    private static final int BAR_LIGHTEST_GRAY = 230;  // light gray
    
    // Continuous waveform
    private static final double CONTINUOUS_STEP = Math.PI / 10; // about one value for every 2 pixels
    private static final Color CONTINUOUS_COLOR = Color.BLACK;
    private static final Stroke CONTINUOUS_STROKE = new BasicStroke( 2f );
    
    // Math equation
    private static final Font MATH_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color MATH_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesChart _chartGraphic;
    private LinePlot _continuousWaveformGraphic;
    private boolean _continuousEnabled;
    private GeneralPathPlot _gradientPlot;
    private HTMLGraphic _mathGraphic;
    private int _domain;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param wavePacket the Gaussian wave packet being displayed
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
        _chartGraphic = new D2CAmplitudesChart( component, CHART_RANGE, CHART_SIZE );
        _chartGraphic.setRegistrationPoint( 0, CHART_SIZE.height / 2 ); // at the chart's origin
        _chartGraphic.setLocation( 60, 35 + (CHART_SIZE.height / 2) );
        addGraphic( _chartGraphic, CHART_LAYER );
        
        // Gradient-filled waveform
        Color darkestColor = new Color( BAR_DARKEST_GRAY, BAR_DARKEST_GRAY, BAR_DARKEST_GRAY );
        Color lightestColor = new Color( BAR_LIGHTEST_GRAY, BAR_LIGHTEST_GRAY, BAR_LIGHTEST_GRAY );
        GradientPaint gradient = new GradientPaint( 0, 0, darkestColor, CHART_SIZE.width, 0, lightestColor );
        _gradientPlot = new GeneralPathPlot( getComponent(), _chartGraphic, gradient );
        
        // Continuous waveform
        _continuousWaveformGraphic = new LinePlot( component, _chartGraphic );
        _continuousWaveformGraphic.setBorderColor( CONTINUOUS_COLOR );
        _continuousWaveformGraphic.setStroke( CONTINUOUS_STROKE );
        _continuousWaveformGraphic.setDataSet( new DataSet() );
        
        // Math equation
        {
            _mathGraphic = new HTMLGraphic( component, MATH_FONT, "", MATH_COLOR );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.centerRegistrationPoint();
            // Location is above the center of the chart.
            int x = _chartGraphic.getX() + ( CHART_SIZE.width / 2 );
            int y = 15;
            _mathGraphic.setLocation( x, y );
        }
        
        // Interactivity
        setIgnoreMouse( true );  // nothing is interactive in this view
        
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
        _continuousEnabled = false;
        setDomain( FourierConstants.DOMAIN_SPACE );
        update();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the domain.
     * Changes various labels on the chart, tools, formulas, etc.
     * 
     * @param domain DOMAIN_SPACE or DOMAIN_TIME
     */
    public void setDomain( int domain ) {
        assert( FourierConstants.isValidDomain( domain ) );
        _domain = domain;
        updateMath();
        updateAxisTitles();
    }
    
    /**
     * Gets a reference to the chart.
     * 
     * @return Chart
     */    
    public Chart getChart() {
        return _chartGraphic;
    }
    
    /**
     * Turns the continuous waveform display on and off.
     * 
     * @param enabled
     */
    public void setContinuousEnabled( boolean enabled ) {
        _continuousEnabled = enabled;
        if ( enabled ) {
            updateContinuous();
            _chartGraphic.addDataSetGraphic( _continuousWaveformGraphic );
        }
        else {
            _chartGraphic.removeDataSetGraphic( _continuousWaveformGraphic );
        }
    }
    
    /**
     * Is the continuous waveform display enabled?
     * 
     * @return true or false
     */
    public boolean isContinuousEnabled() {
        return _continuousEnabled;
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
        
        updateMath(); // ...in case k1 has changed
        
        _chartGraphic.removeAllDataSetGraphics();
        
        double k1 = _wavePacket.getK1();
        if ( k1 > 0 ) {
            addBarPlots();
        }
        else {
            addGeneralPathPlot();
        }
        
        // Update the continuous waveform display if it's enabled.
        if ( _continuousEnabled ) {
            updateContinuous();
            _chartGraphic.addDataSetGraphic( _continuousWaveformGraphic );
        }
    }
    
    //----------------------------------------------------------------------------
    // Methods that construct the chart's data set graphics
    //----------------------------------------------------------------------------
    
    /*
     * Adds a set of BarPlots that represent the discrete components 
     * of the wave packet.
     */
    private void addBarPlots() {
        
        double k1 = _wavePacket.getK1();
        assert( k1 > 0 );
        double k0 = _wavePacket.getK0();
        double dk = _wavePacket.getDeltaK();

        // Number of components
        int numberOfComponents = _wavePacket.getNumberOfComponents();

        // Change in grayscale value between bars.
        int deltaColor = ( BAR_DARKEST_GRAY - BAR_LIGHTEST_GRAY ) / numberOfComponents;

        // Width of the bars is slightly less than the spacing k1.
        double barWidth = k1 - ( k1 * 0.25 );

        double maxAmplitude = 0;

        // Add a bar for each component.
        for ( int i = 0; i < numberOfComponents; i++ ) {

            // Compute the bar color (grayscale).
            int r = BAR_DARKEST_GRAY - ( i * deltaColor );
            int g = BAR_DARKEST_GRAY - ( i * deltaColor );
            int b = BAR_DARKEST_GRAY - ( i * deltaColor );
            Color barColor = new Color( r, g, b );

            // Compute the bar graphic.
            BarPlot barPlot = new BarPlot( getComponent(), _chartGraphic, barWidth );
            barPlot.setFillColor( barColor );
            _chartGraphic.addDataSetGraphic( barPlot );

            // Set the bar's position (kn) and height (An).
            double kn = ( i + 1 ) * k1;
            double An = GaussianWavePacket.getAmplitude( kn, k0, dk ) * k1;
            DataSet dataSet = barPlot.getDataSet();
            dataSet.addPoint( new Point2D.Double( kn, An ) );

            if ( An > maxAmplitude ) {
                maxAmplitude = An;
            }
        }

        _chartGraphic.autoscaleY( maxAmplitude * FourierConfig.AUTOSCALE_PERCENTAGE );
        
        //            System.out.println( "number of components = " + numberOfComponents );//XXX
        //            System.out.println( "max amplitude = " + maxAmplitude );//XXX
    }
    
    /*
     * Populates a GeneralPathPlot that displays the continuous waveform as a 
     * shape that is filled with a gradient.
     */
    private void addGeneralPathPlot() {
        
        DataSet dataSet = _gradientPlot.getDataSet();
        dataSet.clear();
        
        // Compute the points that approximate the waveform.
        double k0 = _wavePacket.getK0();
        double dk = _wavePacket.getDeltaK();
        double maxAmplitude = 0;
        double k = X_MIN;
        ArrayList points = new ArrayList(); // array of Point2D
        points.add( new Point2D.Double( k, 0 ) );
        while ( k <= X_MAX + Math.PI ) {
            double amplitude = GaussianWavePacket.getAmplitude( k, k0, dk );
            points.add( new Point2D.Double( k, amplitude ) );
            k += CONTINUOUS_STEP;
            
            if ( amplitude > maxAmplitude ) {
                maxAmplitude = amplitude;
            }
        }
        points.add( new Point2D.Double( k, 0 ) );

        // Add the points to the data set.
        dataSet.addPoints( (Point2D.Double[]) points.toArray( new Point2D.Double[points.size()] ) );
        
        _chartGraphic.addDataSetGraphic( _gradientPlot );
        _chartGraphic.autoscaleY( maxAmplitude * FourierConfig.AUTOSCALE_PERCENTAGE );
    }
    
    /*
     * Populates a LinePlot with a set of points that approximate the continuous waveform.
     */
    private void updateContinuous() {
        System.out.println( "D2CAmplitudesGraph.updateContinuous" );//XXX
        
        // Clear the data set.
        DataSet dataSet = _continuousWaveformGraphic.getDataSet();
        dataSet.clear();

        // Get various wave packet parameters that we'll need to compute the amplitude values.
        double dk = _wavePacket.getDeltaK();
        double k0 = _wavePacket.getK0();
        double k1 = _wavePacket.getK1();
        
        // Compute the points that approximate the waveform.
        double k = X_MIN;
        ArrayList points = new ArrayList(); // array of Point2D
        while ( k <= X_MAX + Math.PI ) {
            double amplitude = GaussianWavePacket.getAmplitude( k, k0, dk );
            if ( k1 != 0 ) {
                amplitude *= k1;
            }
            points.add( new Point2D.Double( k, amplitude ) );
            k += CONTINUOUS_STEP;
        }
        
        // Add the points to the data set.
        dataSet.addPoints( (Point2D.Double[]) points.toArray( new Point2D.Double[points.size()] ) );
    }
    
    /*
     * Updates the math equation that appears above the graph.
     */
    private void updateMath() {
        if ( _wavePacket.getK1() != 0 ) {
            // A(n)
            _mathGraphic.setHTML( "<html>A(n)</html>" );
        }
        else {
            if ( _domain == FourierConstants.DOMAIN_SPACE ) {
                // A(k)
                _mathGraphic.setHTML( "<html>A(k)</html>" );
            }
            else if ( _domain == FourierConstants.DOMAIN_TIME ) {
                // A(w)
                _mathGraphic.setHTML( "<html>A(" + MathStrings.C_OMEGA + ")</html>" );
            }
        }
        _mathGraphic.centerRegistrationPoint();
    }
    
    /*
     * Update the titles on the axes.
     */
    private void updateAxisTitles() {
        if ( _domain == FourierConstants.DOMAIN_SPACE ) {
            _chartGraphic.setXAxisTitle( SimStrings.get( "D2CAmplitudesGraph.xTitleSpace" ) );
        }
        else if ( _domain == FourierConstants.DOMAIN_TIME ) {
            _chartGraphic.setXAxisTitle( SimStrings.get( "D2CAmplitudesGraph.xTitleTime" ) );
        }
    }
}