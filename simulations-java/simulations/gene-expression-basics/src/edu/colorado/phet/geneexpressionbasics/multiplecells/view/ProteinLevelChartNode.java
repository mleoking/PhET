// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
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

    private static final Dimension2D SIZE = new PDimension( 400, 200 );  // In screen coordinates, which is close to pixels.
    private static final double TIME_SPAN = 30; // In seconds.
    private static final IntegerRange PROTEIN_LEVEL_RANGE = new IntegerRange( 0, 300 );

    private final XYSeries dataSeries = new XYSeries( "0" );
    private double timeOffset = 0;

    public ProteinLevelChartNode( final Property<Double> averageProteinLevelProperty, final IClock clock ) {
        XYDataset dataSet = new XYSeriesCollection( dataSeries );
        // Create the chart itself, i.e. the place where data will be shown.
        // TODO: i18n
        JFreeChart chart = createXYLineChart( "Average Protein Level vs. Time", "Time", null, dataSet, PlotOrientation.VERTICAL );

        // Create and configure the x axis.
        NumberAxis xAxis = new NumberAxis( "Time(s)" );
        xAxis.setRange( 0, TIME_SPAN );
        xAxis.setNumberFormatOverride( new DecimalFormat( "##" ) );
        xAxis.setLabelFont( new PhetFont( 12 ) );
        chart.getXYPlot().setDomainAxis( xAxis );

        // Create a Y axis with "None" and "Lots" as the labels.  There may be
        // a better way to do with in JFreeChart, but this is the best I could
        // come up with with limited time.
//        String[] yAxisLabels = new String[PROTEIN_LEVEL_RANGE.getMax() + 1];
//        for ( int i = 0; i <= PROTEIN_LEVEL_RANGE.getMax(); i++ ) {
//            yAxisLabels[i] = "";
//        }
//        yAxisLabels[PROTEIN_LEVEL_RANGE.getMin()] = "None";
//        yAxisLabels[PROTEIN_LEVEL_RANGE.getMax()] = "Lots";
//        SymbolAxis yAxis = new SymbolAxis( "Average Protein Level", yAxisLabels );
//        yAxis.setTickUnit( new NumberTickUnit( PROTEIN_LEVEL_RANGE.getLength() / 4 ) );
//        yAxis.setLabelFont( new PhetFont( 12 ) );
//        yAxis.setRange( PROTEIN_LEVEL_RANGE.getMin(), PROTEIN_LEVEL_RANGE.getMax() );
//        chart.getXYPlot().setRangeAxis( yAxis );

        NumberAxis yAxis = new NumberAxis();
        yAxis.setRange( 0, TIME_SPAN );
        yAxis.setRange( PROTEIN_LEVEL_RANGE.getMin(), PROTEIN_LEVEL_RANGE.getMax() );
        yAxis.setTickLabelsVisible( false ); // Y axis label is provided elsewhere.
        yAxis.setTickMarksVisible( false );
        chart.getXYPlot().setRangeAxis( yAxis );

        // Embed the chart in a PNode.
        JFreeChartNode jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, SIZE.getWidth(), SIZE.getHeight() );
        jFreeChartNode.updateChartRenderingInfo();

        // Create a rectangle with a gradient that maps the amount of protein
        // to a color.  This is admittedly a bit "tweaky", and the size and
        // position may need to be adjusted if the size of the chart changes.
        PPath proteinLevelColorKey = new PhetPPath( new Rectangle2D.Double( 0, 0, 20, jFreeChartNode.getFullBounds().height * 0.7 ) );
        proteinLevelColorKey.setStroke( new BasicStroke( 1 ) );
        proteinLevelColorKey.setPaint( Color.GREEN );

        // Put the chart and the Y axis label in a horizontal box.  The
        // height of the Y axis label is a bit "tweaky" and may need to be
        // adjusted if the size of the chart changes.
        PNode contents = new HBox( 0, new YAxisLabel( jFreeChartNode.getFullBoundsReference().height * 0.6 ), jFreeChartNode );

        // Put the content in a control panel node in order to give it a decent
        // looking border.
        addChild( new ControlPanelNode( contents ) );

        // Hook up a listener to the average protein level and update the data
        // on the chart.
        averageProteinLevelProperty.addObserver( new VoidFunction1<Double>() {
            public void apply( Double averageProteinLevel ) {
                if ( clock.getSimulationTime() - timeOffset > TIME_SPAN ) {
                    // If the end of the chart has been reached, clear it.
                    clear();
                }
                if ( dataSeries.getItemCount() == 0 ) {
                    // This is the first data added after the most recent
                    // clear, so record the time offset.
                    timeOffset = clock.getSimulationTime();
                }
                // Add the data to the chart.
                dataSeries.add( clock.getSimulationTime() - timeOffset, averageProteinLevelProperty.get() );
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

    public void clear() {
        // Clear the data series, which clears the chart.
        dataSeries.clear();
    }

    // Convenience class for combining the elements of the label for the
    // chart's y axis.
    public static class YAxisLabel extends PNode {
        public YAxisLabel( double height ) {
            // TODO: i18n
            PText mainLabel = new PText( "Average Protein Level" ) {{
                setFont( new PhetFont( 12 ) );
                rotate( -Math.PI / 2 );
            }};

            // Labels for the top and bottom of the scale.
            // TODO: i18n
            PText lotsLabel = new PText( "Lots" );
            lotsLabel.setFont( new PhetFont( 12 ) );
            PText noneLabel = new PText( "None" );
            noneLabel.setFont( new PhetFont( 12 ) );

            // Create an invisible rectangle that will serve as a spacer for
            // positioning the "None" and "Lots" labels.
            PPath spacerRect = new PhetPPath( new Rectangle2D.Double( 0,
                                                                      0,
                                                                      Math.max( lotsLabel.getFullBoundsReference().width, noneLabel.getFullBoundsReference().width ) * 1.1,
                                                                      height - lotsLabel.getFullBoundsReference().height / 2 - noneLabel.getFullBoundsReference().height / 2 ) );
            spacerRect.setStroke( null );
            PNode tickLabelsNode = new VBox( 0, lotsLabel, spacerRect, noneLabel );

            // Create a rectangle with a gradient that maps the amount of
            // protein to a color.  Width is arbitrarily chosen.
            PPath proteinLevelColorKey = new PhetPPath( new Rectangle2D.Double( 0, 0, 20, height ) );
            proteinLevelColorKey.setStroke( new BasicStroke( 1 ) );
            proteinLevelColorKey.setPaint( Color.GREEN );

            addChild( new HBox( mainLabel, tickLabelsNode, proteinLevelColorKey ) );
        }
    }
}
