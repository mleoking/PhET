package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.model.Block;
import edu.colorado.phet.densityflex.model.DensityObject;

import flash.events.Event;
import flash.geom.ColorTransform;

public class DensityViewFull extends DensityView {
    public function DensityViewFull() {
    }

    override public function initObjects():void {
        super.initObjects();
        addScales();
        model.addDensityObject(Block.newBlockSizeMass(3, 4.0, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
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

    public function createToyboxObject( densityObject:DensityObject ):void {
        model.addDensityObject( densityObject.copy( model ) );
    }
}
}