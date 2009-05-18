package edu.colorado.phet.densityjava.model;

//environment for computing applied forces and collisions
public class DensityModelBlockEnvironment implements BlockEnvironment {
    private DensityModel model;

    public DensityModelBlockEnvironment(DensityModel model) {
        this.model = model;
    }

    //Find the Y value of whatever the block could eventually settle on (i.e. the ground, the pool bottom, another block)
    public double getFloorY(Block block) {
        DensityModel.RectangularObject target = model.getHighestObjectBelow(block);
        if (target != null) {
            return target.getMaxY();
        }
        if (getWater().getWidthRange().contains(block.getWidthRange()))
            return getWater().getBottomY();
        else
            return getWater().getSwimmingPoolSurfaceY();
    }

    public Water getWater() {
        return model.getWater();
    }

    //Determine the applied force on a block (e.g. due to other blocks resting on it)
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
        //TODO: resolve recursive compuation problem: F_normal_1=f(F_applied_2) and F_applied_2=f(F_normal_1)
//            if (justBeneath != null && justBeneath instanceof Block && justBeneath.getDistanceY(block) < 0.01) {
//                Block beneath = (Block) justBeneath;
//                sum += beneath.getNormalForce();
//            }
//            return 0.0;
        return sum;
    }

}
