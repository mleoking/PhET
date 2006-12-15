/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ModelViewTransform2D;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 8, 2003
 * Time: 11:58:25 PM
 * Copyright (c) Oct 8, 2003 by Sam Reid
 */
public class TestImageGraphic implements InteractiveGraphic {
    BufferedImage image;
    private double x;
    private double y;
    private double sx;
    private double sy;

    public TestImageGraphic(BufferedImage image, double x,double y,double sx,double sy) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.sx = sx;
        this.sy = sy;
    }

    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc) {
        return false;
    }

    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mousePressed(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
    }

    public static AffineTransform toAffineTransform(Rectangle2D.Double viewBounds, Rectangle2D.Double modelBounds) {

        double sx = viewBounds.width / modelBounds.width;
        double sy = viewBounds.height / modelBounds.height;
        AffineTransform scaling = AffineTransform.getScaleInstance(sx, sy);

        double tx = (viewBounds.x - modelBounds.x * sx);
        double ty = (viewBounds.y - modelBounds.y * sy);
        AffineTransform translate = AffineTransform.getTranslateInstance(tx, ty);

//        AffineTransform flip=AffineTransform.getScaleInstance(1,-1);

        AffineTransform total = new AffineTransform(scaling);
        total.preConcatenate(translate);
//        total.concatenate(flip);
        return total;
    }


    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform at=AffineTransform.getTranslateInstance(x,y);
        at.scale(sx,sy);
        g.drawRenderedImage(image, at);
    }

}
