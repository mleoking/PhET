// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.BasicStroke;
import java.awt.geom.Dimension2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class defines a PNode that displays the average protein level for a
 * population of cells.
 * <p/>
 * This wouldn't be too hard to generalize to make it work for any
 * Property<Double>, but it wasn't worth it at the time of the original
 * creation.
 *
 * @author John Blanco
 */
public class ProteinLevelChartNode extends PNode {

    private static final Dimension2D SIZE = new PDimension( 300, 200 );  // In screen coordinates, which is close to pixels.
    private static final double TIME_SPAN = 60; // In seconds.

    private final XYSeries dataSeries = new XYSeries( "0" );

    public ProteinLevelChartNode() {
        // Create the chart itself, i.e. the place where date will be shown.
        XYDataset dataSet = new XYSeriesCollection( dataSeries );
        JFreeChart chart = createXYLineChart( "Average Protein Level vs. Time", "Time", "Average Protein Level", dataSet, PlotOrientation.VERTICAL );
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( true );
        chart.getXYPlot().getRangeAxis().setRange( -100, 100 );
        JFreeChartNode jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, SIZE.getWidth(), SIZE.getHeight() );
        chart.getXYPlot().getDomainAxis().setRange( 0, TIME_SPAN );
        jFreeChartNode.updateChartRenderingInfo();

        // Add the chart to this node.
        addChild( jFreeChartNode );


    }

    /**
     * Create the JFreeChart chart that will show the data and that will be
     * contained by this node.
     *
     * @param title
     * @param xAxisLabel
     * @param yAxisLabel
     * @param dataSet
     * @param orientation
     * @return
     */
    private static JFreeChart createXYLineChart( String title, String xAxisLabel, String yAxisLabel,
                                                 XYDataset dataSet, PlotOrientation orientation ) {

        if ( orientation == null ) {
            throw new IllegalArgumentException( "Null 'orientation' argument." );
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataSet,
                PlotOrientation.VERTICAL,
                false, // legend
                false, // tooltips
                false // urls
        );

        // Set the stroke for the data line to be larger than the default.
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setStroke( new BasicStroke( 3f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL ) );

        return chart;
    }
}
