/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/graphics/mousecontrols/TranslationEvent.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view.graphics.mousecontrols;

import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * TranslationEvent
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
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
