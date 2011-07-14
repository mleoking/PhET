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
import edu.colorado.phet.common.charts.StringLabelTable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.fourier.MathStrings;


/**
 * D2CAmplitudesChart is the "Amplitudes" chart in the "Discrete to Continuous" (D2C) module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CAmplitudesChart extends Chart {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Axis parameters
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Font AXIS_TITLE_FONT = new PhetFont( Font.BOLD, 12 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;

    // X axis 
    private static final double X_MAJOR_TICK_SPACING = 2 * Math.PI;
    private static final double X_MINOR_TICK_SPACING = Math.PI / 2;
    private static final Stroke X_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke X_MINOR_TICK_STROKE = new BasicStroke( 0.5f );
    private static final Font X_MAJOR_TICK_FONT = new PhetFont( Font.BOLD, 12 );

    // Y axis
    private static final double Y_MAJOR_TICK_SPACING = 0.2;
    private static final double Y_MINOR_TICK_SPACING = 0.1;
    private static final Stroke Y_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_TICK_STROKE = new BasicStroke( 0.5f );
    private static final Font Y_MAJOR_TICK_FONT = new PhetFont( Font.BOLD, 12 );

    // Gridlines
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.1f );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private HTMLGraphic _xAxisTitleGraphic;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public D2CAmplitudesChart( Component component, Range2D range, Dimension chartSize ) {
        super( component, range, chartSize );

        // X axis
        {
            getXAxis().setStroke( AXIS_STROKE );
            getXAxis().setColor( AXIS_COLOR );

            // Axis title
            _xAxisTitleGraphic = new HTMLGraphic( component, AXIS_TITLE_FONT, "", AXIS_TITLE_COLOR );
            setXAxisTitle( _xAxisTitleGraphic );

            // Major ticks with labels
            {
                getXAxis().setMajorTicksVisible( false );
                getHorizontalTicks().setMajorTicksVisible( true );
                getHorizontalTicks().setMajorTickLabelsVisible( true );
                getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                getHorizontalTicks().setMajorTickStroke( X_MAJOR_TICK_STROKE );
                getHorizontalTicks().setMajorTickFont( X_MAJOR_TICK_FONT );
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
                xAxisLabels.put( 24 * Math.PI, "24" + MathStrings.C_PI );
                getHorizontalTicks().setMajorLabels( xAxisLabels );
            }

            // Minor ticks with no labels
            getXAxis().setMinorTicksVisible( false );
            getHorizontalTicks().setMinorTicksVisible( true );
            getHorizontalTicks().setMinorTickLabelsVisible( false );
            getHorizontalTicks().setMinorTickSpacing( X_MINOR_TICK_SPACING );
            getHorizontalTicks().setMinorTickStroke( X_MINOR_TICK_STROKE );

            // No major gridlines
            getVerticalGridlines().setMajorGridlinesVisible( false );

            // No minor gridlines
            getVerticalGridlines().setMinorGridlinesVisible( false );
        }

        // Y axis
        {
            getYAxis().setStroke( AXIS_STROKE );
            getYAxis().setColor( AXIS_COLOR );

            // Major ticks with labels
            getYAxis().setMajorTicksVisible( false );
            getVerticalTicks().setMajorTicksVisible( true );
            getVerticalTicks().setMajorTickLabelsVisible( true );
            getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            getVerticalTicks().setMajorTickStroke( Y_MAJOR_TICK_STROKE );
            getVerticalTicks().setMajorTickFont( Y_MAJOR_TICK_FONT );

            // Minor ticks with no labels
            getYAxis().setMinorTicksVisible( false );
            getVerticalTicks().setMinorTicksVisible( true );
            getVerticalTicks().setMinorTickLabelsVisible( false );
            getVerticalTicks().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
            getVerticalTicks().setMinorTickStroke( Y_MINOR_TICK_STROKE );

            // Major gridlines
            getHorizonalGridlines().setMajorGridlinesVisible( true );
            getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
            getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
            getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

            // No minor gridlines
            getHorizonalGridlines().setMinorGridlinesVisible( false );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the X axis title.
     *
     * @param title
     */
    public void setXAxisTitle( String title ) {
        _xAxisTitleGraphic.setHTML( title );
        _xAxisTitleGraphic.setRegistrationPoint( -2, _xAxisTitleGraphic.getHeight() ); // lower left corner
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
            setRange( range );
        }
        else {
            range.setMaxY( maxY );
            setRange( range );

            getVerticalTicks().setMajorNumberFormat( majorNumberFormat );
            getVerticalTicks().setMajorTickSpacing( majorSpacing );
            getVerticalTicks().setMinorTickSpacing( minorSpacing );
            getHorizonalGridlines().setMajorTickSpacing( majorSpacing );
        }
    }
}
