/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 3:15:26 PM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class SegmentGraphic implements Graphic, TransformListener {
    private ModelViewTransform2D transform;
    double x;
    double y;
    double x2;
    double y2;
    private Color color;
    private Stroke stroke;
    Point start;
    Point end;
    private Shape shape;
    private boolean shapeValid = false;
    private boolean selected;
    private Stroke highlightStroke;
    private Color highlightColor;

    public SegmentGraphic(ModelViewTransform2D transform, double x, double y, double x2, double y2, Color color, Stroke stroke, Stroke highlightStroke, Color highlightColor) {
        this.transform = transform;
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.stroke = stroke;
        this.highlightStroke = highlightStroke;
        this.highlightColor = highlightColor;
        transform.addTransformListener(this);
        update();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Shape getShape() {
        if (shapeValid)
            return shape;
        else {
            validateShape();
            return shape;
        }
    }

    private void validateShape() {
        this.shape = stroke.createStrokedShape(new Line2D.Double(start.x, start.y, end.x, end.y));
        shapeValid = true;
    }

    private void update() {
        start = transform.modelToView(x, y);
        end = transform.modelToView(x2, y2);
//        this.selected=
    }

    public void paint(Graphics2D g) {

        if (start != null) {
            if (selected) {
                g.setColor(highlightColor);
                g.setStroke(highlightStroke);
                g.drawLine(start.x, start.y, end.x, end.y);
            }
            g.setColor(color);
            g.setStroke(stroke);
            g.drawLine(start.x, start.y, end.x, end.y);
//            g.setColor(Color.blue);
//            g.fillRect(start.x, start.y, 20, 20);
//            g.fillRect(end.x, end.y, 20, 20);
        }
    }

    public void transformChanged(ModelViewTransform2D mvt) {
        update();
    }

    public void setState(double x, double y, double x2, double y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        shapeValid = false;
        update();
    }

    public Point getStartPoint() {
        return start;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
