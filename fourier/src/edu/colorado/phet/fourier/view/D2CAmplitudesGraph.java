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

import edu.colorado.phet.chart.BarPlot;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.charts.D2CAmplitudesChart;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.view.tools.MeasurementTool;


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
    private static final double TOOL_LAYER = 4;
    
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
    private static final Dimension CHART_SIZE = new Dimension( 575, 160 );
    
    // Bars in the chart
    private static final int BAR_DARKEST_GRAY = 0; //dark gray
    private static final int BAR_LIGHTEST_GRAY = 230;  // light gray
    
    // Tools
    private static final Font TOOL_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 16 );
    private static final Color TOOL_COLOR = Color.RED;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesChart _chartGraphic;
    private String _xTitleSpace, _xTitleTime;
    private MeasurementTool _spacingTool;
    private MeasurementTool _widthTool;
    
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
        _xTitleSpace = SimStrings.get( "D2CAmplitudesGraph.xTitleSpace" );
        _xTitleTime = SimStrings.get( "D2CAmplitudesGraph.xTitleTime" );
        _chartGraphic.setXAxisTitle( _xTitleSpace );
        _chartGraphic.setRegistrationPoint( 0, CHART_SIZE.height / 2 ); // at the chart's origin
        _chartGraphic.setLocation( 60, 15 + (CHART_SIZE.height / 2) );
        addGraphic( _chartGraphic, CHART_LAYER );       
        
        // Width measurement tool
        _widthTool = new MeasurementTool( component );
        _widthTool.setLabelFont( TOOL_FONT );
        _widthTool.setLabelColor( TOOL_COLOR );
        _widthTool.setFillColor( TOOL_COLOR );
        _widthTool.setLocation( 540, 40  );
        addGraphic( _widthTool, TOOL_LAYER );
        
        // Spacing measurement tool
        _spacingTool = new MeasurementTool( component );
        _spacingTool.setLabelFont( TOOL_FONT );
        _spacingTool.setLabelColor( TOOL_COLOR );
        _spacingTool.setFillColor( TOOL_COLOR );
        _spacingTool.setLocation( 590, 140 );
        addGraphic( _spacingTool, TOOL_LAYER );
        
        // Interactivity
        titleGraphic.setIgnoreMouse( true );
        _chartGraphic.setIgnoreMouse( true );
        
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
        update();
        setDomain( FourierConstants.DOMAIN_SPACE );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the domain.
     * Changes various labels on the chart, tools, etc.
     * 
     * @param domain DOMAIN_SPACE or DOMAIN_TIME
     * @throws IllegalArgumentException if the domain is invalid or not supported
     */
    public void setDomain( int domain ) {
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            _chartGraphic.setXAxisTitle( _xTitleSpace );
            _spacingTool.setLabel( "<html>k<sub>1</sub></html>" );
            _widthTool.setLabel( "<html>2\u0394k</html>" );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            _chartGraphic.setXAxisTitle( _xTitleTime );
            _spacingTool.setLabel( "<html>\u03C9<sub>1</sub></html>" );
            _widthTool.setLabel( "<html>2\u0394\u03C9</html>" );
        }
        else {
            throw new IllegalArgumentException( "unsupported domain: " + domain );
        }
    }
    
    /**
     * Gets a reference to the spacing measurement tool.
     * 
     * @return MeasurementTool
     */
    public MeasurementTool getSpacingTool() {
        return _spacingTool;
    }
    
    /**
     * Gets a reference to the width measurement tool.
     * 
     * @return MeasurementTool
     */
    public MeasurementTool getWidthTool() {
        return _widthTool;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     * Called when the wave packet notifies us that it has been changed.
     */
    public void update() {
        
        System.out.println( "D2CAmplitudesChart.update" ); //XXX
        
        _chartGraphic.removeAllDataSetGraphics();
        
        double k1 = _wavePacket.getK1();
        double k0 = _wavePacket.getK0();
        double dk = _wavePacket.getDeltaK();
        
        if ( k1 > 0 ) {
            
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
            
//            System.out.println( "number of components = " + numberOfComponents );//XXX
//            System.out.println( "max amplitude = " + maxAmplitude );//XXX
            
            _chartGraphic.autoscaleY( maxAmplitude );
        }
        else {
            //XXX do something else when k1=0
        }
        
        // Spacing measurement tool.
        {
            float width = (float) _chartGraphic.transformXDouble( k1 );
            _spacingTool.setToolWidth( width );
        }
        
        // Width measurement tool 
        {
            float width = (float) ( 2 * _chartGraphic.transformXDouble( dk ) );
            _widthTool.setToolWidth( width );
        }
    }

}