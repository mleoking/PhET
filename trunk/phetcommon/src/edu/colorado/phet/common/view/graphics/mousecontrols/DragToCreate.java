package edu.colorado.phet.common.view.graphics.mousecontrols;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 9:47:46 PM
 * Copyright (c) Jan 27, 2004 by Sam Reid
 */
public class DragToCreate implements MouseInputListener {
    InteractiveGraphicCreator interactiveGraphicCreator;
    private CompositeInteractiveGraphic target;
    private double layer;
//    MouseManager targetManager;

    public DragToCreate(InteractiveGraphicCreator interactiveGraphicCreator, CompositeInteractiveGraphic target, double layer) {
        this.interactiveGraphicCreator = interactiveGraphicCreator;
        this.target = target;
//        this.targetManager = targetManager;
        this.layer = layer;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        InteractiveGraphic graphic = interactiveGraphicCreator.newInstance();
        target.addGraphic(graphic, layer);
        target.getMouseManager().startDragging(graphic, e);
//        targetManager.startDragging(graphic,e);
    }

    public void mouseMoved(MouseEvent e) {
    }
}
