/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

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
    private ModelViewTransform2D transform;
    private Color color;

    public ShapeGraphic2(HasModelShape hsm, ModelViewTransform2D transform, Color color, BasicStroke stroke) {
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
            public void transformChanged(ModelViewTransform2D ModelViewTransform2D) {
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
