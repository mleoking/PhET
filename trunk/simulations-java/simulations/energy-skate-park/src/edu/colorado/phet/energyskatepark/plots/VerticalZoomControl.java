package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import org.jfree.chart.axis.ValueAxis;

/**
 * Author: Sam Reid
* May 22, 2007, 3:52:05 AM
*/
public class VerticalZoomControl extends DefaultZoomControl {

    public VerticalZoomControl( ValueAxis axis ) {
        super( ZoomControlNode.VERTICAL, axis );
    }

    protected void updateZoom() {
        setZoomInEnabled( zoom > -5 );
        setZoomOutEnabled( zoom < 7 );
        double range = 7000 + zoom * 1000;
        range = Math.max( range, 0 );
        double minY = zoom < 0 ? -500 : -500 - zoom * 500;
        axis.setRange( minY, minY + range );
    }
}
