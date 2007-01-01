package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 10:59:15 AM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

public class BasicVerticalSliderChart extends PNode {
    private JFreeChartNode jFreeChartNode;
    private BasicChartSlider basicChartSlider;

    public BasicVerticalSliderChart( JFreeChartNode jFreeChartNode, PNode thumb ) {
        this.jFreeChartNode = jFreeChartNode;
        jFreeChartNode.updateChartRenderingInfo();
        this.basicChartSlider = new BasicChartSlider( jFreeChartNode, thumb );
        addChild( jFreeChartNode );
        addChild( basicChartSlider );
        relayout();
    }

    private void relayout() {
        basicChartSlider.setOffset( 0, 0 );
        jFreeChartNode.setOffset( 50, 0 );
    }
}
