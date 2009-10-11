package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.XYPlotNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Visual display of the bunny population statistics. Shows a separate line for the total population, and each trait
 * of all of the genes.
 * <p/>
 * Caches and simplifies datapoints as they are seen, and has two separate plots (one for drawing the data, and one for
 * drawing the axes and everything else). Both of these are critical for performance.
 *
 * @author Jonathan Olson
 */
public class BunnyStatsCanvas extends PhetPCanvas {

    // visual settings
    private static final boolean CHART_ANTIALIAS = false;
    private static final boolean PLOT_ANTIALIAS = false;

    private JFreeChartNode chartNode;
    private XYPlotNode plotNode;
    private XYSeriesCollection mainDataset;
    private XYPlot emptyPlot;
    private XYPlot mainPlot;

    /**
     * What slice of time should be displayed on the stats panel.
     */
    private static final int RANGE = 300;

    // Indices for each different plotted value
    private static final int TOTAL_INDEX = 0;
    private static final int FUR_WHITE_INDEX = 1;
    private static final int FUR_BROWN_INDEX = 2;
    private static final int TAIL_SHORT_INDEX = 3;
    private static final int TAIL_LONG_INDEX = 4;
    private static final int TEETH_SHORT_INDEX = 5;
    private static final int TEETH_LONG_INDEX = 6;

    private static final int NUM_SERIES = 7;

    private PSwing zoomHolder;

    // zoom properties
    private final int[] zoomBounds = new int[]{5, 15, 30, 50, 75, 100, 150, 200, 250, 350, 500, 1000, 2000, 3000, 5000};
    private static final int DEFAULT_ZOOM_INDEX = 3;
    private int zoomIndex = DEFAULT_ZOOM_INDEX;

    // cached value of the total bunny population
    private int cachedPopulation = 0;

    private static final Dimension DEFAULT_STATS_SIZE = new Dimension( 300, 200 );

    // used so that we don't update EVERY time when the population doesn't change
    private int cycleCounter = 0;
    private static final int CYCLE_LENGTH = 3;

    public BunnyStatsCanvas() {
        super( DEFAULT_STATS_SIZE );

        PNode root = new PNode();
        addScreenChild( root );

        mainDataset = createDataset();
        emptyPlot = createPlot( createDataset() );
        mainPlot = createPlot( mainDataset );

        JFreeChart chart = new JFreeChart( emptyPlot );
        chart.setBackgroundPaint( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        if ( NaturalSelectionApplication.isHighContrast() ) {
            chart.setBackgroundPaint( Color.BLACK );
            chart.setBorderPaint( Color.WHITE );
            LegendTitle legend = chart.getLegend();
            legend.setBackgroundPaint( Color.BLACK );
            legend.setItemPaint( Color.WHITE );
            legend.setFrame( new LineBorder( Color.WHITE, new BasicStroke( 1.0f ), new RectangleInsets( 5, 5, 5, 5 ) ) );
        }

        chart.setAntiAlias( CHART_ANTIALIAS );

        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );

        chartNode = new JFreeChartNode( chart );
        root.addChild( chartNode );

        plotNode = new XYPlotNode( mainPlot );
        if ( !PLOT_ANTIALIAS ) {
            RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
            plotNode.setRenderingHints( hints );
        }
        root.addChild( plotNode );

        JPanel zoomPanel = new JPanel( new FlowLayout() );
        zoomPanel.setOpaque( false );

        // zoom buttons

        JButton zoomInButton = new JButton( new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_ZOOM_IN ) ) );
        JButton zoomOutButton = new JButton( new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_ZOOM_OUT ) ) );
        zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                zoomIndex--;
                updateZoom();
                updateLayout();
            }
        } );

        zoomOutButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                zoomIndex++;
                updateZoom();
                updateLayout();
            }
        } );

        zoomInButton.setOpaque( true );
        zoomOutButton.setOpaque( true );

        zoomInButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        zoomOutButton.setMargin( new Insets( 0, 0, 0, 0 ) );

        zoomPanel.add( zoomInButton );
        zoomPanel.add( zoomOutButton );

        zoomHolder = new PSwing( zoomPanel );
        root.addChild( zoomHolder );

        updateZoom();
        updateLayout();

        setBorder( null );
    }

    /**
     * Called when time changes
     *
     * @param population The current total population
     */
    public void onTick( int population ) {
        if ( cachedPopulation != population ) {
            cachedPopulation = population;

            // population changed, so we should update our plot immediately
            addDataPoint( population );
        }
        else if ( cycleCounter++ % CYCLE_LENGTH == 0 ) {
            // otherwise, only update the population every so often for performance
            addDataPoint( population );
        }
    }

    public void reset() {
        for ( int i = 0; i < NUM_SERIES; i++ ) {
            mainDataset.getSeries( i ).clear();
        }

        pos = 0;
        low = 0;

        cachedPositions = new int[]{2, 2, 2, 2, 2, 2, 2};
        cachedValid = new boolean[]{false, false, false, false, false, false, false};

        zoomIndex = DEFAULT_ZOOM_INDEX;

        // redraw and relayout everything
        updateZoom();
        updateLayout();
    }

    private XYSeriesCollection createDataset() {
        XYSeriesCollection ret = new XYSeriesCollection();

        ret.addSeries( new XYSeries( NaturalSelectionStrings.STATS_TOTAL ) );
        ret.addSeries( new XYSeries( NaturalSelectionStrings.GENE_COLOR_WHITE ) );
        ret.addSeries( new XYSeries( NaturalSelectionStrings.GENE_COLOR_BROWN ) );
        ret.addSeries( new XYSeries( NaturalSelectionStrings.GENE_TAIL_SHORT ) );
        ret.addSeries( new XYSeries( NaturalSelectionStrings.GENE_TAIL_LONG ) );
        ret.addSeries( new XYSeries( NaturalSelectionStrings.GENE_TEETH_SHORT ) );
        ret.addSeries( new XYSeries( NaturalSelectionStrings.GENE_TEETH_LONG ) );

        return ret;
    }

    private XYPlot createPlot( XYSeriesCollection dataset ) {
        XYPlot plot = new XYPlot();

        ValueAxis domainAxis = new NumberAxis( NaturalSelectionStrings.STATS_TIME );
        domainAxis.setTickLabelsVisible( false );
        domainAxis.setRange( 0, RANGE );
        plot.setDomainAxis( domainAxis );

        ValueAxis rangeAxis = new NumberAxis( NaturalSelectionStrings.STATS_POPULATION );
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        plot.setRangeAxis( rangeAxis );
        rangeAxis.setRange( 0, 50 );

        plot.setRenderer( new StandardXYItemRenderer() );

        final int seriesIndex = 0;
        plot.setDataset( seriesIndex, dataset );
        XYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setStroke( new BasicStroke( 3f ) );
        if ( NaturalSelectionApplication.isHighContrast() ) {
            domainAxis.setAxisLinePaint( Color.WHITE );
            domainAxis.setLabelPaint( Color.WHITE );
            domainAxis.setTickLabelPaint( Color.WHITE );
            domainAxis.setTickMarkPaint( Color.WHITE );

            rangeAxis.setAxisLinePaint( Color.WHITE );
            rangeAxis.setLabelPaint( Color.WHITE );
            rangeAxis.setTickLabelPaint( Color.WHITE );
            rangeAxis.setTickMarkPaint( Color.WHITE );

            renderer.setSeriesPaint( TOTAL_INDEX, Color.WHITE );
            renderer.setSeriesPaint( FUR_WHITE_INDEX, Color.RED );
            renderer.setSeriesPaint( FUR_BROWN_INDEX, Color.CYAN );
            renderer.setSeriesPaint( TAIL_SHORT_INDEX, Color.BLUE );
            renderer.setSeriesPaint( TAIL_LONG_INDEX, Color.ORANGE );
            renderer.setSeriesPaint( TEETH_SHORT_INDEX, Color.YELLOW );
            renderer.setSeriesPaint( TEETH_LONG_INDEX, Color.MAGENTA );

            plot.setBackgroundPaint( Color.BLACK );
            plot.setDomainGridlinePaint( Color.WHITE );
            plot.setRangeGridlinePaint( Color.WHITE );
            plot.setDomainZeroBaselinePaint( Color.WHITE );
            plot.setOutlinePaint( Color.WHITE );
            plot.setRangeTickBandPaint( new Color( 32, 32, 32 ) );
            plot.setRangeZeroBaselinePaint( Color.WHITE );
        }
        else {
            // TODO: (low) consolidate colors somewhere for data? maybe they should be changable easily?
            renderer.setSeriesPaint( TOTAL_INDEX, Color.BLACK );
            renderer.setSeriesPaint( FUR_WHITE_INDEX, Color.RED );
            renderer.setSeriesPaint( FUR_BROWN_INDEX, Color.CYAN );
            renderer.setSeriesPaint( TAIL_SHORT_INDEX, Color.BLUE );
            renderer.setSeriesPaint( TAIL_LONG_INDEX, Color.ORANGE );
            renderer.setSeriesPaint( TEETH_SHORT_INDEX, Color.YELLOW.darker() );
            renderer.setSeriesPaint( TEETH_LONG_INDEX, Color.MAGENTA );
        }
        plot.setRenderer( seriesIndex, renderer );

        return plot;
    }

    @Override
    protected void updateLayout() {

        // resize the chart
        final double margin = 10;
        double x = margin;
        double y = margin;
        double w = getWidth() - 2 * margin;
        double h = getHeight() - 2 * margin;
        chartNode.setBounds( 0, 0, w, h );
        chartNode.setOffset( x, y );
        chartNode.updateChartRenderingInfo();

        // Plot bounds
        ChartRenderingInfo chartInfo = chartNode.getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();

        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D localBounds = new Rectangle2D.Double();
        localBounds.setRect( dataAreaRef );
        Rectangle2D plotBounds = chartNode.localToGlobal( localBounds );

        // Plot node
        plotNode.setOffset( 0, 0 );
        plotNode.setDataArea( plotBounds );

        zoomHolder.setOffset( plotBounds.getX(), plotBounds.getY() );
    }

    /**
     * Update both the main plot (data) and empty plot (for axes) with the new zoom limits
     */
    private synchronized void updateZoom() {
        if ( zoomIndex < 0 ) {
            zoomIndex = 0;
        }
        if ( zoomIndex >= zoomBounds.length ) {
            zoomIndex = zoomBounds.length - 1;
        }
        mainPlot.getRangeAxis().setRange( 0, zoomBounds[zoomIndex] );
        emptyPlot.getRangeAxis().setRange( 0, zoomBounds[zoomIndex] );
    }

    //----------------------------------------------------------------------------
    // Caching and adding data points
    //----------------------------------------------------------------------------

    /**
     * The latest time position where points are added
     */
    private int pos = 0;

    /**
     * The lowest time position where points are displayed.
     */
    private int low = 0;

    /**
     * Holds the latest values seen for each plot
     */
    private int[] cachedPositions = new int[]{2, 2, 2, 2, 2, 2, 2};

    /**
     * Stores whether two datapoints have been seen indentically in a row. (If the third datapoint is also the same, we
     * can remove the middle (second) datapoint for efficiency)
     */
    private boolean[] cachedValid = new boolean[]{false, false, false, false, false, false, false};

    /**
     * Sets the data value at a particular index (IE for a particular line).
     * This function uses an intelligent cache, so that the plotter simplifies repeated (constant) values in a row so
     * that only the first and last values are kept.
     *
     * @param index Index (line) to add the datapoint to
     * @param value Value (the population)
     */
    private void setDataAtIndex( int index, int value ) {
        XYSeries series = mainDataset.getSeries( index );

        if ( cachedPositions[index] == value ) {
            // next data point is the same as in our cache

            if ( cachedValid[index] == true ) {
                // cache is valid, so we can delete the "middle" datapoint

                series.setNotify( false );
                series.remove( series.getItemCount() - 1 );
                series.setNotify( true );
            }
            else {
                // cache is not valid. mark it as valid now, since we have seen two datapoints with the same value in a row
                cachedValid[index] = true;
            }
        }
        else {
            // it's different. change the cache and invalidate it
            cachedValid[index] = false;
            cachedPositions[index] = value;
        }

        // actually add the datapoint into the series
        series.add( pos, value, false );
    }

    /**
     * Adds a data point at the current time position
     *
     * @param totalPopulation The current total population
     */
    private synchronized void addDataPoint( int totalPopulation ) {
        // add data to each line
        setDataAtIndex( TOTAL_INDEX, totalPopulation );
        setDataAtIndex( FUR_WHITE_INDEX, ColorGene.getInstance().getPrimaryPhenotypeCount() );
        setDataAtIndex( FUR_BROWN_INDEX, ColorGene.getInstance().getSecondaryPhenotypeCount() );
        setDataAtIndex( TAIL_SHORT_INDEX, TailGene.getInstance().getPrimaryPhenotypeCount() );
        setDataAtIndex( TAIL_LONG_INDEX, TailGene.getInstance().getSecondaryPhenotypeCount() );
        setDataAtIndex( TEETH_SHORT_INDEX, TeethGene.getInstance().getPrimaryPhenotypeCount() );
        setDataAtIndex( TEETH_LONG_INDEX, TeethGene.getInstance().getSecondaryPhenotypeCount() );

        pos++;

        if ( pos >= RANGE + 1 ) {
            // TODO: (medium) possibly remove old data points? (may be a memory issue if this sim is left running for hours in a stable state
            low++;
        }

        // this will cause a redraw
        mainPlot.getDomainAxis().setRange( low, low + RANGE );
    }
}
