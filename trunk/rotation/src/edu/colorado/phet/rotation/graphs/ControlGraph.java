package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.jfreechart.piccolo.VerticalChartControl;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:38:00 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class ControlGraph extends PNode {
    private GraphControlNode graphControlNode;
    private VerticalChartControl verticalChartControl;

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable ) {
        int xIndex = 0;
        final XYSeries series = new XYSeries( "series_1" );
        for( xIndex = 0; xIndex < 100; xIndex++ ) {
            series.add( xIndex, Math.sin( xIndex / 100.0 * Math.PI * 2 ) );
        }
        XYDataset dataset = new XYSeriesCollection( series );
        JFreeChart jFreeChart = ChartFactory.createXYLineChart( "title", "x", "y", dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.getXYPlot().getRangeAxis().setAutoRange( false );
        jFreeChart.getXYPlot().getRangeAxis().setRange( -2, 2 );
        JFreeChartNode jFreeChartNode = new JFreeChartNode( jFreeChart );
        jFreeChartNode.setBounds( 0, 0, 500, 400 );
        verticalChartControl = new VerticalChartControl( jFreeChartNode, new PText( "THUMB" ) );

        graphControlNode = new GraphControlNode( pSwingCanvas, simulationVariable );

        addChild( graphControlNode );
        addChild( verticalChartControl );

        simulationVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                verticalChartControl.setValue( simulationVariable.getValue() );
            }
        } );
        verticalChartControl.addListener( new VerticalChartControl.Listener() {
            public void valueChanged() {
                simulationVariable.setValue( verticalChartControl.getValue() );
            }
        } );
        relayout();
    }

    private void relayout() {
        double dx = 5;
        graphControlNode.setOffset( 0, 0 );
        verticalChartControl.setOffset( graphControlNode.getFullBounds().getMaxX() + dx, 0 );
    }
}
