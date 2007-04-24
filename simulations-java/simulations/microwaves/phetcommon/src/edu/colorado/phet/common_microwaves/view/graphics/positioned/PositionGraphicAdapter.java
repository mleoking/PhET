/*, 2003.*/
package edu.colorado.phet.common_microwaves.view.graphics.positioned;

import edu.colorado.phet.common_microwaves.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 17, 2003
 * Time: 10:11:34 AM
 *
 */
public class PositionGraphicAdapter implements Graphic {
    PositionedGraphic lp;
    int x;
    int y;

    public PositionGraphicAdapter(PositionedGraphic lp, int x, int y) {
        this.x = x;
        this.y = y;
        this.lp = lp;
    }

    public void paint(Graphics2D graphics2D) {
        lp.paint(graphics2D, x, y);
    }
}
