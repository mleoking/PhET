package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.geom.Point2D;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.JFreeChartDecorator;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.motion.model.UpdateableObject;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Author: Sam Reid
 * Jul 18, 2007, 5:45:18 PM
 */
public class MovingManGraph extends MotionControlGraph {
    public MovingManGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title, double min, double max,
                           final MotionModel motionModel, boolean editable, final TimeSeriesModel timeSeriesModel,
                           final UpdateStrategy updateStrategy, double maxDomainValue, final UpdateableObject iPositionDriven ) {
        this( pSwingCanvas, series, label, title, min, max, motionModel, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven, 5 );
    }

    public MovingManGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title, double min, double max,
                           final MotionModel motionModel, boolean editable, final TimeSeriesModel timeSeriesModel,
                           final UpdateStrategy updateStrategy, double maxDomainValue, final UpdateableObject iPositionDriven, int verticalTickUnit ) {
        super( createMovingManJFreeChart( verticalTickUnit ), pSwingCanvas, series, label, title, min, max, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        getJFreeChartNode().getChart().setBackgroundPaint( MotionProjectLookAndFeel.BACKGROUND_COLOR );
        getJFreeChartNode().getChart().getXYPlot().setBackgroundPaint( MotionProjectLookAndFeel.CHART_BACKGROUND_COLOR );
        getJFreeChartNode().updateAll();
        getJFreeChartNode().setPiccoloSeries();
        addInputEventListener( new PBasicInputEventHandler() {
            public Point2D lastPlotPoint;

            public void mousePressed( PInputEvent event ) {
                try {
                    lastPlotPoint = getJFreeChartNode().nodeToPlot( event.getPositionRelativeTo( getJFreeChartNode() ) );
                }
                catch( Exception e ) {

                }
            }

            public void mouseDragged( PInputEvent event ) {
                try {
                    Point2D plotPT = getJFreeChartNode().nodeToPlot( event.getPositionRelativeTo( getJFreeChartNode() ) );
//                System.out.println( "plotPT = " + plotPT );
                    int index = series.getTemporalVariable().getIndexForTime( plotPT.getX() );
//                System.out.println( "index = " + index );

                    //remove other intervening points
                    int[] indices = series.getTemporalVariable().getIndicesForTimeInterval( lastPlotPoint.getX(), plotPT.getX() );
//                series.getTemporalVariable().removeAll( indices );

                    for ( int i = 0; i < indices.length; i++ ) {
                        series.getTemporalVariable().setTimeData( indices[i], series.getTemporalVariable().getData( index ).getTime(), plotPT.getY() );
                    }

                    //update other series
                    forceUpdateAll();
//                series.getTemporalVariable().getData( index ).setValue()
                    lastPlotPoint = plotPT;
                }
                catch( Exception e ) {

                }
            }
        } );
    }

    public void forceUpdateAll() {
        super.rebuildSeries();
        super.forceUpdateAll();
    }

    public static JFreeChart createMovingManJFreeChart( int verticalTickUnit ) {

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRangeIncludesZero( false );
        xAxis.setTickLabelsVisible( false );
        xAxis.setTickUnit( new NumberTickUnit( 5 ) );

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFont( new PhetFont( 12, true ) );
        yAxis.setTickUnit( new NumberTickUnit( verticalTickUnit ) );


        XYItemRenderer renderer = new XYLineAndShapeRenderer( true, false );
        XYDataset dataset = new XYSeriesCollection();
        XYPlot plot = new XYPlot( dataset, xAxis, yAxis, renderer );
        plot.setRangeGridlineStroke( new BasicStroke( 1 ) );
        plot.setRangeGridlinePaint( Color.lightGray );

        plot.setDomainGridlineStroke( new BasicStroke( 1 ) );
        plot.setDomainGridlinePaint( Color.lightGray );

//        plot.setDomainGridlinesVisible();
        plot.setOrientation( PlotOrientation.VERTICAL );
        final JFreeChartDecorator chart = new JFreeChartDecorator( null, JFreeChart.DEFAULT_TITLE_FONT, plot, false );
        chart.addJFreeChartNodeGraphic( new JFreeChartDecorator.InChartTickMarks( 0, 2, 11 ) );
        chart.addJFreeChartNodeGraphic( new JFreeChartDecorator.DottedZeroLine() );

        return chart;
    }
}
