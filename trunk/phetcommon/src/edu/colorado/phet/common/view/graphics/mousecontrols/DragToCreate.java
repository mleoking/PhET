package edu.colorado.phet.common.view.graphics.mousecontrols;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

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
    private CompositeGraphic target;
    private double layer;

    public DragToCreate( InteractiveGraphicCreator interactiveGraphicCreator, CompositeGraphic target, double layer ) {
        this.interactiveGraphicCreator = interactiveGraphicCreator;
        this.target = target;
        this.layer = layer;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
        InteractiveGraphic graphic = interactiveGraphicCreator.newInstance();
        target.addGraphic( graphic, layer );
        //        target.startDragging( graphic, e );
    }

    public void mouseMoved( MouseEvent e ) {
    }
}
