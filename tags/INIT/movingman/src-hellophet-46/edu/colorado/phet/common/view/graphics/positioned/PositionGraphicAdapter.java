/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.positioned;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 17, 2003
 * Time: 10:11:34 AM
 * Copyright (c) Apr 17, 2003 by Sam Reid
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
