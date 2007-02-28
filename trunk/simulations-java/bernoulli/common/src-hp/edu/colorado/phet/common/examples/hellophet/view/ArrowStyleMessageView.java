/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.view;

import edu.colorado.phet.common.examples.hellophet.model.Message;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: May 21, 2003
 * Time: 11:28:50 PM
 * Copyright (c) May 21, 2003 by Sam Reid
 */
public class ArrowStyleMessageView implements Observer, Graphic {
    Message m;
    private Point root;
    private Color c;
    private Stroke s;
    private int offset;
//    private Repainter repainter;

    int endx = Integer.MAX_VALUE;
    int endy;

    public ArrowStyleMessageView(Message m, Point root, Color c, Stroke s, int offset) {
        this.m = m;
        this.root = root;
        this.c = c;
        this.s = s;
        this.offset = offset;
        m.addObserver(this);
    }

    public void update(Observable o, Object arg) {
        int atx = (int) m.getX();
        int aty = (int) m.getY() + offset;
        int dx = atx - root.x;
        int dy = aty - root.y;
        this.endx = (int) (dx * .8 + root.x);
        this.endy = (int) (dy * .8 + root.y);
    }

    public void paint(Graphics2D g) {
        if (endx == Integer.MAX_VALUE)
            return;//haven't got an observe event yet.//TODO fix this.
        g.setColor(c);
        g.setStroke(s);
        g.drawLine(root.x, root.y, endx, endy);
    }
}
