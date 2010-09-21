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
 * InputPulseView displays the input pulse.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class InputPulseView extends GraphicLayerSet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double SPIGOT_LAYER = 2;
    private static final double TITLE_LAYER = 3;
    private static final double CHART_LAYER = 4;

    // Background parameters
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
    private static final double Y_RANGE_START = OQCConstants.MAX_HARMONIC_AMPLITUDE + ( 0.05 * OQCConstants.MAX_HARMONIC_AMPLITUDE );
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 440, 135 );

    // Wave parameters
    private static final Stroke WAVE_STROKE = new BasicStroke( 1f );
    private static final Color WAVE_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component the parent Component
     * @param fourierSeries the Fourier series that this view displays
     */
    public InputPulseView( Component component, FourierSeries fourierSeries ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Background
        PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component );
        backgroundGraphic.setShape( new RoundRectangle2D.Double( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height, 20, 20 ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        backgroundGraphic.setLocation( 0, 0 );
        
        // Light spigot
        LightSpigot lightSpigot = new LightSpigot( component );
        lightSpigot.setLocation( 1, 20 );
        addGraphic( lightSpigot, SPIGOT_LAYER );

        // Title
        String title = OQCResources.INPUT_PULSE;
        HTMLGraphic titleGraphic = new HTMLGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.setRegistrationPoint( titleGraphic.getWidth()/2, 0 );
        titleGraphic.setLocation( BACKGROUND_SIZE.width / 2, 5 );
        addGraphic( titleGraphic, TITLE_LAYER );

        // Chart
        PulseChart chartGraphic = new PulseChart( component, CHART_RANGE, CHART_SIZE );
        addGraphic( chartGraphic, CHART_LAYER );
        chartGraphic.setRegistrationPoint( 0, 0 );
        chartGraphic.setLocation( 35, 35 );
        chartGraphic.setXAxisTitle( OQCResources.PULSE_X_AXIS_LABEL );
        
        // Input pulse
        FourierSumPlot inputPlot = new FourierSumPlot( component, chartGraphic, fourierSeries );
        inputPlot.setPixelsPerPoint( 1 );
        inputPlot.setStroke( WAVE_STROKE );
        inputPlot.setStrokeColor( WAVE_COLOR );
        inputPlot.setUseCosines( true );
        inputPlot.setYScale( OQCConstants.FOURIER_SUM_SCALE );
        chartGraphic.addDataSetGraphic( inputPlot );

        // Interactivity
        setIgnoreMouse( true );
    }
}
