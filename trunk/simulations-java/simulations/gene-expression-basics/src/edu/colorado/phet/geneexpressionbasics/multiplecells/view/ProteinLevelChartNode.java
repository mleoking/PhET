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
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
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
public class ProteinLevelChartNode extends PNode implements Resettable {

    private static final Dimension2D SIZE = new PDimension( 400, 200 );  // In screen coordinates, which is close to pixels.
    private static final double TIME_SPAN = 30; // In seconds.

    private final IClock clock;
    private final XYSeries dataSeries = new XYSeries( "0" );
    private double timeOffset = 0;

    public ProteinLevelChartNode( final Property<Double> observableDataValue, final IClock clock ) {
        this.clock = clock;
        XYDataset dataSet = new XYSeriesCollection( dataSeries );
        // Create the chart itself, i.e. the place where data will be shown.
        // TODO: i18n
        JFreeChart chart = createXYLineChart( "Average Protein Level vs. Time", "Time", "Average Protein Level", dataSet, PlotOrientation.VERTICAL );
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( true );
        chart.getXYPlot().getRangeAxis().setRange( 0, 300 );
        chart.getXYPlot().getDomainAxis().setRange( 0, TIME_SPAN );

        // Embed the chart in a PNode.
        JFreeChartNode jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, SIZE.getWidth(), SIZE.getHeight() );
        jFreeChartNode.updateChartRenderingInfo();

        // Put the chart in a control panel node in order to give it a decent
        // looking border.
        PNode panel = new ControlPanelNode( jFreeChartNode );
        addChild( panel );

        // Hook up a listener to the average protein level and update the data
        // data on the chart.
        observableDataValue.addObserver( new VoidFunction1<Double>() {
            public void apply( Double aDouble ) {
                if ( clock.getSimulationTime() - timeOffset > TIME_SPAN ) {
                    reset();
                }
                // Add the data to the chart.
                dataSeries.add( clock.getSimulationTime() - timeOffset, observableDataValue.get() );
            }
        } );
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
        renderer.setStroke( new BasicStroke( 2f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL ) );

        return chart;
    }

    public void reset() {
        // Clear the chart.
        dataSeries.clear();

        // Save the time offset for charting new data that is added.
        timeOffset = clock.getSimulationTime();
    }
}
