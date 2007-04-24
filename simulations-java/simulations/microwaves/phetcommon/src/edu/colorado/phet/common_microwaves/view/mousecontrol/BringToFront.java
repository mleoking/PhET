/*, 2003.*/
package edu.colorado.phet.common_microwaves.view.mousecontrol;

import edu.colorado.phet.common_microwaves.view.CompositeGraphic;
import edu.colorado.phet.common_microwaves.view.graphics.Graphic;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 1:07:02 AM
 *
 */
public class BringToFront implements MouseControl {
    AbstractShape shape;
    CompositeGraphic graphicTree;
    Graphic target;

    public BringToFront(AbstractShape shape, CompositeGraphic graphicTree, Graphic target) {
        this.shape = shape;
        this.graphicTree = graphicTree;
        this.target = target;
    }

    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc) {
        return shape.contains(modelLoc.x, modelLoc.y);
    }

    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mousePressed(MouseEvent event, Point2D.Double modelLoc) {
//        double layer=graphicTree.getLayer(target);
        graphicTree.moveToTop(target);
    }

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
    }
}
