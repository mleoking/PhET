/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 4, 2003
 * Time: 12:05:58 AM
 * Copyright (c) Sep 4, 2003 by Sam Reid
 */
public class TextDisplay2WithBackground implements Graphic {
    String text;
    Point target;
    Point src;

    Font font;
    Color textColor;
    boolean visible = true;
    private Color border = Color.black;
    private Color background = new Color(210, 160, 240);

    public TextDisplay2WithBackground(String text, int x, int y) {
        this.text = text;
        font = new Font("Lucida Sans", Font.BOLD, 14);
        textColor = Color.black;
        setLocation(x, y);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLocation(int x, int y) {
        setLocation(new Point(x, y));
    }

    public void setLocation(Point src) {
        this.src = src;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void paint(Graphics2D g) {
        if (!visible)
            return;
        g.setFont(font);
        Rectangle2D bounds = font.getStringBounds(text, g.getFontRenderContext());
        double dw = 6;
        Rectangle2D.Double full = new Rectangle2D.Double(bounds.getX() - dw + src.x, bounds.getY() - dw + src.y, bounds.getWidth() + dw * 2, bounds.getHeight() + dw * 2);
        g.setColor(background);
        g.fill(full);
        g.setColor(border);
        g.setStroke(new BasicStroke(2F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(full);
        g.setColor(textColor);
        g.drawString(text, src.x, src.y);
    }
}
