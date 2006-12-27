/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics.positioned;

import edu.colorado.phet.common.view.graphics.positioned.PositionedGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 3, 2003
 * Time: 3:54:09 PM
 * Copyright (c) Jun 3, 2003 by Sam Reid
 */
public class PositionedRectangleGraphic implements PositionedGraphic {
    Color color;
    int width;
    int height;

    public PositionedRectangleGraphic(Color color, int width, int height) {
        this.color = color;
        this.width = width;
        this.height = height;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void paint(Graphics2D g, int x, int y) {
        g.setColor(color);
        int xat = x - width / 2;
        int yat = y - height / 2;
        g.fillRect(xat, yat, width, height);
    }

    public Shape getShape(int x, int y) {
        int xat = x - width / 2;
        int yat = y - height / 2;
        Rectangle r = new Rectangle(xat, yat, width, height);
        return r;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
