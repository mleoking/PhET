/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.controllers;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Sep 16, 2003
 * Time: 10:11:27 PM
 * Copyright (c) Sep 16, 2003 by Sam Reid
 */
public class DefaultControlGraphic implements InteractiveGraphic {
    private Graphic graphic;
    MouseHandler controller;

    public DefaultControlGraphic(Graphic graphic, MouseHandler controller) {
        this.graphic = graphic;
        this.controller = controller;
    }

    public boolean canHandleMousePress(MouseEvent event) {
        return controller.canHandleMousePress(event);
    }

    public void mousePressed(MouseEvent event) {
        controller.mousePressed(event);
    }

    public void mouseDragged(MouseEvent event) {
        controller.mouseDragged(event);
    }

    public void mouseReleased(MouseEvent event) {
        controller.mouseReleased(event);
    }

    public void mouseEntered(MouseEvent event) {
        controller.mouseEntered(event);
    }

    public void mouseExited(MouseEvent event) {
        controller.mouseExited(event);
    }

    public void paint(Graphics2D g) {
        graphic.paint(g);
    }

}
