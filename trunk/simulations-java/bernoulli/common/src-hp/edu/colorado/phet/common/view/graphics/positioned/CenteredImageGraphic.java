/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.positioned;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 6:23:33 PM
 * Copyright (c) Apr 16, 2003 by Sam Reid
 */
public class CenteredImageGraphic implements PositionedGraphic {
    BufferedImage bi;

    public CenteredImageGraphic(BufferedImage bi) {
        this.bi = bi;
    }

    public void paint(Graphics2D g, int x, int y) {
        AffineTransform at = AffineTransform.getTranslateInstance(x - bi.getWidth() / 2, y - bi.getHeight() / 2);
        g.drawRenderedImage(bi, at);
    }

    public Rectangle getRectangle(int x, int y) {
        Rectangle r = new Rectangle(x - bi.getWidth() / 2, y - bi.getHeight() / 2, bi.getWidth(), bi.getHeight());
        return r;
    }

}
