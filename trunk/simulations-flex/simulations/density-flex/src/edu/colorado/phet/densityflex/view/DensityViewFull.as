package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.model.Block;

import flash.geom.ColorTransform;

public class DensityViewFull extends DensityView {
    public function DensityViewFull() {
    }

    override public function initObjects():void {
        super.initObjects();
        addScales();
        model.addDensityObject(Block.newBlockSizeMass(3, 4.0, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
    }
}
}