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
        chartNode.setOffset( 50, 0 );
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
        addControl( combinedChartSlider );

        GraphControlNode graphControlNode = new GraphControlNode( pSwingCanvas, simulationVariable, graphTimeSeries );
        addControl( graphControlNode );


    }

    public void addControl( PNode control ) {
        controlNode.addChild( control );
    }

}
