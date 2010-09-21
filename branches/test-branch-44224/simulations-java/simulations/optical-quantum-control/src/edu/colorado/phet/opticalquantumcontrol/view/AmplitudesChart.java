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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;


/**
 * AmplitudesChart is the chart behind the amplitude sliders.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudesChart extends Chart {

    // Axis parameters
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Font AXIS_TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;
    
    // X axis
    private static final String X_AXIS_LABEL = OQCResources.AMPLITUDE_X_AXIS_LABEL;
    
    // Y axis
    private static final double Y_MAJOR_TICK_SPACING = 0.5;
    private static final double Y_MINOR_TICK_SPACING = 0.1;
    private static final Stroke Y_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font Y_MAJOR_TICK_FONT = new PhetFont( Font.BOLD, 12 );
    
    // Gridlines
    private static final boolean MAJOR_GRIDLINES_ENABLED = true;
    private static final boolean MINOR_GRIDLINES_ENABLED = false;
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    
    // Range labels
    private static final boolean RANGE_LABELS_VISIBLE = false;
    private static final NumberFormat RANGE_LABELS_FORMAT = new DecimalFormat( "0.00" );
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param range
     * @param chartSize
     */
    public AmplitudesChart( Component component, Range2D range, Dimension chartSize ) {
        super( component, range, chartSize );
        
        // X axis
        {
            getXAxis().setStroke( AXIS_STROKE );
            getXAxis().setColor( AXIS_COLOR );

            HTMLGraphic xAxisTitleGraphic = new HTMLGraphic( component, AXIS_TITLE_FONT, X_AXIS_LABEL, AXIS_TITLE_COLOR );
            xAxisTitleGraphic.setRegistrationPoint( -2, xAxisTitleGraphic.getHeight() / 2 ); // left center
            setXAxisTitle( xAxisTitleGraphic );
            
            // No ticks, labels or gridlines
            getHorizontalTicks().setVisible( false );
            getXAxis().setMajorTicksVisible( false );
            getXAxis().setMinorTicksVisible( false );
            getXAxis().setMajorTickLabelsVisible( false );
            getXAxis().setMinorTickLabelsVisible( false );
            getVerticalGridlines().setMinorGridlinesVisible( false );
            getVerticalGridlines().setMajorGridlinesVisible( false );
        }
        
        // Y axis
        {
            getYAxis().setStroke( AXIS_STROKE );
            getYAxis().setColor( AXIS_COLOR );
            
            // No ticks or labels on the axis
            getYAxis().setMajorTicksVisible( false );
            getYAxis().setMajorTickLabelsVisible( false );
            getYAxis().setMinorTicksVisible( false );
            getYAxis().setMinorTickLabelsVisible( false );

            // Range labels
            getVerticalTicks().setRangeLabelsVisible( RANGE_LABELS_VISIBLE );
            getVerticalTicks().setRangeLabelsNumberFormat( RANGE_LABELS_FORMAT );
            
            // Major ticks and labels to the left of the chart
            getVerticalTicks().setMajorTicksVisible( true );
            getVerticalTicks().setMajorTickLabelsVisible( true );
            getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            getVerticalTicks().setMajorTickStroke( Y_MAJOR_TICK_STROKE );
            getVerticalTicks().setMajorTickFont( Y_MAJOR_TICK_FONT );

            // Major gridlines
            getHorizonalGridlines().setMajorGridlinesVisible( MAJOR_GRIDLINES_ENABLED );
            getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
            getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

            // Minor gridlines
            getHorizonalGridlines().setMinorGridlinesVisible( MINOR_GRIDLINES_ENABLED );
            getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
            getHorizonalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
            getHorizonalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
        }
    }
}
