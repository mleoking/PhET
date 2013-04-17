// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.d2c;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;


/**
 * D2CHarmonicsChart is the "Components" chart in the "Discrete to Continuous" (D2C) module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CComponentsChart extends Chart {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Axis parameter
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 0.5f );
    private static final Font AXIS_TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;

    // Range labels
    private static final boolean RANGE_LABELS_VISIBLE = false;
    private static final NumberFormat RANGE_LABELS_FORMAT = new DecimalFormat( "0.00" );

    // Tick Mark parameter
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font MAJOR_TICK_FONT = new PhetFont( Font.BOLD, 12 );
    private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
    private static final Font MINOR_TICK_FONT = MAJOR_TICK_FONT;

    // X Axis parameters
    private static final double L = 1;
    private static final double X_MAJOR_TICK_SPACING = 0.5;
    private static final double X_MINOR_TICK_SPACING = 0.1;

    // Y Axis parameters
    private static final double Y_MAJOR_TICK_SPACING = 0.5;

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
    public D2CComponentsChart( Component component, Range2D range, Dimension chartSize ) {
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

            // Minor ticks, no labels below the chart
            getHorizontalTicks().setMinorTicksVisible( true );
            getHorizontalTicks().setMinorTickLabelsVisible( false );
            getHorizontalTicks().setMinorTickSpacing( X_MINOR_TICK_SPACING );
            getHorizontalTicks().setMinorTickStroke( MINOR_TICK_STROKE );
            getHorizontalTicks().setMinorTickFont( MINOR_TICK_FONT );

            // No vertical grid lines
            getVerticalGridlines().setMajorGridlinesVisible( false );
            getVerticalGridlines().setMinorGridlinesVisible( false );
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

            // No minor ticks.
            getVerticalTicks().setMinorTicksVisible( false );

            // No horizontal gridlines
            getHorizonalGridlines().setMajorGridlinesVisible( false );
            getHorizonalGridlines().setMinorGridlinesVisible( false );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the title on the x axis.
     *
     * @param title
     */
    public void setXAxisTitle( String title ) {
        _xAxisTitleGraphic.setText( title );
        _xAxisTitleGraphic.setRegistrationPoint( -4, -_xAxisTitleGraphic.getHeight() / 2 ); // left center
    }

    public void setXAxisTitle( char c ) {
        setXAxisTitle( String.valueOf( c ) );
    }

    /**
     * Rescales the Y-axis range, tick marks and gridlines.
     *
     * @param maxY
     */
    public void autoscaleY( double maxY ) {

        double majorSpacing = 0;
        double minorSpacing = 0;
        NumberFormat majorNumberFormat;

        /*
        * Set the tick marks and gridlines based on the max amplitude.
        * These values were set via trial-&-error.
        * Good luck changing them.
        */
        if ( maxY > 1 ) {
            majorSpacing = 1.0;
            minorSpacing = 0.5;
            majorNumberFormat = new DecimalFormat( "#.#" );
        }
        else if ( maxY > 0.5 ) {
            majorSpacing = 0.2;
            minorSpacing = 0.1;
            majorNumberFormat = new DecimalFormat( ".##" );
        }
        else if ( maxY > 0.2 ) {
            majorSpacing = 0.1;
            minorSpacing = 0.05;
            majorNumberFormat = new DecimalFormat( ".##" );
        }
        else if ( maxY > 0.05 ) {
            majorSpacing = 0.05;
            minorSpacing = 0.01;
            majorNumberFormat = new DecimalFormat( ".##" );
        }
        else if ( maxY > 0.02 ) {
            majorSpacing = 0.01;
            minorSpacing = 0.005;
            majorNumberFormat = new DecimalFormat( ".###" );
        }
        else {
            majorSpacing = 0.005;
            minorSpacing = 0.001;
            majorNumberFormat = new DecimalFormat( ".###" );
        }

        /*
        * The order in which we change the range, tick marks and gridlines is
        * important.  If we're not careful, we may end up generating a huge
        * number of ticks gridlines based on old/new settings.
        */
        Range2D range = getRange();
        if ( maxY > range.getMaxY() ) {

            getVerticalTicks().setMajorNumberFormat( majorNumberFormat );
            getVerticalTicks().setMajorTickSpacing( majorSpacing );
            getVerticalTicks().setMinorTickSpacing( minorSpacing );
            getHorizonalGridlines().setMajorTickSpacing( majorSpacing );

            range.setMaxY( maxY );
            range.setMinY( -maxY );
            setRange( range );
        }
        else {
            range.setMaxY( maxY );
            range.setMinY( -maxY );
            setRange( range );

            getVerticalTicks().setMajorNumberFormat( majorNumberFormat );
            getVerticalTicks().setMajorTickSpacing( majorSpacing );
            getVerticalTicks().setMinorTickSpacing( minorSpacing );
            getHorizonalGridlines().setMajorTickSpacing( majorSpacing );
        }
    }
}
