package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 18, 2009
 * Time: 8:51:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class RectangularObject {
    private String name;
    private double x;
    private double y;
    private double width;
    private double height;
    private double depth;
    private Color faceColor;

    RectangularObject(String name, double x, double y, double width, double height, double depth, Color faceColor) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.faceColor = faceColor;
    }

    public double getZ() {
        return 0;//all model elements are at z=0
    }

    public double getMaxX() {
        return x + width;
    }

    public Range getVerticalRange() {
        return new Range(y, y + height);
    }

    public Range getWidthRange() {
        return new Range(x, x + width);
    }

    public double getSubmergedVolume(double proposedHeight) {
        return getWidth() * getDepth() * getHeightSubmerged(proposedHeight);
    }

    private double getHeightSubmerged(double waterLevelY) {
        if (waterLevelY < y) return 0;
        else if (waterLevelY > y + height) return height;
        else return waterLevelY - y;
    }

    public double getMaxY() {
        return y + height;
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void translate(double dx, double dy) {
        translate(new Point2D.Double(dx, dy));
    }

    public void setHeight(double height) {
        this.height = height;
        notifyListeners();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVolume() {
        return width * height * depth;
    }

    public Shape getFrontFace() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public Color getFaceColor() {
        return faceColor;
    }

    public Shape getTopFace() {
        Point2D startPoint = new Point2D.Double(x, y + height);
        final DoubleGeneralPath path = new DoubleGeneralPath(startPoint);
        path.lineToRelative(width, 0);
        double viewAngle = Math.PI / 4;
        path.lineToRelative(depth * Math.cos(viewAngle), depth * Math.sin(viewAngle));
        path.lineToRelative(-width, 0);
        path.lineTo(startPoint);
        path.closePath();
        return path.getGeneralPath();
    }

    public Shape getRightFace() {
        Point2D startPoint = new Point2D.Double(x + width, y + height);
        final DoubleGeneralPath path = new DoubleGeneralPath(startPoint);
//            path.lineToRelative(width, 0);
        double viewAngle = Math.PI / 4;
        path.lineToRelative(depth * Math.cos(viewAngle), depth * Math.sin(viewAngle));
        path.lineToRelative(0, -height);
        path.lineToRelative(-depth * Math.cos(viewAngle), -depth * Math.sin(viewAngle));
        path.lineTo(startPoint);
        path.closePath();
        return path.getGeneralPath();
    }


    public Paint getTopFaceColor() {
        return new Color(faceColor.brighter().getRed(), faceColor.brighter().getGreen(), faceColor.brighter().getBlue(), faceColor.getAlpha());
    }

    public Paint getRightFaceColor() {
        return new Color(faceColor.darker().getRed(), faceColor.darker().getGreen(), faceColor.darker().getBlue(), faceColor.getAlpha());
    }

    public void translate(Point2D point2D) {
        x += point2D.getX();
        y += point2D.getY();
        notifyListeners();
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(x, y);
    }

    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public double getCenterX() {
        return x + width / 2;
    }

    public double getCenterY() {
        return y + height / 2;
    }

    public double getCenterZ() {
        return 0 - depth / 2;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDepth() {
        return depth;
    }

    public String getName() {
        return name;
    }

    public void setPosition2D(double x, double y) {
        this.x = x;
        this.y = y;
        notifyListeners();
    }

    public double getDistanceY(RectangularObject block) {
        return getVerticalRange().distanceTo(block.getVerticalRange());
    }

    public static interface Listener {

        void modelChanged();

        void blockRemoving();
    }

    public static class Adapter implements Listener {

        public void modelChanged() {
        }

        public void blockRemoving() {
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    protected void notifyListeners() {
        for (Listener listener : listeners) {
            listener.modelChanged();
        }
    }


    public void notifyRemoving() {
        for (Listener listener : listeners) {
            listener.blockRemoving();
        }
    }
}
