/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 17, 2003
 * Time: 11:02:04 AM
 * Copyright (c) Dec 17, 2003 by Sam Reid
 */
public class ShapeGraphic2 implements Graphic {
    Shape shape;
    HasModelShape hsm;
    private BasicStroke stroke;
    private ModelViewTransform2d transform;
    private Color color;

    public ShapeGraphic2(HasModelShape hsm, ModelViewTransform2d transform, Color color, BasicStroke stroke) {
        this.hsm = hsm;
        this.stroke = stroke;
        this.shape = hsm.getShape();
        this.transform = transform;
        this.color = color;
        hsm.addObserver(new SimpleObserver() {
            public void update() {
                doupdate();
            }
        });
        doupdate();
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2d modelViewTransform2d) {
                doupdate();
            }
        });
    }

    private void doupdate() {
        Shape s = hsm.getShape();
        shape = transform.toAffineTransform().createTransformedShape(s);
    }

    public void paint(Graphics2D g) {

        g.setColor(color);
        g.fill(shape);

        g.setStroke(stroke);
        g.setColor(Color.black);
        g.draw(shape);
    }
}
