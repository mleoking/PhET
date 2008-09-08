/* Copyright 2005, University of Colorado */

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

import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCConstants;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;
import edu.colorado.phet.opticalquantumcontrol.charts.FourierSumPlot;
import edu.colorado.phet.opticalquantumcontrol.charts.PulseChart;
import edu.colorado.phet.opticalquantumcontrol.model.FourierSeries;


/**
 * OutputPulseView displays the output pulse.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OutputPulseView extends GraphicLayerSet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double SPIGOT_LAYER = 2;
    private static final double TITLE_LAYER = 3;
    private static final double CHART_LAYER = 4;

    // Background parameters
    private static final int MIN_HEIGHT = 150;
    private static final Dimension BACKGROUND_SIZE = new Dimension( 535, 190 );
    private static final Color BACKGROUND_COLOR = OQCConstants.COMMON_GRAY;
    
    // Title parameters
    private static final Font TITLE_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLACK;
    
    // Chart parameters
    private static final double L = OQCConstants.L; // do not change!
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = OQCConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MIN = OQCConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MAX = 12.0;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 440, 135 );
    
    // Wave parameters
    private static final Stroke USER_SUM_STROKE = new BasicStroke( 1f );
    private static final Color USER_SUM_COLOR = Color.BLACK;
    private static final Stroke RANDOM_SUM_STROKE = new BasicStroke( 3f );
    public static final Color RANDOM_SUM_COLOR = OQCConstants.OUTPUT_PULSE_COLOR;
    private static final double PIXELS_PER_POINT = 1;
    
    // Autoscaling
    private static final double AUTOSCALE_FACTOR = 1.5; // multiple max amplitude by this amount when autoscaling
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _userFourierSeries;
    private FourierSeries _outputFourierSeries;
    private PhetShapeGraphic _backgroundGraphic;
    private HTMLGraphic _titleGraphic;
    private PulseChart _chartGraphic;
    private FourierSumPlot _userSumPlot;
    private FourierSumPlot _outputSumPlot;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param userFourierSeries the Fourier series constructed by the user
     * @param outputFourierSeries the Fourier series that describes the output pulse
     */
    public OutputPulseView( Component component, FourierSeries userFourierSeries, FourierSeries outputFourierSeries ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Model
        _userFourierSeries = userFourierSeries;
        _userFourierSeries.addObserver( this );
        _outputFourierSeries = outputFourierSeries;
        _outputFourierSeries.addObserver( this );
        
        // Background
        _backgroundGraphic = new PhetShapeGraphic( component );
        _backgroundGraphic.setShape( new RoundRectangle2D.Double( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height, 20, 20 ) );
        _backgroundGraphic.setPaint( BACKGROUND_COLOR );
        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        _backgroundGraphic.setLocation( 0, 0 );
        
        // Light spigot
        LightSpigot lightSpigot = new LightSpigot( component );
        lightSpigot.scale( 1, -1 ); // reflect about the x-axis
        lightSpigot.setLocation( 1, BACKGROUND_SIZE.height - 20 );
        addGraphic( lightSpigot, SPIGOT_LAYER );
        
        // Title
        String title = OQCResources.OUTPUT_PULSE;
        _titleGraphic = new HTMLGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.setRegistrationPoint( _titleGraphic.getWidth()/2, 0 );
        _titleGraphic.setLocation( BACKGROUND_SIZE.width / 2, 5 );
        addGraphic( _titleGraphic, TITLE_LAYER );
        
        // Chart
        {
            _chartGraphic = new PulseChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 35, 35 );
            _chartGraphic.setXAxisTitle( OQCResources.PULSE_X_AXIS_LABEL ); 
         
            // Output Pulse sum plot
            _outputSumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _outputFourierSeries );
            _outputSumPlot.setUseCosines( true );
            _outputSumPlot.setPeriod( L );
            _outputSumPlot.setYScale( OQCConstants.FOURIER_SUM_SCALE );
            _outputSumPlot.setPixelsPerPoint( PIXELS_PER_POINT );
            _outputSumPlot.setStroke( RANDOM_SUM_STROKE );
            _outputSumPlot.setBorderColor( RANDOM_SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _outputSumPlot );
            _chartGraphic.autoscaleY( _outputSumPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
            
            // User's sum plot
            _userSumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _userFourierSeries );
            _userSumPlot.setUseCosines( true );
            _userSumPlot.setPeriod( L );
            _userSumPlot.setYScale( OQCConstants.FOURIER_SUM_SCALE );
            _userSumPlot.setPixelsPerPoint( PIXELS_PER_POINT );
            _userSumPlot.setStroke( USER_SUM_STROKE );
            _userSumPlot.setBorderColor( USER_SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _userSumPlot );
        }
        
        // Interactivity
        setIgnoreMouse( true );
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _userFourierSeries.removeObserver( this );
        _userFourierSeries = null;
        _outputFourierSeries.removeObserver( this );
        _outputFourierSeries = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Changes the visibility of the output pulse waveform.
     * 
     * @param visible true or false
     */
    public void setOutputPulseVisible( boolean visible ) {
        _outputSumPlot.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( isVisible() ) {
            /* Update the plots to match their models.
             * Note: It would be more efficient to update only the data set 
             * that has changed, but we don't have that information.  So we 
             * update both data sets.  No one has complained about the 
             * performance of this. 
             */
            _userSumPlot.updateDataSet();
            _outputSumPlot.updateDataSet();
            
            // Auto scale the chart based on the output pulse plot.
            _chartGraphic.autoscaleY( _outputSumPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
        }
    }
}
