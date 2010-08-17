package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.model.Cuboid;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;

public class CubeNode extends CuboidNode {
    private var cube:PickableCube;

    public function CubeNode(cuboid:Cuboid, view:AbstractDensityModule) {
        super(cuboid, view);
        cube = new PickableCube(this);
        //        updateGeometry();
        cube.segmentsH = 2;
        cube.segmentsW = 2;
        cube.useHandCursor = true;
    }

    public function getCube():PickableCube {
        return cube;
    }

    public override function updateGeometry():void {
        super.updateGeometry();
        cube.width = getCuboid().getWidth() * DensityModel.DISPLAY_SCALE;
        cube.height = getCuboid().getHeight() * DensityModel.DISPLAY_SCALE;
        cube.depth = getCuboid().getDepth() * DensityModel.DISPLAY_SCALE;
    }
}
}