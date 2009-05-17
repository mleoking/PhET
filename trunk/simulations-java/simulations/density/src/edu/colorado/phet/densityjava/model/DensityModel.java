package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DensityModel {
    private SwimmingPool swimmingPool = new SwimmingPool();

    private Sphere sphere = new Sphere();
    private double waterVolume = swimmingPool.getVolume() * 0.8;
    private ArrayList<Block> blocks = new ArrayList<Block>();
    private ArrayList<Scale> scales = new ArrayList<Scale>();

    private Water water = new Water(swimmingPool, waterVolume, new WaterHeightMapper() {
        public double getWaterHeight(double waterVolume) {
            double proposedHeight = waterVolume / swimmingPool.getWidth() / swimmingPool.getDepth();
            for (int i = 0; i < 10; i++) {//use an iterative algorithm since I can't find any nice analytical solution
                proposedHeight = getEffectiveVolume(proposedHeight) / swimmingPool.getWidth() / swimmingPool.getDepth();
            }
            return proposedHeight;  //To change body of implemented methods use File | Settings | File Templates.
        }

        private double getEffectiveVolume(double proposedHeight) {
            double subVol = 0.0;
            for (Block block : blocks) subVol += block.getSubmergedVolume(proposedHeight);
            return waterVolume + subVol;
        }
    });

    public DensityModel() {
        setFourBlocksSameVolume();
        //as blocks go underwater, water level should rise
        water.updateWaterHeight();
    }

    public int getScaleCount() {
        return scales.size();
    }

    public Scale getScale(int i) {
        return scales.get(i);
    }

    public static interface Listener {
        void blockAdded(Block block);

        void scaleAdded(Scale scale);
    }

    public static class Adapter implements Listener {

        public void blockAdded(Block block) {
        }

        public void scaleAdded(Scale scale) {
        }
    }

    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void stepInTime(double simulationTimeChange) {
        //update the blocks and the forces on the blocks
        for (Block block : blocks) {
            block.stepInTime(simulationTimeChange);
        }
    }

    double floatHeight = 5;

    private BlockEnvironment blockEnvironment = new BlockEnvironment() {
        public double getFloorY(Block block) {
            ArrayList<Block> targets = new ArrayList<Block>();
            //if there's a block with a center of mass below this block, and their width ranges overlap, then that's the bottom
            for (Block target : blocks) {
                if (target != block) {
                    if (target.getWidthRange().intersects(block.getWidthRange())) {//within x range
                        if (target.getCenterY() < block.getCenterY()) {//below
                            targets.add(target);
                        }
                    }
                }
            }
            //Choose the highest block below this block
            if (targets.size()>0){
                Collections.sort(targets,new Comparator<Block>() {
                    public int compare(Block b1, Block b2) {
                        return Double.compare(b1.getMaxY(), b2.getMaxY());
                    }
                });
                return targets.get(targets.size()-1).getMaxY();
            }


            if (water.getWidthRange().contains(block.getWidthRange()))
                return water.getBottomY();
            else
                return water.getSwimmingPoolSurfaceY();
        }
    };

    public void setFourBlocksSameMass() {
        clearBlocks();
        clearScales();
        double sameMass = 7;
        addBlock(new Block("Block 1", 1, water, -1, swimmingPool.getMaxY() + floatHeight, Color.red, sameMass, blockEnvironment));
        addBlock(new Block("Block 2", 1.5, water, 1, swimmingPool.getMaxY() + floatHeight, Color.green, sameMass, blockEnvironment));
        addBlock(new Block("Block 3", 2, water, 4, swimmingPool.getMaxY() + floatHeight, Color.blue, sameMass, blockEnvironment));
        addBlock(new Block("Block 4", 2.5, water, 7, swimmingPool.getMaxY() + floatHeight, Color.yellow, sameMass, blockEnvironment));

        addScale(new Scale("Scale 1", -1, swimmingPool.getMaxY(), 1, 1, 1));
        addScale(new Scale("Scale 1", 1, swimmingPool.getMaxY(), 1, 1, 1));
    }

    private void addScale(Scale scale) {
        scales.add(scale);
        notifyScaleAdded(scale);
    }

    private void notifyScaleAdded(Scale scale) {
        for (Listener listener : listeners) {
            listener.scaleAdded(scale);
        }
    }

    public void setFourBlocksSameVolume() {
        clearBlocks();
        clearScales();
        double sameVolume = 1;
        addBlock(new Block("Block 1", sameVolume, water, -1, swimmingPool.getMaxY() + floatHeight, Color.red, 3, blockEnvironment));
        addBlock(new Block("Block 2", sameVolume, water, 1, swimmingPool.getMaxY() + floatHeight, Color.green, 17, blockEnvironment));
        addBlock(new Block("Block 3", sameVolume, water, 4, swimmingPool.getMaxY() + floatHeight, Color.blue, 2, blockEnvironment));
        addBlock(new Block("Block 4", sameVolume, water, 7, swimmingPool.getMaxY() + floatHeight, Color.yellow, 1.5, blockEnvironment));

        addScale(new Scale("Scale 1", -1, swimmingPool.getMaxY(), 1, 1, 1));
        addScale(new Scale("Scale 1", 1, swimmingPool.getMaxY(), 1, 1, 1));
    }

    private void addBlock(Block block) {
        blocks.add(block);
        block.addListener(listener);
        notifyBlockAdded(block);
    }

    private void notifyBlockAdded(Block block) {
        for (Listener listener : listeners) {
            listener.blockAdded(block);
        }
    }

    private void clearBlocks() {
        while (blocks.size() > 0) {
            removeBlock(blocks.get(0));
        }
    }

    private void clearScales() {
        while (scales.size() > 0) {
            removeScale(scales.get(0));
        }
    }

    private void removeScale(Scale scale) {
        scale.removeListener(listener);
        scale.notifyRemoving();
        scales.remove(scale);
    }

    private void removeBlock(Block block) {
        block.removeListener(listener);
        block.notifyRemoving();
        blocks.remove(block);
    }

    public int getBlockCount() {
        return blocks.size();
    }

    public Block getBlock(int i) {
        return blocks.get(i);
    }

    public static interface WaterHeightMapper {
        double getWaterHeight(double waterVolume);
    }

    final RectangularObject.Listener listener = new RectangularObject.Adapter() {
        public void modelChanged() {
            updateWaterHeight();
        }
    };

    private void updateWaterHeight() {
        water.updateWaterHeight();
    }

    public SwimmingPool getSwimmingPool() {
        return swimmingPool;
    }

    public Water getWater() {
        return water;
    }

    public Sphere getSphere() {
        return sphere;
    }

    static interface BlockEnvironment {
        double getFloorY(Block block);
    }

    public static class Block extends RectangularObject {
        private double mass;
        private BlockEnvironment blockEnvironment;
        private double velocity = 0;
        private Water water;
        private boolean dragging = false;

        Block(String name, double dim, Water water, double x, double y, Color color, double mass, BlockEnvironment blockEnvironment) {
            super(name, x, y, dim, dim, dim, color);
            this.water = water;
            this.mass = mass;
            this.blockEnvironment = blockEnvironment;
        }

        public void stepInTime(double simulationTimeChange) {
            if (!dragging) {
                double force = getGravityForce() + getBuoyancyForce() + getNormalForce();
                double accel = force / mass;
                velocity += accel * simulationTimeChange;
                setPosition2D(getX(), getY() + velocity * simulationTimeChange);
                if (getY() <= getFloorY()) {
                    setPosition2D(getX(), getFloorY());
                    velocity = 0;
                }
            }
        }

        //the bottom of the pool if over the pool, otherwise the ground level
        //todo: generalize for block stacking
        private double getFloorY() {
            return blockEnvironment.getFloorY(this);
        }

        private double getNormalForce() {
            if (getY() <= getFloorY() && getGravityForce() + getBuoyancyForce() < 0) {
                return -getGravityForce() - getBuoyancyForce();
            } else return 0;
        }


        public void setVelocity(double v) {
            this.velocity = v;
            notifyListeners();
        }

        //According to Archimedes' principle,
        // "Any object, wholly or partly immersed in a fluid,
        // is buoyed up by a force equal to the weight of the fluid displaced by the object."
        private double getBuoyancyForce() {
            double volumeDisplaced = getSubmergedVolume(water.getHeight());
            double massDisplaced = water.getDensity() * volumeDisplaced / 500;
            double weightDisplaced = massDisplaced * 9.8;
            return weightDisplaced;
        }

        private double getGravityForce() {
            return -9.8 * mass;
        }

        public void setDragging(boolean b) {
            dragging = b;
        }

    }

    static class Sphere {

    }

    public static class Scale {
        private String name;
        private ScaleSurface surface;
        private ScaleBody body;
        private ArrayList<RectangularObject.Listener> listeners = new ArrayList<RectangularObject.Listener>();

        public Scale(String name, double x, double y, double width, double height, double depth) {
            this.name = name;
            surface = new ScaleSurface(x, y, width, height, depth, Color.white);
            body = new ScaleBody(x, y, width, height, depth, Color.gray);
        }

        public void removeListener(RectangularObject.Listener listener) {
            listeners.remove(listener);
        }

        public void notifyRemoving() {
        }

        public void addListener(RectangularObject.Listener listener) {
            listeners.add(listener);
        }

        public double getX() {
            return body.getX();
        }

        public double getY() {
            return body.getY();
        }

        class ScaleSurface extends RectangularObject {
            ScaleSurface(double x, double y, double width, double height, double depth, Color faceColor) {
                super(name + ".top", x, y, width, height, depth, faceColor);
            }
        }

        class ScaleBody extends RectangularObject {
            ScaleBody(double x, double y, double width, double height, double depth, Color faceColor) {
                super(name + ".body", x, y, width, height, depth, faceColor);
            }
        }
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

        //todo: generalize
        public double getIntersectingVolume(RectangularObject object) {
            double intersectHeight = getOverlapLengths(object.getY(), object.getY() + object.getHeight(),
                    this.getY(), this.getY() + this.getHeight());
            RectangularObject intersection = new RectangularObject("intersection", 0, 0,
                    Math.min(object.getWidth(), this.getWidth()), intersectHeight, Math.min(object.getDepth(), this.getDepth()), Color.black);
            final double volume = intersection.getVolume();
            return volume;
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

            public boolean intersects(Range r) {
                //ranges intersect if either contains any end point of the other.
                return contains(r.min) || contains(r.max) || r.contains(min) || r.contains(max);
            }
        }

        private double getOverlapLengths(double a1, double a2, double b1, double b2) {
            Range a = new Range(a1, a2);
            Range b = new Range(b1, b2);
            return a.getOverlap(b);
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

        public double getDensity() {
            return 1000;
        }

        public double getBottomY() {
            return container.getY();
        }

        public double getSwimmingPoolSurfaceY() {
            return container.getY() + container.getHeight();
        }

    }
}
