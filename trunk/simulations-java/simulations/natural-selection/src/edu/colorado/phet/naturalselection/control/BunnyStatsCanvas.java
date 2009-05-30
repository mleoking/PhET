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
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.XYPlotNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public class BunnyStatsCanvas extends PhetPCanvas {

    private static final boolean CHART_ANTIALIAS = false;
    private static final boolean PLOT_ANTIALIAS = false;

    private JFreeChartNode chartNode;
    private XYPlotNode plotNode;

    private XYSeriesCollection mainDataset;
    private XYSeriesCollection emptyDataset;

    private XYPlot emptyPlot;
    private XYPlot mainPlot;

    private static final int RANGE = 300;
    private static final int TOTAL_INDEX = 0;
    private static final int FUR_WHITE_INDEX = 1;
    private static final int FUR_BROWN_INDEX = 2;
    private static final int TAIL_SHORT_INDEX = 3;
    private static final int TAIL_LONG_INDEX = 4;
    private static final int TEETH_SHORT_INDEX = 5;
    private static final int TEETH_LONG_INDEX = 6;

    private static final int NUM_SERIES = 7;

    private PSwing zoomHolder;

    private final int[] zoomBounds = new int[]{5, 15, 30, 50, 75, 100, 150, 200, 250, 350, 500, 1000, 2000, 3000, 5000};
    private static final int DEFAULT_ZOOM_INDEX = 3;
    private int zoomIndex = DEFAULT_ZOOM_INDEX;

    private NaturalSelectionModel model;

    public static boolean allowUpdates = true;

    private JFreeChart chart;

    private int cachedPopulation = 0;


    public BunnyStatsCanvas( final NaturalSelectionModel model ) {
        super( new Dimension( 300, 200 ) );

        this.model = model;

        setBorder( null );

        PNode root = new PNode();
        addScreenChild( root );

        mainDataset = createDataset();
        emptyDataset = createDataset();

        emptyPlot = createPlot( emptyDataset );
        mainPlot = createPlot( mainDataset );

        chart = new JFreeChart( emptyPlot );
        chart.setBackgroundPaint( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        chart.setAntiAlias( CHART_ANTIALIAS );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

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

        model.getClock().addClockListener( new ClockListener() {

            int rot = 0;

            public void clockTicked( ClockEvent clockEvent ) {

            }

            public void clockStarted( ClockEvent clockEvent ) {

            }

            public void clockPaused( ClockEvent clockEvent ) {

            }

            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if ( cachedPopulation != model.getPopulation() ) {
                    cachedPopulation = model.getPopulation();
                    addDataPoint();
                }
                else if ( rot++ % 3 == 0 ) {
                    addDataPoint();
                }
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {

            }
        } );


        chart.addChangeListener( new ChartChangeListener() {
            public void chartChanged( ChartChangeEvent event ) {
                System.out.println( "Chart changed" );
            }
        } );

    }

    public void reset() {
        for ( int i = 0; i < NUM_SERIES; i++ ) {
            mainDataset.getSeries( i ).clear();
        }

        pos = 0;
        low = 0;

        zoomIndex = DEFAULT_ZOOM_INDEX;

        updateZoom();
        updateLayout();

    }

    private XYSeriesCollection createDataset() {
        XYSeriesCollection ret = new XYSeriesCollection();

        ret.addSeries( new XYSeries( "Total" ) );
        ret.addSeries( new XYSeries( "White fur" ) );
        ret.addSeries( new XYSeries( "Brown fur" ) );
        ret.addSeries( new XYSeries( "Short tail" ) );
        ret.addSeries( new XYSeries( "Long tail" ) );
        ret.addSeries( new XYSeries( "Short teeth" ) );
        ret.addSeries( new XYSeries( "Long teeth" ) );

        return ret;
    }

    private XYPlot createPlot( XYSeriesCollection dataset ) {
        XYPlot plot = new XYPlot();

        ValueAxis domainAxis = new NumberAxis( "Time" );
        domainAxis.setTickLabelsVisible( false );
        domainAxis.setRange( 0, RANGE );
        plot.setDomainAxis( domainAxis );

        ValueAxis rangeAxis = new NumberAxis( "Population" );
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        plot.setRangeAxis( rangeAxis );
        rangeAxis.setRange( 0, 50 );

        plot.setRenderer( new StandardXYItemRenderer() );

        final int seriesIndex = 0;
        plot.setDataset( seriesIndex, dataset );
        XYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setStroke( new BasicStroke( 3f ) );
        renderer.setSeriesPaint( TOTAL_INDEX, Color.BLACK );
        renderer.setSeriesPaint( FUR_WHITE_INDEX, Color.RED );
        renderer.setSeriesPaint( FUR_BROWN_INDEX, Color.CYAN );
        renderer.setSeriesPaint( TAIL_SHORT_INDEX, Color.BLUE );
        renderer.setSeriesPaint( TAIL_LONG_INDEX, Color.ORANGE );
        renderer.setSeriesPaint( TEETH_SHORT_INDEX, Color.YELLOW.darker() );
        renderer.setSeriesPaint( TEETH_LONG_INDEX, Color.MAGENTA );
        plot.setRenderer( seriesIndex, renderer );

        return plot;
    }

    @Override
    protected void updateLayout() {

        // chart
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

    private int pos = 0;
    private int low = 0;

    private int[] cachedPositions = new int[]{2, 2, 2, 2, 2, 2, 2};
    private boolean[] cachedValid = new boolean[]{false, false, false, false, false, false, false};

    public void setDataAtIndex( int index, int value ) {
        XYSeries series = mainDataset.getSeries( index );

        if ( cachedPositions[index] == value ) {
            if ( cachedValid[index] == true ) {
                series.setNotify( false );
                series.remove( series.getItemCount() - 1 );
                series.setNotify( true );
            }
            else {
                cachedValid[index] = true;
            }
        }
        else {
            cachedValid[index] = false;
            cachedPositions[index] = value;
        }

        series.add( pos, value, false );
    }

    public synchronized void addDataPoint() {
        if ( !allowUpdates ) {
            return;
        }

        int total = model.getPopulation();

        setDataAtIndex( TOTAL_INDEX, total );
        setDataAtIndex( FUR_WHITE_INDEX, ColorGene.getInstance().getPrimaryPhenotypeCount() );
        setDataAtIndex( FUR_BROWN_INDEX, ColorGene.getInstance().getSecondaryPhenotypeCount() );
        setDataAtIndex( TAIL_SHORT_INDEX, TailGene.getInstance().getPrimaryPhenotypeCount() );
        setDataAtIndex( TAIL_LONG_INDEX, TailGene.getInstance().getSecondaryPhenotypeCount() );
        setDataAtIndex( TEETH_SHORT_INDEX, TeethGene.getInstance().getPrimaryPhenotypeCount() );
        setDataAtIndex( TEETH_LONG_INDEX, TeethGene.getInstance().getSecondaryPhenotypeCount() );

        pos++;

        if ( pos >= RANGE + 1 ) {
            // TODO: possibly remove old data points?
            low++;

        }

        // this will cause a redraw
        mainPlot.getDomainAxis().setRange( low, low + RANGE );
    }
}
