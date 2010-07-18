package edu.colorado.phet.densityflex.view {
import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityflex.model.DensityModel;

public class DensityObjectNode extends ObjectContainer3D {
    public function DensityObjectNode() {
        super();
    }

    //Override to specify the depth of the object so arrows will render just outside of the object
    public function getArrowOriginZ():Number {
        return 0;
    }

    public function addArrowNode(arrowNode:ArrowNode):void {
        arrowNode.z = -getArrowOriginZ() * DensityModel.DISPLAY_SCALE - 1E-6;
        addChild(arrowNode);
    }
}
}