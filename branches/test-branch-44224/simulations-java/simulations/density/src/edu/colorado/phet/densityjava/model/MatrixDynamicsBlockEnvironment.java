package edu.colorado.phet.densityjava.model;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 18, 2009
 * Time: 10:44:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixDynamicsBlockEnvironment implements BlockEnvironment {
    private DensityModel model;

    public MatrixDynamicsBlockEnvironment(DensityModel model) {
        this.model = model;
    }

    //Find the Y value of whatever the block could eventually settle on (i.e. the ground, the pool bottom, another block)
    public double getFloorY(Block block) {
        RectangularObject target = model.getHighestObjectBelow(block);
        if (target != null)
            return target.getMaxY();
        else if (getWater().getWidthRange().contains(block.getWidthRange()))
            return getWater().getBottomY();
        else
            return getWater().getSwimmingPoolSurfaceY();
    }

    private Water getWater() {
        return model.getWater();
    }

    public double getAppliedForce(Block block) {
        DensityModel.ObjectElement[] elements = model.getElements();
        MatrixDynamics.ContactGroup[] contactGroups = model.getContactGroups(elements);
        MatrixDynamics matrixDynamics = new MatrixDynamics(elements, contactGroups);
        double[][] solution = matrixDynamics.solve();
        int index = -1;
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].getObject() == block) {
                index = i;
            }
        }
        double sumForces = 0;
        for (int i = 0; i < elements.length; i++) {
            sumForces = sumForces + solution[i][index];
        }
        return sumForces;
    }
}
