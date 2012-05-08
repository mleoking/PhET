// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
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
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
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
    private static final IntegerRange PROTEIN_LEVEL_RANGE = new IntegerRange( 0, 170 );

    private final XYSeries dataSeries = new XYSeries( "0" );
    private double timeOffset = 0;

    public ProteinLevelChartNode( final Property<Double> averageProteinLevelProperty, final IClock clock ) {
        XYDataset dataSet = new XYSeriesCollection( dataSeries );
        // Create the chart itself, i.e. the place where data will be shown.
        // TODO: i18n
        JFreeChart chart = createXYLineChart( "Average Protein Level vs. Time", "Time", null, dataSet, PlotOrientation.VERTICAL );

        // Create and configure the x axis.
        NumberAxis xAxis = new NumberAxis( "Time(s)" ); // TODO: i18n
        xAxis.setRange( 0, TIME_SPAN );
        xAxis.setNumberFormatOverride( new DecimalFormat( "##" ) );
        xAxis.setLabelFont( new PhetFont( 12 ) );
        chart.getXYPlot().setDomainAxis( xAxis );

        // Make the Y axis, and have it be essentially blank, since we are
        // going to create our own custom label.
        NumberAxis yAxis = new NumberAxis();
        yAxis.setRange( PROTEIN_LEVEL_RANGE.getMin(), PROTEIN_LEVEL_RANGE.getMax() );
        yAxis.setTickLabelsVisible( false ); // Y axis label is provided elsewhere.
        yAxis.setTickMarksVisible( false );
        chart.getXYPlot().setRangeAxis( yAxis );

        // Embed the chart in a PNode.
        JFreeChartNode jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, SIZE.getWidth(), SIZE.getHeight() );
        jFreeChartNode.updateChartRenderingInfo();

        // Create the Y axis label, which includes a key to the colors used to
        // indicate the protein level in the cells.  The size an position are
        // empirically determined and may need to change if the chart size
        // changes.
        PNode yAxisLabelNode = new YAxisLabel( jFreeChartNode.getFullBoundsReference().height * 0.65 );
        yAxisLabelNode.setOffset( 0, 29 );

        // Lay out the chart and the Y axis label in parent PNode.
        PNode contents = new PNode();
        contents.addChild( yAxisLabelNode );
        jFreeChartNode.setOffset( yAxisLabelNode.getFullBoundsReference().width, 0 );
        contents.addChild( jFreeChartNode );

        // Put the content in a control panel node in order to give it a decent
        // looking border.
        addChild( new ControlPanelNode( contents ) );

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
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

    /**
     * Convenience class for combining the elements of the label for the
     * chart's y axis.
     */
    public static class YAxisLabel extends PNode {
        public YAxisLabel( final double height ) {
            // Labels for the top and bottom of the gradient key.
            // TODO: i18n
            PText lotsLabel = new PText( GeneExpressionBasicsResources.Strings.LOTS );
            lotsLabel.setFont( new PhetFont( 12 ) );
            PText noneLabel = new PText( GeneExpressionBasicsResources.Strings.NONE );
            noneLabel.setFont( new PhetFont( 12 ) );

            // Create an invisible rectangle that will serve as a spacer for
            // positioning the "None" and "Lots" labels.
            PPath spacerRect = new PhetPPath( new Rectangle2D.Double( 0,
                                                                      0,
                                                                      Math.max( lotsLabel.getFullBoundsReference().width, noneLabel.getFullBoundsReference().width ) * 1.1,
                                                                      height - lotsLabel.getFullBoundsReference().height / 2 - noneLabel.getFullBoundsReference().height / 2 ) );
            spacerRect.setStroke( null );

            // Box up the "Lots" and "None" labels with the spacer rect.
            PNode tickLabelsNode = new VBox( 0, lotsLabel, spacerRect, noneLabel );

            // Create a rectangle with a gradient that maps the amount of
            // protein to a color.  Width is arbitrarily chosen.
            PPath proteinLevelColorKey = new PhetPPath( new Rectangle2D.Double( 0, 0, 20, height ) );
            proteinLevelColorKey.setStroke( new BasicStroke( 1 ) );
            proteinLevelColorKey.setPaint( new GradientPaint( 0, (float) height, ColorChangingCellNode.NOMINAL_FILL_COLOR, 0, 0, ColorChangingCellNode.FLORESCENT_FILL_COLOR ) );

            // Create the main label.
            // TODO: i18n
            PNode mainLabel = new ZeroOffsetNode( new PText( "Average Protein Level" ) {{
                setFont( new PhetFont( 14 ) );
                rotate( -Math.PI / 2 );
            }} );

            // Add all the parts to the parent node.  Set this up so that the
            // upper Y coordinate is based on the gradient rectangle and NOT
            // the label.  This is done so that translation of the labels
            // doesn't mess up the layout of the overall chart.
            mainLabel.setOffset( 0, height / 2 - mainLabel.getFullBoundsReference().height / 2 );
            addChild( mainLabel );
            tickLabelsNode.setOffset( mainLabel.getFullBoundsReference().getMaxX(), height / 2 - tickLabelsNode.getFullBoundsReference().height / 2 );
            addChild( tickLabelsNode );
            proteinLevelColorKey.setOffset( tickLabelsNode.getFullBoundsReference().getMaxX() + 3, 0 );
            addChild( proteinLevelColorKey );
        }
    }
}
