package edu.colorado.phet.naturalselection.test;


import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.LinkedList;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.XYPlotNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

public class TestCanvas extends PhetPCanvas {
    private JFreeChartNode chartNode;
    private XYPlotNode plotNode;

    private XYSeries totalSeries;
    private XYSeries whiteSeries;

    private List<XYSeries> mainSeriesList;
    private List<XYSeries> emptySeriesList;

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

    public TestCanvas( Dimension2D renderingSize ) {
        super( renderingSize );

        PNode root = new PNode();
        addScreenChild( root );

        mainSeriesList = createSeriesList();
        emptySeriesList = createSeriesList();

        emptyPlot = createPlot( emptySeriesList );
        mainPlot = createPlot( mainSeriesList );
        
        JFreeChart chart = new JFreeChart( emptyPlot );

        chartNode = new JFreeChartNode( chart );
        root.addChild( chartNode );

        final int totalIndex = 0;
        totalSeries = new XYSeries( "Total" );
        XYSeriesCollection totalDataset = new XYSeriesCollection();
        totalDataset.addSeries( totalSeries );

        final int whiteIndex = 1;
        whiteSeries = new XYSeries( "White Fur" );
        XYSeriesCollection whiteDataset = new XYSeriesCollection();
        whiteDataset.addSeries( whiteSeries );

        mainPlot.setDataset( totalIndex, totalDataset );
        mainPlot.setDataset( whiteIndex, whiteDataset );
        XYItemRenderer totalRenderer = new StandardXYItemRenderer();
        totalRenderer.setPaint( Color.RED );
        totalRenderer.setStroke( new BasicStroke( 2f ) );
        mainPlot.setRenderer( totalIndex, totalRenderer );
        XYItemRenderer whiteRenderer = new StandardXYItemRenderer();
        whiteRenderer.setPaint( Color.BLUE );
        whiteRenderer.setStroke( new BasicStroke( 2f ) );
        mainPlot.setRenderer( whiteIndex, whiteRenderer );

        plotNode = new XYPlotNode( mainPlot );
        root.addChild( plotNode );

        updateLayout();
    }

    private List<XYSeries> createSeriesList() {
        List<XYSeries> ret = new LinkedList<XYSeries>();

        ret.add( new XYSeries( "Total" ) );
        ret.add( new XYSeries( "White fur" ) );
        ret.add( new XYSeries( "Brown fur" ) );
        ret.add( new XYSeries( "Short tail" ) );
        ret.add( new XYSeries( "Long tail" ) );
        ret.add( new XYSeries( "Short teeth" ) );
        ret.add( new XYSeries( "Long teeth" ) );

        return ret;
    }

    private XYPlot createPlot( List<XYSeries> seriesList ) {
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
    }

    private int pos = 0;
    private int low = 0;
    private int MIN_Y = 0;
    private int MAX_Y = 50;
    private double lastTotalY = 25;
    private double lastWhiteY = 20;

    public void addDataPoint() {
        totalSeries.setNotify( false );
        whiteSeries.setNotify( false );

        lastTotalY += Math.random() * 4 - 2;
        if ( lastTotalY > MAX_Y ) { lastTotalY = MAX_Y; }
        if ( lastTotalY < MIN_Y ) { lastTotalY = MIN_Y; }

        totalSeries.add( pos, lastTotalY );

        lastWhiteY += Math.random() * 4 - 2;
        if ( lastWhiteY > MAX_Y ) { lastWhiteY = MAX_Y; }
        if ( lastWhiteY < MIN_Y ) { lastWhiteY = MIN_Y; }

        whiteSeries.add( pos, lastWhiteY );

        pos++;

        if ( pos >= RANGE + 1 ) {
            //totalSeries.remove( 0 );
            //whiteSeries.remove( 0 );
            low++;

            mainPlot.getDomainAxis().setRange( low, low + RANGE );
        }

        totalSeries.setNotify( true );
        whiteSeries.setNotify( true );
    }

}
