/**
 * Class: CloudView
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 4:59:46 PM
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.*;

public class CloudView implements Graphic {

    private Ellipse2D.Double bounds;
    private Cloud cloud;
    private Paint cloudPaint = Color.gray;

    public CloudView( Cloud cloud ) {
        this.cloud = cloud;
        this.update();
    }

    public void paint( Graphics2D g ) {
        RenderingHints orgRenderingHints = g.getRenderingHints();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setPaint( cloudPaint );
        g.fill( bounds );
        g.setRenderingHints( orgRenderingHints );
    }

    public void update() {
        bounds = cloud.getBounds();
    }
}
