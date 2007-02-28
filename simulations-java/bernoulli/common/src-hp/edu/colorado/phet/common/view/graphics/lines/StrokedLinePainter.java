/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.lines;

import java.awt.*;

public class StrokedLinePainter implements LinePainter {
    Stroke s;
    Color c;
    Color selectionColor;

    public StrokedLinePainter(Stroke s, Color c) {
        this.s = s;
        this.c = c;
    }

    public void drawLine(Graphics2D graphics2D, int x, int y, int x2, int y2) {
        graphics2D.setStroke(s);
        graphics2D.setColor(c);
        graphics2D.drawLine(x, y, x2, y2);
    }
}

