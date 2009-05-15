package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 15, 2009
 * Time: 9:07:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class DensityModel {
    private SwimmingPool swimmingPool = new SwimmingPool();
    private Block block1 = new Block();
    private Sphere sphere = new Sphere();
    private Scale scale = new Scale();

    public DensityModel() {
    }

    public SwimmingPool getSwimmingPool() {
        return swimmingPool;
    }

    public Block getBlock1() {
        return block1;
    }

    public Sphere getSphere() {
        return sphere;
    }

    public Scale getScale() {
        return scale;
    }

    static class Block extends RectangularObject {
        Block() {
            super(2.7, 2.6, 2, 2, 2, new Color(123, 81, 237));
        }
    }

    static class Sphere {

    }

    static class Scale {

    }

    public static class RectangularObject {
        private double x;
        private double y;
        private double width;
        private double height;
        private double depth;
        private Color faceColor;

        RectangularObject(double x, double y, double width, double height, double depth, Color faceColor) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.faceColor = faceColor;
        }

        public Shape getFrontFace() {
            return new Rectangle2D.Double(x, y, width, height);
        }

        public Paint getFaceColor() {
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

        private ArrayList<Listener> listeners = new ArrayList<Listener>();

        public static interface Listener {

            void modelChanged();
        }

        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        private void notifyListeners() {
            for (Listener listener : listeners) {
                listener.modelChanged();
            }
        }
    }

    static class SwimmingPool extends RectangularObject {
        public SwimmingPool() {
            super(0, 0, 10, 4, 3, new Color(144, 207, 206, 128));
        }
    }
}
