package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DensityModel {
    private SwimmingPool swimmingPool = new SwimmingPool();
    private Block block1 = new Block("Block 1");
    private Block block2 = new Block("Block 2");
    private Sphere sphere = new Sphere();
    private Scale scale = new Scale();
    private Water water = new Water(swimmingPool, swimmingPool.getVolume() * 0.8);

    public DensityModel() {
        block2.translate(new Point2D.Double(5, -1));
        //as blocks go underwater, water level should rise
        block1.addListener(new RectangularObject.Listener() {
            public void modelChanged() {
                updateWaterDepth();
            }
        });
        block2.addListener(new RectangularObject.Listener() {
            public void modelChanged() {
                updateWaterDepth();
            }
        });
    }

    private void updateWaterDepth() {
        double waterVolume = water.getWaterVolume();
    }

    public SwimmingPool getSwimmingPool() {
        return swimmingPool;
    }

    public Water getWater() {
        return water;
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

    public Block getBlock2() {
        return block2;
    }

    static class Block extends RectangularObject {
        Block(String name) {
            super(name, 2.7, 4, 1, 1, 1, new Color(123, 81, 237));
        }
    }

    static class Sphere {

    }

    static class Scale {

    }

    public static class RectangularObject {
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

    public static class SwimmingPool extends RectangularObject {
        public SwimmingPool() {
            super("Pool", 0, 0, 10, 5, 5, new Color(255, 255, 255));
        }

    }

    public static class Water extends RectangularObject {
        private SwimmingPool container;
        private double waterVolume;

        public Water(SwimmingPool container, double waterVolume) {
            super("Water", container.getX(), container.getY(), container.getWidth(), 4, container.getDepth(), new Color(144, 207, 206, 128));
            this.container = container;
            this.waterVolume = waterVolume;
            updateWaterHeight();
        }

        private void updateWaterHeight() {
            setHeight(waterVolume / getWidth() / getHeight());
        }

        public double getWaterVolume() {
            return waterVolume;
        }

        public double getDistanceToTopOfPool() {
            return container.getHeight() - getHeight();
        }
    }
}
