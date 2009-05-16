package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DensityModel {
    private SwimmingPool swimmingPool = new SwimmingPool();
    private Block block1 = new Block("Block 1", 1);
    private Block block2 = new Block("Block 2", 2);
    private Sphere sphere = new Sphere();
    private Scale scale = new Scale();
    private double waterVolume = swimmingPool.getVolume() * 0.8;
    private Water water = new Water(swimmingPool, waterVolume, new WaterHeightMapper() {
        public double getWaterHeight(double waterVolume) {
            double proposedHeight = waterVolume / swimmingPool.getWidth() / swimmingPool.getDepth();
            for (int i = 0; i < 10; i++) {//use an iterative algorithm since I can't find any nice analytical solution
                proposedHeight = getEffectiveVolume(proposedHeight) / swimmingPool.getWidth() / swimmingPool.getDepth();
            }
            return proposedHeight;  //To change body of implemented methods use File | Settings | File Templates.
        }

        private double getEffectiveVolume(double proposedHeight) {
            if (block1 == null) {
                return waterVolume;
            } else {
                return waterVolume + block1.getSubmergedVolume(proposedHeight) + block2.getSubmergedVolume(proposedHeight);
            }
        }
    });

    public static interface WaterHeightMapper {
        double getWaterHeight(double waterVolume);
    }

    public DensityModel() {
        block2.translate(new Point2D.Double(5, -1));
        //as blocks go underwater, water level should rise
        block1.addListener(new RectangularObject.Listener() {
            public void modelChanged() {
                updateWaterHeight();
            }
        });
        block2.addListener(new RectangularObject.Listener() {
            public void modelChanged() {
                updateWaterHeight();
            }
        });
    }

    private void updateWaterHeight() {
        water.updateWaterHeight();
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
        Block(String name, double dim) {
            super(name, 2.7, 4, dim, dim, dim, new Color(123, 81, 237));
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

        //todo: generalize
        public double getIntersectingVolume(RectangularObject object) {
            double intersectHeight = getOverlapLengths(object.getY(), object.getY() + object.getHeight(),
                    this.getY(), this.getY() + this.getHeight());
            RectangularObject intersection = new RectangularObject("intersection", 0, 0,
                    Math.min(object.getWidth(), this.getWidth()), intersectHeight, Math.min(object.getDepth(), this.getDepth()), Color.black);
            final double volume = intersection.getVolume();
            return volume;
        }

        public double getSubmergedVolume(double proposedHeight) {
            return getWidth() * getDepth() * getHeightSubmerged(proposedHeight);
        }

        private double getHeightSubmerged(double waterLevelY) {
            if (waterLevelY < y) return 0;
            else if (waterLevelY > y + height) return height;
            else return waterLevelY - y;
        }

        static class Range {
            private double min;
            private double max;

            Range(double min, double max) {
                this.min = min;
                this.max = max;
            }

            public double getOverlap(Range b) {
                if (this.contains(b)) {
                    return b.range();
                } else if (b.contains(this)) {
                    return this.range();
                } else if (this.contains(b.min)) {
                    return new Range(b.min, this.max).range();
                } else if (this.contains(b.max)) {
                    return new Range(this.min, b.max).range();
                } else if (b.contains(this.min)) {
                    return new Range(this.min, b.max).range();
                } else if (b.contains(this.max)) {
                    return new Range(b.min, this.max).range();
                } else {
                    return 0;
                }
            }

            private double range() {
                return max - min;
            }

            private boolean contains(Range b) {
                return contains(b.min) && contains(b.max);
            }

            private boolean contains(double x) {
                return x >= min && x <= max;
            }
        }

        private double getOverlapLengths(double a1, double a2, double b1, double b2) {
            Range a = new Range(a1, a2);
            Range b = new Range(b1, b2);
            return a.getOverlap(b);
//            if (b1 > a1 && b1 < a2 && b2 > a1 && b2 < a2) {
//                return b2 - b1;
//            } else if (a1 > b1 && a1 < b2 && a2 > b1 && a2 < b2) {
//                return a2 - a1;
//            } else if ()
//            else{
//                return 0;
//            }
        }

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
        private WaterHeightMapper waterHeightMapper;

        public Water(SwimmingPool container, double waterVolume, WaterHeightMapper waterHeightMapper) {
            super("Water", container.getX(), container.getY(), container.getWidth(), 4, container.getDepth(), new Color(144, 207, 206, 128));
            this.container = container;
            this.waterVolume = waterVolume;
            this.waterHeightMapper = waterHeightMapper;
            updateWaterHeight();
        }

        private void updateWaterHeight() {
            setHeight(waterHeightMapper.getWaterHeight(waterVolume));
        }

        public double getWaterVolume() {
            return waterVolume;
        }

        public double getDistanceToTopOfPool() {
            return container.getHeight() - getHeight();
        }
    }
}
