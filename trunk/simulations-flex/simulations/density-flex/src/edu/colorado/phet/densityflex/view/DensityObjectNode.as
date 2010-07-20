package edu.colorado.phet.densityflex.view {
import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityflex.model.DensityModel;
import edu.colorado.phet.densityflex.model.DensityObject;

public class DensityObjectNode extends ObjectContainer3D {
    private var densityObject:DensityObject;
    private var numArrowNodes:Number = 0;

    public function DensityObjectNode(densityObject:DensityObject) {
        super();
        this.densityObject = densityObject;
    }

    //Override to specify the depth of the object so arrows will render just outside of the object
    public function getArrowOriginZ():Number {
        return 0;
    }

    public function addArrowNode(arrowNode:ArrowNode):void {
        numArrowNodes = numArrowNodes + 1;
        arrowNode.z = -getArrowOriginZ() * DensityModel.DISPLAY_SCALE - 1E-6 * numArrowNodes;//Offset so they don't overlap in z
        addChild(arrowNode);
    }

    function getDensityObject():DensityObject {
        return densityObject;
    }
}
}