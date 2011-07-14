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
 * Base class for charts that display a waveform.
 * <p/>
 * The chart has an internal width for the waveform, and all values (actual or symbolic)
 * are displayed using label tables that map the internal values to labels that
 * match the wavelength or period of the fundamental frequency.  See #2981.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class WaveformChart extends Chart {

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

    // X axis parameters
    private static final double L = FourierConstants.L;
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );

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
    public WaveformChart( Component component, Range2D range, Dimension chartSize, double yMajorTickSpacing, double yMinorTickSpacing ) {
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
            getHorizontalTicks().setMajorLabels( getActualSpaceLabels1() );
            getHorizontalTicks().setMajorNumberFormat( new DecimalFormat( "#.###" ) );

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
            getVerticalTicks().setMajorTickSpacing( yMajorTickSpacing );
            getVerticalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
            getVerticalTicks().setMajorTickFont( MAJOR_TICK_FONT );

            // Horizontal gridlines for major ticks
            getHorizonalGridlines().setMajorGridlinesVisible( true );
            getHorizonalGridlines().setMajorTickSpacing( yMajorTickSpacing );
            getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
            getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

            // Horizontal gridlines for minor ticks
            getHorizonalGridlines().setMinorGridlinesVisible( true );
            getHorizonalGridlines().setMinorTickSpacing( yMinorTickSpacing );
            getHorizonalGridlines().setMinorGridlinesColor( Color.BLACK );
            getHorizonalGridlines().setMinorGridlinesStroke( new BasicStroke( 0.25f ) );
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

    //----------------------------------------------------------------------------
    // Chart Labels
    //----------------------------------------------------------------------------

    // Actual x-axis labels for the space domain, these correspond to the actual wavelength of the fundamental.
    public StringLabelTable getActualSpaceLabels1() {
        DecimalFormat format = new DecimalFormat( "#.###" );
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        double[] multipliers = { -1.00, -0.75, -0.50, -0.25, 0, 0.25, 0.50, 0.75, 1.00 };
        for ( int i = 0; i < multipliers.length; i++ ) {
            table.put( multipliers[i] * L, format.format( multipliers[i] * FourierConstants.FUNDAMENTAL_WAVELENGTH ) );
        }
        return table;
    }

    // Symbolic x-axis labels for the space domain.
    public StringLabelTable getSymbolicSpaceLabels1() {
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        table.put( -1.00 * L, "-L" );
        table.put( -0.75 * L, "-3L/4" );
        table.put( -0.50 * L, "-L/2" );
        table.put( -0.25 * L, "-L/4" );
        table.put( 0 * L, "0" );
        table.put( +0.25 * L, "L/4" );
        table.put( +0.50 * L, "L/2" );
        table.put( +0.75 * L, "3L/4" );
        table.put( +1.00 * L, "L" );
        return table;
    }

    // Actual x-axis labels for the space domain, these correspond to the actual wavelength of the fundamental.
    public StringLabelTable getActualSpaceLabels2() {
        DecimalFormat format = new DecimalFormat( "#.###" );
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        double[] multipliers = { -2.0, -1.5, -1.0, -0.5, 0, 0.5, 1.0, 1.5, 2.0 };
        for ( int i = 0; i < multipliers.length; i++ ) {
            table.put( multipliers[i] * L, format.format( multipliers[i] * FourierConstants.FUNDAMENTAL_WAVELENGTH ) );
        }
        return table;
    }

    // Symbolic x-axis labels for the space domain, for a different zoom level.
    public StringLabelTable getSymbolicSpaceLabels2() {
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        table.put( -2.0 * L, "-2L" );
        table.put( -1.5 * L, "-3L/2" );
        table.put( -1.0 * L, "-L" );
        table.put( -0.5 * L, "-L/2" );
        table.put( 0 * L, "0" );
        table.put( +0.5 * L, "L/2" );
        table.put( +1.0 * L, "L" );
        table.put( +1.5 * L, "3L/2" );
        table.put( +2.0 * L, "2L" );
        return table;
    }

    // Actual x-axis labels for the time domain, these correspond to the actual period of the fundamental.
    public StringLabelTable getActualTimeLabels1() {
        DecimalFormat format = new DecimalFormat( "#.###" );
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        double[] multipliers = { -1.00, -0.75, -0.50, -0.25, 0, 0.25, 0.50, 0.75, 1.00 };
        for ( int i = 0; i < multipliers.length; i++ ) {
            table.put( multipliers[i] * L, format.format( multipliers[i] * FourierConstants.FUNDAMENTAL_PERIOD ) );
        }
        return table;
    }

    // Symbolic x-axis labels for the space domain, for specific zoom levels.
    public StringLabelTable getSymbolicTimeLabels1() {
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        table.put( -1.00 * L, "-T" );
        table.put( -0.75 * L, "-3T/4" );
        table.put( -0.50 * L, "-T/2" );
        table.put( -0.25 * L, "-T/4" );
        table.put( 0 * L, "0" );
        table.put( +0.25 * L, "T/4" );
        table.put( +0.50 * L, "T/2" );
        table.put( +0.75 * L, "3T/4" );
        table.put( +1.00 * L, "T" );
        return table;
    }

    // Actual x-axis labels for the time domain, these correspond to the actual period of the fundamental.
    public StringLabelTable getActualTimeLabels2() {
        DecimalFormat format = new DecimalFormat( "#.###" );
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        double[] multipliers = { -2.0, -1.5, -1.0, -0.5, 0, 0.5, 1.0, 1.5, 2.0 };
        for ( int i = 0; i < multipliers.length; i++ ) {
            table.put( multipliers[i] * L, format.format( multipliers[i] * FourierConstants.FUNDAMENTAL_PERIOD ) );
        }
        return table;
    }

    // Symbolic x-axis labels for the space domain, for specific zoom levels.
    public StringLabelTable getSymbolicTimeLabels2() {
        StringLabelTable table = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
        table.put( -2.0 * L, "-2T" );
        table.put( -1.5 * L, "-3T/2" );
        table.put( -1.0 * L, "-T" );
        table.put( -0.5 * L, "-T/2" );
        table.put( 0 * L, "0" );
        table.put( +0.5 * L, "T/2" );
        table.put( +1.0 * L, "T" );
        table.put( +1.5 * L, "3T/2" );
        table.put( +2.0 * L, "2T" );
        return table;
    }
}
