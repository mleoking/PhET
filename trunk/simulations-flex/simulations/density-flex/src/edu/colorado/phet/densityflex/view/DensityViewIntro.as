package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.model.Block;
import edu.colorado.phet.densityflex.model.Scale;

import flash.geom.ColorTransform;

public class DensityViewIntro extends DensityView {
    public function DensityViewIntro() {
        super();
    }

    override public function initObjects():void {
        super.initObjects();
        initializeTab1SameMass();
        //model.initializeTab1SameVolume();
    }

    public function initializeTab1SameMass():void {
        model.addDensityObject(Block.newBlockSizeMass(3, 4.0, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockSizeMass(2, 4.0, -1.5, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockSizeMass(1.5, 4.0, 1.5, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockSizeMass(1, 4.0, 4.5, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    public function initializeTab1SameVolume():void {
        model.addDensityObject(Block.newBlockDensitySize(1.0 / 8.0, 2, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockDensitySize(0.5, 2, -1.5, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockDensitySize(2, 2, 1.5, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockDensitySize(4, 2, 4.5, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    public function switchToSameMass():void {
        model.clearDensityObjects();
        initializeTab1SameMass();
    }

    override public function reset():void {
        super.reset();
        switchToSameMass();
    }

    public function switchToSameVolume():void {
        model.clearDensityObjects();
        initializeTab1SameVolume();
    }
}
}