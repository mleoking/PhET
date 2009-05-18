package edu.colorado.phet.densityjava.model;

public class DensityModelBlockEnvironment implements BlockEnvironment {
    private DensityModel model;

    public DensityModelBlockEnvironment(DensityModel model) {
        this.model = model;
    }

    public double getFloorY(Block block) {
        DensityModel.RectangularObject target = model.getHighestObjectBelow(block);
        if (target != null) {
            return target.getMaxY();
        }
        Water water = model.getWater();
        if (water.getWidthRange().contains(block.getWidthRange()))
            return water.getBottomY();
        else
            return water.getSwimmingPoolSurfaceY();
    }

    public double getAppliedForce(Block block) {
        //if there is a block sitting on top of this block, the applied force
        //due to that block is equal to -N, since F12=-F21
        double sum = 0;
        DensityModel.RectangularObject justBeneath = model.getHighestObjectBelow(block);
        DensityModel.RectangularObject justAbove = model.getLowestObjectAbove(block);
        if (justAbove != null && justAbove instanceof Block && justAbove.getDistanceY(block) < 0.01) {
            Block above = (Block) justAbove;
            sum += -above.getNormalForce();
        }
//            if (justBeneath != null && justBeneath instanceof Block && justBeneath.getDistanceY(block) < 0.01) {
//                Block beneath = (Block) justBeneath;
//                sum += beneath.getNormalForce();
//            }
//            return 0.0;
        return sum;
    }

}
