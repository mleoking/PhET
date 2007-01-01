package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.jfreechart.piccolo.VerticalChartSlider;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:38:00 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class ControlGraph extends PNode {
    private GraphControlNode graphControlNode;
    private VerticalChartSlider verticalChartSlider;
    private ArrayList listeners = new ArrayList();
    private XYSeries xySeries;

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String title ) {
        int xIndex = 0;
        xySeries = new XYSeries( "series_1" );
//        for( xIndex = 0; xIndex < 100; xIndex++ ) {
//            series.add( xIndex, Math.sin( xIndex / 100.0 * Math.PI * 2 ) );
//        }
        XYDataset dataset = new XYSeriesCollection( xySeries );
        JFreeChart jFreeChart = ChartFactory.createXYLineChart( title, "time (s)", "value", dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.getXYPlot().getRangeAxis().setAutoRange( true );
//        jFreeChart.getXYPlot().getRangeAxis().setRange( -10, 10 );
        JFreeChartNode jFreeChartNode = new JFreeChartNode( jFreeChart );
        jFreeChartNode.setBounds( 0, 0, 500, 400 );
        verticalChartSlider = new VerticalChartSlider( jFreeChartNode, new PText( "THUMB" ) );

        graphControlNode = new GraphControlNode( pSwingCanvas, simulationVariable, new DefaultGraphTimeSeries() );

        addChild( graphControlNode );
        addChild( verticalChartSlider );

        simulationVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                verticalChartSlider.setValue( simulationVariable.getValue() );
            }
        } );
        verticalChartSlider.addListener( new VerticalChartSlider.Listener() {
            public void valueChanged() {
                simulationVariable.setValue( verticalChartSlider.getValue() );
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyListeners();
            }
        } );
        relayout();
    }

    private void relayout() {
        double dx = 5;
        graphControlNode.setOffset( 0, 0 );
        verticalChartSlider.setOffset( graphControlNode.getFullBounds().getMaxX() + dx, 0 );
    }

    public void addValue( double time, double value ) {
        xySeries.add( time, value );
    }

    public static interface Listener {
        void mousePressed();

        void valueChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.mousePressed();
        }
    }
}
