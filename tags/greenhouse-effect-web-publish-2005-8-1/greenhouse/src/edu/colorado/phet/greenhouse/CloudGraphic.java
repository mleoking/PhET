/**
 * Class: CloudGraphic
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 4:59:46 PM
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.ShapeGraphicType;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.*;

public class CloudGraphic implements Graphic, ShapeGraphicType {

    private Ellipse2D.Double bounds = new Ellipse2D.Double();
    private Cloud cloud;
    private Paint cloudPaint = Color.gray;

    public CloudGraphic( Cloud cloud ) {
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
