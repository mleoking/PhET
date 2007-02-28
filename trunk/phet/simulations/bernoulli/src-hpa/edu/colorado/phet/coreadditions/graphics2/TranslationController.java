/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics2;

import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.controllers.AbstractShape;
import edu.colorado.phet.coreadditions.controllers.MouseHandler;
import edu.colorado.phet.coreadditions.graphics2.Translatable;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 */
public class TranslationController implements MouseHandler {
    DifferentialDragHandler dragger;
    AbstractShape shape;
    private Translatable trans;
    private ModelViewTransform2d trf;

    public TranslationController(AbstractShape shape, Translatable trans, ModelViewTransform2d trf) {
        this.shape = shape;
        this.trans = trans;
        this.trf = trf;
    }

    public boolean canHandleMousePress(MouseEvent event) {
        return shape.containsPoint(event.getPoint());
    }

    public void mousePressed(MouseEvent event) {
        dragger = new DifferentialDragHandler(event.getPoint());
    }

    public void mouseDragged(MouseEvent event) {
        if (trf != null) {
            Point dx = dragger.getDifferentialLocationAndReset(event.getPoint());
            Point2D.Double modelDX = trf.viewToModelDifferential(dx);
            trans.translate(modelDX.x, modelDX.y);
        }
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void setTransform(ModelViewTransform2d transform) {
        this.trf = transform;
    }
}
