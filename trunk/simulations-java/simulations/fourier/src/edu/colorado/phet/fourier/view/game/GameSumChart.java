// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fourier.view.game;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.fourier.FourierConstants;

/**
 * GameSumChart is the Sum chart in the Game module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSumChart extends Chart {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Axis parameter
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    private static final Font AXIS_TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;

    // Range labels
    private static final boolean RANGE_LABELS_VISIBLE = false;
    private static final NumberFormat RANGE_LABELS_FORMAT = new DecimalFormat( "0.00" );

    // Tick Mark parameter
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font MAJOR_TICK_FONT = new PhetFont( Font.BOLD, 12 );
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
    private static final Font MINOR_TICK_FONT = MAJOR_TICK_FONT;
    private static final Color MINOR_TICK_COLOR = MAJOR_TICK_COLOR;

    // Gridline parameters
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );

    // X axis
    private static final double L = FourierConstants.L;
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );

    // Y axis
    private static final double Y_MAJOR_TICK_SPACING = 5.0;
    private static final double Y_MINOR_TICK_SPACING = 1.0;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetTextGraphic _xAxisTitleGraphic;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component
     * @param range
     * @param chartSize
     */
    public GameSumChart( Component component, Range2D range, Dimension chartSize ) {
        super( component, range, chartSize );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // X axis
        {
            getXAxis().setStroke( AXIS_STROKE );
            getXAxis().setColor( AXIS_COLOR );

            // Title
            _xAxisTitleGraphic = new PhetTextGraphic( component, AXIS_TITLE_FONT, "", AXIS_TITLE_COLOR );
            setXAxisTitle( _xAxisTitleGraphic );

            // No ticks or labels on the axis
            getXAxis().setMajorTicksVisible( false );
            getXAxis().setMajorTickLabelsVisible( false );
            getXAxis().setMinorTicksVisible( false );
            getXAxis().setMinorTickLabelsVisible( false );

            // Major ticks with labels below the chart
            getHorizontalTicks().setMajorTicksVisible( true );
            getHorizontalTicks().setMajorTickLabelsVisible( true );
            getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
            getHorizontalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
            getHorizontalTicks().setMajorTickFont( MAJOR_TICK_FONT );
            getHorizontalTicks().setMajorNumberFormat( new DecimalFormat( "#.##" ) );

            // Vertical gridlines for major ticks.
            getVerticalGridlines().setMajorGridlinesVisible( true );
            getVerticalGridlines().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
            getVerticalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
            getVerticalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

            // Vertical gridlines for minor ticks.
            getVerticalGridlines().setMinorGridlinesVisible( true );
            getVerticalGridlines().setMinorTickSpacing( X_MINOR_TICK_SPACING );
            getVerticalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
            getVerticalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
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

            // Major ticks with labels to the left of the chart
            getVerticalTicks().setMajorTicksVisible( true );
            getVerticalTicks().setMajorTickLabelsVisible( true );
            getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            getVerticalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
            getVerticalTicks().setMajorTickFont( MAJOR_TICK_FONT );

            // Horizontal gridlines for major ticks
            getHorizonalGridlines().setMajorGridlinesVisible( true );
            getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
            getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

            // Horizontal gridlines for minor ticks
            getHorizonalGridlines().setMinorGridlinesVisible( true );
            getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
            getHorizonalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
            getHorizonalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
        }

        autoscaleY( range.getMaxY() );
    }

    /**
     * Sets the title on the x axis.
     *
     * @param title
     */
    public void setXAxisTitle( String title ) {
        _xAxisTitleGraphic.setText( title );
        _xAxisTitleGraphic.setRegistrationPoint( -4, -_xAxisTitleGraphic.getHeight() / 2 ); // left center
    }

    /**
     * Rescales the Y-axis range, tick marks and gridlines.
     *
     * @param maxY
     */
    public void autoscaleY( double maxY ) {

        // Range
        if ( maxY < 4 / Math.PI ) {
            maxY = 4 / Math.PI;
        }
        Range2D range = getRange();
        range.setMaxY( maxY );
        range.setMinY( -maxY );
        setRange( range );

        // Y axis ticks and gridlines
        {
            double tickSpacing;
            if ( range.getMaxY() < 2 ) {
                tickSpacing = 0.5;
            }
            else if ( range.getMaxY() < 5 ) {
                tickSpacing = 1.0;
            }
            else {
                tickSpacing = 5.0;
            }
            getVerticalTicks().setMajorTickSpacing( tickSpacing );
            getHorizonalGridlines().setMajorTickSpacing( tickSpacing );
        }
    }
}
