/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.dvm;

import edu.colorado.phet.cck.common.SimpleObservable;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 2:43:36 PM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class CableGraphic extends SimpleObservable implements Graphic {
    private ModelViewTransform2D transform;
    private Color color;
    private LeadGraphic leadGraphic;
    private HasLocation attachmentPoint;
    private CubicCurve2D.Double cableCurve;
    private double cx;
    private double cy;
    private double dx;
    private double dy;

    public CableGraphic(ModelViewTransform2D transform, Color color, LeadGraphic leadGraphic, HasLocation attachmentPoint) {
        this.transform = transform;
        this.color = color;
        this.leadGraphic = leadGraphic;
        this.attachmentPoint = attachmentPoint;
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2D ModelViewTransform2D) {
                changed();
            }
        });
        changed();
    }

    public void changed() {
        Point in = attachmentPoint.getLocation();
        Point2D out = leadGraphic.getInputPoint();
        dx = in.getX();
        dy = in.getY();
        cx = out.getX();
        cy = out.getY();
        float dcy = 100;
        cableCurve = new CubicCurve2D.Double(cx, cy, cx, cy + dcy, (2 * dx + cx) / 3, dy, dx, dy);
        updateObservers();
    }

    public void paint(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(4.0f));
        g.draw(cableCurve);
    }
}
