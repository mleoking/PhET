// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fourier.view.discrete;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.charts.StringLabelTable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.fourier.FourierConstants;

/**
 * DiscreteHarmonicsChart is the "Harmonics" chart in the Discrete module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiscreteHarmonicsChart extends Chart {

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

    // Gridline parameters
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );

    // X Axis parameters
    private static final double L = FourierConstants.L;
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );

    // Y Axis parameters
    private static final double Y_MAJOR_TICK_SPACING = 0.5;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetTextGraphic _xAxisTitleGraphic;
    private StringLabelTable _spaceLabels1, _spaceLabels2;
    private StringLabelTable _timeLabels1, _timeLabels2;

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
    public DiscreteHarmonicsChart( Component component, Range2D range, Dimension chartSize ) {
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
            getHorizontalTicks().setMajorLabels( _spaceLabels1 );
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

    //----------------------------------------------------------------------------
    // Chart Labels
    //----------------------------------------------------------------------------

    /*
    * Lazy initialization of the X axis "space" labels.
    */
    public StringLabelTable getSpaceLabels1() {
        if ( _spaceLabels1 == null ) {
            _spaceLabels1 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _spaceLabels1.put( -1.00 * L, "-L" );
            _spaceLabels1.put( -0.75 * L, "-3L/4" );
            _spaceLabels1.put( -0.50 * L, "-L/2" );
            _spaceLabels1.put( -0.25 * L, "-L/4" );
            _spaceLabels1.put( 0 * L, "0" );
            _spaceLabels1.put( +0.25 * L, "L/4" );
            _spaceLabels1.put( +0.50 * L, "L/2" );
            _spaceLabels1.put( +0.75 * L, "3L/4" );
            _spaceLabels1.put( +1.00 * L, "L" );
        }
        return _spaceLabels1;
    }

    /*
    * Lazy initialization of the X axis "space" labels.
    */
    public StringLabelTable getSpaceLabels2() {
        if ( _spaceLabels2 == null ) {
            _spaceLabels2 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _spaceLabels2.put( -2.0 * L, "-2L" );
            _spaceLabels2.put( -1.5 * L, "-3L/2" );
            _spaceLabels2.put( -1.0 * L, "-L" );
            _spaceLabels2.put( -0.5 * L, "-L/2" );
            _spaceLabels2.put( 0 * L, "0" );
            _spaceLabels2.put( +0.5 * L, "L/2" );
            _spaceLabels2.put( +1.0 * L, "L" );
            _spaceLabels2.put( +1.5 * L, "3L/2" );
            _spaceLabels2.put( +2.0 * L, "2L" );
        }
        return _spaceLabels2;
    }

    /*
    * Lazy initialization of the X axis "time" labels.
    */
    public StringLabelTable getTimeLabels1() {
        if ( _timeLabels1 == null ) {
            double T = L; // use the same quantity for wavelength and period
            _timeLabels1 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _timeLabels1.put( -1.00 * T, "-T" );
            _timeLabels1.put( -0.75 * T, "-3T/4" );
            _timeLabels1.put( -0.50 * T, "-T/2" );
            _timeLabels1.put( -0.25 * T, "-T/4" );
            _timeLabels1.put( 0 * T, "0" );
            _timeLabels1.put( +0.25 * T, "T/4" );
            _timeLabels1.put( +0.50 * T, "T/2" );
            _timeLabels1.put( +0.75 * T, "3T/4" );
            _timeLabels1.put( +1.00 * T, "T" );
        }
        return _timeLabels1;
    }

    /*
    * Lazy initialization of the X axis "time" labels.
    */
    public StringLabelTable getTimeLabels2() {
        if ( _timeLabels2 == null ) {
            double T = L; // use the same quantity for wavelength and period
            _timeLabels2 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _timeLabels2.put( -2.0 * T, "-2T" );
            _timeLabels2.put( -1.5 * T, "-3T/2" );
            _timeLabels2.put( -1.0 * T, "-T" );
            _timeLabels2.put( -0.5 * T, "-T/2" );
            _timeLabels2.put( 0 * T, "0" );
            _timeLabels2.put( +0.5 * T, "T/2" );
            _timeLabels2.put( +1.0 * T, "T" );
            _timeLabels2.put( +1.5 * T, "3T/2" );
            _timeLabels2.put( +2.0 * T, "2T" );
        }
        return _timeLabels2;
    }

}
