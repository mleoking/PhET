package edu.colorado.phet.coreadditions.graphics.transform;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Represents a rectangular mapping from model coordinates to view coordinates.
 */
public class ModelViewTransform2d {
    private Rectangle2D.Double modelBounds;
    private Rectangle viewBounds;
    private CompositeTransformListener listeners = new CompositeTransformListener();

    /**
     * Constructs a ModelViewTransform2d2 with model rectangle {0,0,1,1}.
     * @param viewBounds
     */
    public ModelViewTransform2d(Rectangle viewBounds) {
        this(new Rectangle2D.Double(0, 0, 1, 1), viewBounds);
    }

    /**
     * Constructs a transform from the specified model bounds to view bounds.
     * @param modelBounds
     * @param viewBounds
     */
    public ModelViewTransform2d(Rectangle2D.Double modelBounds, Rectangle viewBounds) {
        this.modelBounds = modelBounds;
        this.viewBounds = viewBounds;
        if (viewBounds.getWidth() <= 0)
            throw new RuntimeException("View Bounds width must be positive.");
        if (viewBounds.getHeight() <= 0)
            throw new RuntimeException("View Bounds height must be positive.");
    }

    public void addTransformListener(TransformListener tl) {
        listeners.addTransformListener(tl);
    }

    public void removeTransformListener(TransformListener tl) {
        listeners.removeTransformListener(tl);
    }

    /**Transforms the model coordinate to the corresponding view coordinate.*/
    public Point modelToView(double x, double y) {
        return new Point(modelToViewX(x), modelToViewY(y));
    }

    public Point modelToView(Point2D.Double pt) {
        return modelToView(pt.x, pt.y);
    }

    public Point modelToView(PhetVector point) {
        return modelToView(point.getX(), point.getY());
    }

    public Point modelToViewDifferential(PhetVector dx) {
        return modelToViewDifferential(dx.getX(), dx.getY());
    }

    public Point modelToViewDifferential(Point2D.Double point) {
        return modelToViewDifferential(point.x, point.y);
    }

    public int modelToViewX(double x) {
        double m = viewBounds.width / modelBounds.width;
        int out = (int) (m * (x - modelBounds.x) + viewBounds.x);
        return out;
    }

    public int modelToViewY(double y) {
        double m = -viewBounds.height / modelBounds.height;
        int out = (int) (m * (y - modelBounds.y - modelBounds.height) + viewBounds.y);
        return out;
    }

    /**Creates a new AffineTransform that corresponds to this transformation.
     *
     * @return a new AffineTransform that corresponds to this transformation.
     */
    public AffineTransform toAffineTransform() {
        double m00 = viewBounds.width / modelBounds.width;
        double m01 = 0;
        double m02 = viewBounds.x - m00 * modelBounds.x;
        double m10 = 0;
        double m11 = -viewBounds.height / modelBounds.height;
        double m12 = viewBounds.y + viewBounds.height / modelBounds.height * (modelBounds.y + modelBounds.height);
        return new AffineTransform(m00, m10, m01, m11, m02, m12);
    }

    public Point2D.Double viewToModel(int x, int y) {
        return new Point2D.Double(viewToModelX(x), viewToModelY(y));
    }

    public Point2D.Double viewToModel(Point pt) {
        return viewToModel(pt.x, pt.y);
    }

    public double viewToModelY(int y) {
        double m = -viewBounds.height / modelBounds.height;
        double out = (y - viewBounds.y) / m + modelBounds.height + modelBounds.y;
        return out;
    }

    public double viewToModelX(double x) {
        double m = modelBounds.width / viewBounds.width;
        return m * (x - viewBounds.x) + modelBounds.x;
    }

    public Rectangle2D.Double getModelBounds() {
        return modelBounds;
    }

    public void setModelBounds(Rectangle2D.Double modelBounds) {
        this.modelBounds = modelBounds;
        listeners.transformChanged(this);
    }

    public void setViewBounds(Rectangle viewBounds) {
        if (viewBounds.getWidth() <= 0)
            throw new RuntimeException("View Bounds width must be positive.");
        if (viewBounds.getHeight() <= 0)
            throw new RuntimeException("View Bounds height must be positive.");
        this.viewBounds = viewBounds;
        listeners.transformChanged(this);
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }

    public int modelToViewDifferentialY(double dy) {
        double m = -viewBounds.height / modelBounds.height;
        return (int) (m * dy);
    }

    public int modelToViewDifferentialX(double dx) {
        double m = viewBounds.height / modelBounds.height;
        return (int) (m * dx);
    }

    public double viewToModelDifferentialY(double dy) {
        double m = -modelBounds.height / viewBounds.height;
        return m * dy;
    }

    public double viewToModelDifferentialX(double dx) {
        double m = modelBounds.width / viewBounds.width;
        return m * dx;
    }

    public Point2D.Double viewToModelDifferential(Point rel) {
        return viewToModelDifferential(rel.x, rel.y);
    }

    public Point2D.Double viewToModelDifferential(int dx, int dy) {
        return new Point2D.Double(viewToModelDifferentialX(dx), viewToModelDifferentialY(dy));
    }

    public Point modelToViewDifferential(double dx, double dy) {
        return new Point(modelToViewDifferentialX(dx), modelToViewDifferentialY(dy));
    }

    /**Converts a model rectangle to the corresponding view rectangle.*/
    public Rectangle modelToView(Rectangle2D.Double modelRect) {
        Point cornerA = modelToView(modelRect.x, modelRect.y);
        Point cornerB = modelToView(modelRect.x + modelRect.width, modelRect.y + modelRect.height);
        Rectangle out = new Rectangle(cornerA.x, cornerA.y, 0, 0);
        out.add(cornerB);
        return out;
    }

    public static void main(String[] args) {
        Rectangle2D.Double input = new Rectangle2D.Double(0, 0, 1, 1);
        Rectangle output = new Rectangle(0, 0, 100, 100);
        ModelViewTransform2d test = new ModelViewTransform2d(input, output);
        for (double x = 0; x < 1; x += .1) {
            Point2D.Double model = new Point2D.Double(x, x);
            Point out = test.modelToView(model.x, model.y);
//            O.d("in="+model",""out="+out);
            AffineTransform at = test.toAffineTransform();
            Point2D trfed = at.transform(model, null);
//            O.d("Model=" + model + ", view=" + out + ", trfed=" + trfed);
            Point2D.Double model2 = test.viewToModel(out.x, out.y);
//            O.d("View=" + out + ", model2=" + model2 + "\n\n");
        }
    }

}



