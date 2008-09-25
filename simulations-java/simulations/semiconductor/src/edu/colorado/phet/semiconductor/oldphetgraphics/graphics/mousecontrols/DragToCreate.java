package edu.colorado.phet.semiconductor.oldphetgraphics.graphics.mousecontrols;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import edu.colorado.phet.semiconductor.phetcommon.view.CompositeInteractiveGraphic;
import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.InteractiveGraphic;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 9:47:46 PM
 */
public class DragToCreate implements MouseInputListener {
    InteractiveGraphicCreator interactiveGraphicCreator;
    private CompositeInteractiveGraphic target;
    private double layer;

    public DragToCreate( InteractiveGraphicCreator interactiveGraphicCreator, CompositeInteractiveGraphic target, double layer ) {
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
        target.getMouseManager().startDragging( graphic, e );
    }

    public void mouseMoved( MouseEvent e ) {
    }
}
