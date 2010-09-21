package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.densityjava.ModelComponents;

import java.awt.*;
import java.text.DecimalFormat;
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
    private ModelComponents.Units units = new ModelComponents.Units();

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

    public MatrixDynamics.ContactGroup[] getContactGroups(ObjectElement[] elements) {
        ArrayList<MatrixDynamics.ContactGroup> list = new ArrayList<MatrixDynamics.ContactGroup>();
        for (int i = 0; i < elements.length; i++) {
            ObjectElement element = elements[i];
            for (int k = 0; k < elements.length; k++) {
                ObjectElement elm2 = elements[k];
                if (i != k && inContact(element.getObject(), elm2.getObject())) {
                    list.add(new MatrixDynamics.ContactGroup(new MatrixDynamics.Element[]{element, elm2}));//todo: adds duplicates
                }
            }
        }
        return list.toArray(new MatrixDynamics.ContactGroup[list.size()]);
    }

    private boolean inContact(RectangularObject a, RectangularObject b) {
        if (getLowestObjectAbove(a) == b && a.getDistanceY(b) < 1E-5) {
            return true;
        } else if (getHighestObjectBelow(a) == b && a.getDistanceY(b) < 1E-5)
            return true;
        else
            return false;
    }

    public ModelComponents.Units getUnits() {
        return units;
    }

    public String getWaterVolumeHTML() {
        return "<html>" + new DecimalFormat("0.00").format(waterVolume) + " m<font size=\"-1\"><sup>3</sup></font></html>";
    }

    public String getWaterHeightText() {
        return new DecimalFormat("0.00").format(water.getHeight())+" m";
    }

    public class ObjectElement extends MatrixDynamics.Element {
        private RectangularObject block;

        public ObjectElement(Block block, boolean atRest) {
            super(block.getName(), block.getGravityForce() + block.getBuoyancyForce(), atRest);
            this.block = block;
        }

        public RectangularObject getObject() {
            return block;
        }
    }

    private boolean inContactWithAnything(RectangularObject block) {
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            if (b != block && inContact(b, block)) {
                return true;
            }
        }
        for (int i = 0; i < scales.size(); i++) {
            Block b = scales.get(i).surface;
            if (b != block && inContact(b, block))
                return true;
        }
        return false;
    }

    public ObjectElement[] getElements() {
        ArrayList<ObjectElement> list = new ArrayList<ObjectElement>();
        for (Block block : blocks) list.add(new ObjectElement(block, inContactWithAnything(block)));
        for (Scale scale : scales) list.add(new ObjectElement(scale.surface, false));
        return list.toArray(new ObjectElement[list.size()]);
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
        for (Scale scale : scales) {
            scale.stepInTime(simulationTimeChange);
        }
    }

    //    private BlockEnvironment blockEnvironment = new DensityModelBlockEnvironment(this);
    private BlockEnvironment blockEnvironment = new MatrixDynamicsBlockEnvironment(this);

    public RectangularObject getLowestObjectAbove(RectangularObject block) {
        return getNeighbor(block, new SearchStrategy.Above());
    }

    public RectangularObject getHighestObjectBelow(RectangularObject block) {
        return getNeighbor(block, new SearchStrategy.Below());
    }

    static interface SearchStrategy {
        boolean isSatisfied(double queryCenterY, double blockCenterY);

        int compare(RectangularObject b1, RectangularObject b2);

        static class Above implements SearchStrategy {
            public boolean isSatisfied(double queryCenterY, double blockCenterY) {
                return queryCenterY > blockCenterY;
            }

            public int compare(RectangularObject b1, RectangularObject b2) {
                return -Double.compare(b1.getMaxY(), b2.getMaxY());
            }
        }

        static class Below implements SearchStrategy {

            public boolean isSatisfied(double queryCenterY, double blockCenterY) {
                return queryCenterY < blockCenterY;
            }

            public int compare(RectangularObject b1, RectangularObject b2) {
                return Double.compare(b1.getMaxY(), b2.getMaxY());
            }
        }
    }

    private RectangularObject getNeighbor(RectangularObject block, final SearchStrategy searchStrategy) {
        ArrayList<RectangularObject> targets = new ArrayList<RectangularObject>();
        //if there's a block with a center of mass below this block, and their width ranges overlap, then that's the bottom
        ArrayList<RectangularObject> potentialTargets = new ArrayList<RectangularObject>();
        potentialTargets.addAll(blocks);
        for (Scale s : scales) {
            potentialTargets.add(s.surface);//todo: should Scale be rectangularobject or use common interface?
        }
        for (RectangularObject target : potentialTargets) {
            if (target != block) {
                if (target.getWidthRange().intersects(block.getWidthRange())) {//within x range
                    if (searchStrategy.isSatisfied(target.getCenterY(), block.getCenterY())) {//below
                        targets.add(target);
                    }
                }
            }
        }
        //Choose the highest block below this block
        if (targets.size() > 0) {
            Collections.sort(targets, new Comparator<RectangularObject>() {
                public int compare(RectangularObject b1, RectangularObject b2) {
                    return searchStrategy.compare(b1, b2);
                }
            });
            return targets.get(targets.size() - 1);
        } else {
            return null;
        }
    }

    private ScaleEnvironment scaleEnvironment = new ScaleEnvironment() {
        public double getNormalForce(Scale scale) {
            return -blockEnvironment.getAppliedForce(scale.surface);
        }
    };

    double floatHeight = 5;

    public void setFourBlocksSameMass() {
        clearBlocks();
        clearScales();
        double sameMass = 7;
        addBlock(new Block("Block 1", 1, water, -1, swimmingPool.getMaxY() + floatHeight, Color.red, sameMass, blockEnvironment));
        addBlock(new Block("Block 2", 1.5, water, 1, swimmingPool.getMaxY() + floatHeight, Color.green, sameMass, blockEnvironment));
        addBlock(new Block("Block 3", 2, water, 4, swimmingPool.getMaxY() + floatHeight, Color.blue, sameMass, blockEnvironment));
        addBlock(new Block("Block 4", 2.5, water, 7, swimmingPool.getMaxY() + floatHeight, Color.yellow, sameMass, blockEnvironment));

        addScale(new Scale("Scale 1", -1, swimmingPool.getMaxY(), 1, 1, 1, scaleEnvironment, water, blockEnvironment));
        addScale(new Scale("Scale 1", 1, swimmingPool.getY(), 1, 1, 1, scaleEnvironment, water, blockEnvironment));
    }

    public void setFourBlocksSameVolume() {
        clearBlocks();
        clearScales();
        double sameVolume = 1;
        addBlock(new Block("Block 1", sameVolume, water, -1, swimmingPool.getMaxY() + floatHeight, Color.red, 3, blockEnvironment));
        addBlock(new Block("Block 2", sameVolume, water, 1, swimmingPool.getMaxY() + floatHeight, Color.green, 17, blockEnvironment));
        addBlock(new Block("Block 3", sameVolume, water, 4, swimmingPool.getMaxY() + floatHeight, Color.blue, 2, blockEnvironment));
        addBlock(new Block("Block 4", sameVolume, water, 7, swimmingPool.getMaxY() + floatHeight, Color.yellow, 1.5, blockEnvironment));

        addScale(new Scale("Scale 1", -1, swimmingPool.getMaxY(), 1, 1, 1, scaleEnvironment, water, blockEnvironment));
        addScale(new Scale("Scale 1", 1, swimmingPool.getY(), 1, 1, 1, scaleEnvironment, water, blockEnvironment));
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

    static class Sphere {

    }

    interface ScaleEnvironment {
        double getNormalForce(Scale scale);
    }

    public static class Scale {
        private String name;
        private ScaleEnvironment scaleEnvironment;
        private ScaleSurface surface;
        private ScaleBody body;
        private ArrayList<RectangularObject.Listener> listeners = new ArrayList<RectangularObject.Listener>();
        private ArrayList<Scale.Listener> scaleListeners = new ArrayList<Scale.Listener>();
        private double normalForce;

        public String getFormattedNormalForceString() {
            return new DefaultDecimalFormat("0.00").format(normalForce) + " N";
        }

        public static interface Listener {
            void normalForceChanged();
        }

        public Scale(String name, double x, double y, double width, double height, double depth, ScaleEnvironment scaleEnvironment, Water water, BlockEnvironment blockEnvironment) {
            this.name = name;
            this.scaleEnvironment = scaleEnvironment;
            surface = new ScaleSurface(name, x, y, width, height, depth, Color.blue, water, blockEnvironment);
            body = new ScaleBody(x, y, width, height, depth, Color.gray);
        }

        public void removeListener(RectangularObject.Listener listener) {
            listeners.remove(listener);
        }

        public void notifyRemoving() {
            for (RectangularObject.Listener listener : listeners) {
                listener.blockRemoving();
            }
        }

        public void addListener(Scale.Listener listener) {
            scaleListeners.add(listener);
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

        public void stepInTime(double simulationTimeChange) {
            //update loaded mass
            setNormalForce(scaleEnvironment.getNormalForce(this));
        }

        private void setNormalForce(double normalForce) {
            if (normalForce != this.normalForce) {
                this.normalForce = normalForce;
                for (Listener scaleListener : scaleListeners) {
                    scaleListener.normalForceChanged();
                }
            }
        }

        class ScaleBody extends RectangularObject {
            ScaleBody(double x, double y, double width, double height, double depth, Color faceColor) {
                super(name + ".body", x, y, width, height, depth, faceColor);
            }
        }
    }

    static class ScaleSurface extends Block {
        ScaleSurface(String parentName, double x, double y, double width, double height, double depth, Color faceColor, Water water, BlockEnvironment blockEnvironment) {
            super(parentName + ".top", width, water, x, y, faceColor, 1.0, blockEnvironment);
        }
    }

    public static class SwimmingPool extends RectangularObject {
        public SwimmingPool() {
            super("Pool", 0, 0, 10, 5, 5, new Color(255, 255, 255));
        }

    }

}
