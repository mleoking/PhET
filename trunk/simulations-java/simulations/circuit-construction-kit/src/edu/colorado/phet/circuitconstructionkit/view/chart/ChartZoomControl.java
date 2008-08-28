package edu.colorado.phet.circuitconstructionkit.view.chart;

import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Aug 28, 2008
 * Time: 11:19:45 AM
 */
public class ChartZoomControl extends PNode {
    public ChartZoomControl( final AbstractFloatingChart chart) {
        ZoomControlNode child = new ZoomControlNode( ZoomControlNode.VERTICAL );
        child.addZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                chart.zoomOut();
            }

            public void zoomedIn() {
                chart.zoomIn();
            }
        } );
        addChild( child );

    }
}
