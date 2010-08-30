package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;

public class DensityViewFull extends AbstractDensityModule {
    public function DensityViewFull() {
    }

    override public function initObjects():void {
        super.initObjects();
        //        addScales();
        //        _model.addDensityObject(Block.newBlockSizeMass(3, 4.0, -4.5, 0, new ColorTransform(0.5, 0.5, 0), _model));
        //        model.addDensityObject(Block.newBlockSizeMass(2, 4.0, -1.5, 0, new ColorTransform(0, 0, 1), model));
        //        model.addDensityObject(Block.newBlockSizeMass(1.5, 4.0, 1.5, 0, new ColorTransform(0, 1, 0), model));
    }

    override protected function createDensityObjectNode(densityObject:DensityObject):DensityObjectNode {
        var densityObjectNode:DensityObjectNode = super.createDensityObjectNode(densityObject);
        densityObjectNode.addArrowNodes();
        return densityObjectNode;
    }

    public function setGravityForceVisible(selected:Boolean):void {
        for each (var densityObjectNode:DensityObjectNode in super.densityObjectNodeList) {
            densityObjectNode.setGravityForceVisible(selected);
        }
    }

    public function setBuoyancyForceVisible(selected:Boolean):void {
        for each (var densityObjectNode:DensityObjectNode in super.densityObjectNodeList) {
            densityObjectNode.setBuoyancyForceVisible(selected);
        }
    }

    public function setContactForceVisible(selected:Boolean):void {
        for each (var densityObjectNode:DensityObjectNode in super.densityObjectNodeList) {
            densityObjectNode.setContactForceVisible(selected);
        }
    }

    public function setFluidDragForceVisible(selected:Boolean):void {
        for each (var densityObjectNode:DensityObjectNode in super.densityObjectNodeList) {
            densityObjectNode.setFluidDragForceVisible(selected);
        }
    }

    public function createToyboxObject(densityObject:DensityObject):void {
        _model.addDensityObject(densityObject.copy(_model));
    }
}
}