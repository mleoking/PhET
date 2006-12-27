/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics2;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.controllers.AbstractShape;
//import edu.colorado.phet.ec2.common.controllers.AbstractShape;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 16, 2003
 * Time: 11:16:30 PM
 * Copyright (c) Sep 16, 2003 by Sam Reid
 */
public class RectangleGraphic implements AbstractShape, Graphic {
    int x;
    int y;
    int width;
    int height;
    boolean init = false;
    private Color color;

    public RectangleGraphic(Color color) {
        this.color = color;
    }

    public void setState(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        init = true;
    }

    public boolean containsPoint(Point pt) {
        return new Rectangle(x, y, width, height).contains(pt);
    }

    public void paint(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
