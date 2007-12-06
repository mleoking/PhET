package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.JFreeChartDecorator;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.motion.model.UpdateableObject;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;

/**
 * Author: Sam Reid
 * Jul 18, 2007, 5:45:18 PM
 */
public class MovingManGraph extends MotionControlGraph {

    public MovingManGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title, double min, double max, final MotionModel motionModel, boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, double maxDomainValue, final UpdateableObject iPositionDriven ) {
        super( createMovingManJFreeChart(), pSwingCanvas, series, label, title, min, max, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        getJFreeChartNode().getChart().setBackgroundPaint( MotionProjectLookAndFeel.BACKGROUND_COLOR );
        getJFreeChartNode().getChart().getXYPlot().setBackgroundPaint( MotionProjectLookAndFeel.CHART_BACKGROUND_COLOR );
        DynamicJFreeChartNode dj = (DynamicJFreeChartNode) getJFreeChartNode();
        dj.updateAll();
    }

    public static JFreeChart createMovingManJFreeChart() {

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRangeIncludesZero( false );
        xAxis.setTickLabelsVisible( false );
        xAxis.setTickUnit( new NumberTickUnit( 100 ) );

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFont( new PhetDefaultFont( 12, true ) );
        yAxis.setTickUnit( new NumberTickUnit( 5 ) );


        XYItemRenderer renderer = new XYLineAndShapeRenderer( true, false );
        XYDataset dataset = new XYSeriesCollection();
        XYPlot plot = new XYPlot( dataset, xAxis, yAxis, renderer );
        plot.setRangeGridlineStroke( new BasicStroke( 1 ) );
        plot.setRangeGridlinePaint( Color.lightGray );

        plot.setDomainGridlineStroke( new BasicStroke( 1 ) );
        plot.setDomainGridlinePaint( Color.lightGray);

//        plot.setDomainGridlinesVisible();
        plot.setOrientation( PlotOrientation.VERTICAL );
        final JFreeChartDecorator chart = new JFreeChartDecorator( null, JFreeChart.DEFAULT_TITLE_FONT, plot, false );
        chart.addJFreeChartNodeGraphic( new JFreeChartDecorator.InChartTickMarks( 0, 100, 10 ) );
        chart.addJFreeChartNodeGraphic( new JFreeChartDecorator.DottedZeroLine() );

        return chart;
    }

}
