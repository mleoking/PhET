/** University of Colorado, PhET*/
package edu.colorado.phet.common.view.graphics.mousecontrols;

import java.awt.event.MouseEvent;

/**
 * User: University of Colorado, PhET
 * Date: Nov 15, 2004
 * Time: 5:31:04 PM
 * Copyright (c) Nov 15, 2004 by University of Colorado, PhET
 */
public class TranslationEvent {
    private MouseEvent event;
    private int x;
    private int y;
    private int dx;
    private int dy;

    public TranslationEvent( MouseEvent event, int x, int y, int dx, int dy ) {
        this.event = event;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public MouseEvent getMouseEvent() {
        return event;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}
