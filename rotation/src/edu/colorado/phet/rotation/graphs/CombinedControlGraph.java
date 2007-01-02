package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:33:46 AM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

public class CombinedControlGraph extends PNode {
    private JFreeChart jFreeChart;
    private JFreeChartNode chartNode;
    private PNode controlNode;
    private PSwingCanvas pSwingCanvas;
    private ArrayList controlSets = new ArrayList();

    public CombinedControlGraph( PSwingCanvas pSwingCanvas, XYPlot[] subplot ) {
        this.pSwingCanvas = pSwingCanvas;
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot( new NumberAxis( "Domain" ) );
        plot.setOrientation( PlotOrientation.VERTICAL );
        plot.setGap( 10.0 );

        controlNode = new PNode();

        for( int i = 0; i < subplot.length; i++ ) {
            XYPlot xyPlot = subplot[i];
            plot.add( xyPlot );
        }

        // return a new chart containing the overlaid plot...
        this.jFreeChart = new JFreeChart( "Combined Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, true );

        chartNode = new JFreeChartNode( jFreeChart );
        addChild( chartNode );
        addChild( controlNode );
        chartNode.setOffset( 0, 0 );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                relayout();
            }
        } );
        timer.start();
    }

    public JFreeChart getJFreeChart() {
        return jFreeChart;
    }

    public JFreeChartNode getChartNode() {
        return chartNode;
    }

    public boolean setBounds( double x, double y, double width, double height ) {
        chartNode.setBounds( x, y, width, height );
        return super.setBounds( x, y, width, height );
    }

    public void addDefaultControlSet( int subplotIndex, String title, String units, String abbreviation, SimulationVariable simulationVariable, GraphTimeSeries graphTimeSeries ) {
        PNode sliderThumb = new PText( title );
        CombinedChartSlider combinedChartSlider = new CombinedChartSlider( chartNode, sliderThumb, subplotIndex );
        GraphControlNode graphControlNode = new GraphControlNode( pSwingCanvas, simulationVariable, graphTimeSeries );
        ZoomSuiteNode zoomSuiteNode = new ZoomSuiteNode();

        addControlSet( new ControlSet( subplotIndex, graphControlNode, combinedChartSlider, zoomSuiteNode ) );
        relayout();
    }

    private void addControlSet( ControlSet controlSet ) {
        controlSets.add( controlSet );
        addControl( controlSet );
    }

    static class ControlSet extends PNode {
        private int subplotIndex;
        private GraphControlNode graphControlNode;
        private CombinedChartSlider combinedChartSlider;
        private ZoomSuiteNode zoomSuiteNode;

        public ControlSet( int subplotIndex, GraphControlNode graphControlNode, CombinedChartSlider combinedChartSlider, ZoomSuiteNode zoomSuiteNode ) {
            this.subplotIndex = subplotIndex;
            this.graphControlNode = graphControlNode;
            this.combinedChartSlider = combinedChartSlider;
            this.zoomSuiteNode = zoomSuiteNode;

            addChild( graphControlNode );
            addChild( combinedChartSlider );
            addChild( zoomSuiteNode );
        }

        public GraphControlNode getGraphControlNode() {
            return graphControlNode;
        }

        public CombinedChartSlider getCombinedChartSlider() {
            return combinedChartSlider;
        }

        public ZoomSuiteNode getZoomSuiteNode() {
            return zoomSuiteNode;
        }

        public int getSubplotIndex() {
            return subplotIndex;
        }
    }

    public void relayout() {
        for( int i = 0; i < controlSets.size(); i++ ) {
            ControlSet controlSet = (ControlSet)controlSets.get( i );
            Rectangle2D dataArea = getChartNode().getDataArea( controlSet.getSubplotIndex() );
            controlSet.getGraphControlNode().setOffset( chartNode.getFullBounds().getX() - controlSet.getGraphControlNode().getFullBounds().getWidth() - 20, dataArea.getY() );
//            controlSet.getZoomSuiteNode().setOffset( dataArea.getMaxX(), dataArea.getCenterY() - controlSet.getZoomSuiteNode().getFullBounds().getHeight() / 2 );
            controlSet.getZoomSuiteNode().setOffset( chartNode.getFullBounds().getMaxX(), dataArea.getCenterY() - controlSet.getZoomSuiteNode().getFullBounds().getHeight() / 2 );
        }
    }

    public void addControl( PNode control ) {
        controlNode.addChild( control );
    }
}
