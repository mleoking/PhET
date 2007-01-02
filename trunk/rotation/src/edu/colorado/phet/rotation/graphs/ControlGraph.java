package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
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
    private BasicChartSlider basicChartSlider;
    private ZoomSuiteNode zoomControl;

    private ArrayList listeners = new ArrayList();
    private XYSeries xySeries;
    private double xPad = 0;
    private JFreeChartNode jFreeChartNode;

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String title ) {
        xySeries = new XYSeries( "series_1" );

        XYDataset dataset = new XYSeriesCollection( xySeries );
        JFreeChart jFreeChart = ChartFactory.createXYLineChart( title, "time (s)", "value", dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.getXYPlot().getRangeAxis().setAutoRange( true );
        jFreeChart.setBackgroundPaint( null );
        jFreeChartNode = new JFreeChartNode( jFreeChart );
        jFreeChartNode.setBounds( 0, 0, 300, 400 );
        graphControlNode = new GraphControlNode( pSwingCanvas, simulationVariable, new DefaultGraphTimeSeries() );
        basicChartSlider = new BasicChartSlider( jFreeChartNode, new PText( "THUMB" ) );
        zoomControl = new ZoomSuiteNode();
        addChild( graphControlNode );
        addChild( basicChartSlider );
        addChild( jFreeChartNode );
        addChild( zoomControl );

        simulationVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                basicChartSlider.setValue( simulationVariable.getValue() );
            }
        } );
        basicChartSlider.addListener( new AbstractChartSlider.Listener() {
            public void valueChanged() {
                simulationVariable.setValue( basicChartSlider.getValue() );
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyListeners();
            }
        } );
        relayout();
    }

    public boolean setBounds( double x, double y, double width, double height ) {
        relayout();
        jFreeChartNode.setBounds( 0, 0, width - xPad, height );
        relayout();
        setOffset( x, y );
        return super.setBounds( x, y, width, height );
    }

    private void relayout() {
        double dx = 5;
        graphControlNode.setOffset( 0, 0 );
        basicChartSlider.setOffset( graphControlNode.getFullBounds().getMaxX() + dx, 0 );
        jFreeChartNode.setOffset( basicChartSlider.getFullBounds().getMaxX(), 0 );
        zoomControl.setOffset( jFreeChartNode.getFullBounds().getMaxX(), jFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );

        this.xPad = jFreeChartNode.getFullBounds().getX() + zoomControl.getFullBounds().getWidth();
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
