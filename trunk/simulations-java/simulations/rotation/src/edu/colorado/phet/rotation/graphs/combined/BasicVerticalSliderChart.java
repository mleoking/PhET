package edu.colorado.phet.rotation.graphs.combined;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 10:59:15 AM
 *
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
