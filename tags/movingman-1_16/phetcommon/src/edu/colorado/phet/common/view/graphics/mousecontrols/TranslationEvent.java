/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics.mousecontrols;

import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * TranslationEvent
 *
 * @author ?
 * @version $Revision$
 */
public class TranslationEvent extends EventObject {
    private MouseEvent event;
    private int x;
    private int y;
    private int dx;
    private int dy;

    public TranslationEvent( Object source, MouseEvent event, int x, int y, int dx, int dy ) {
        super( source );
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
