package edu.colorado.phet.densityflex.view {
import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityflex.model.DensityModel;
import edu.colorado.phet.densityflex.model.DensityObject;

public class DensityObjectNode extends ObjectContainer3D {
    private var densityObject:DensityObject;
    private static var numArrowNodes:Number = 0;

    private var gravityArrowNode:ArrowNode;
    private var buoyancyArrowForceNode:ArrowNode;
    private var contactArrowForceNode:ArrowNode;
    private var dragArrowForceNode:ArrowNode;

    public function DensityObjectNode(densityObject:DensityObject) {
        super();
        this.densityObject = densityObject;
    }

    public function addArrowNodes():void {
        gravityArrowNode = new ArrowNode(getDensityObject().getGravityForceArrowModel(), 2, 0x0000FF);
        addArrowNode(gravityArrowNode);

        buoyancyArrowForceNode = new ArrowNode(getDensityObject().getBuoyancyForceArrowModel(), 2, 0xFF00FF);
        addArrowNode(buoyancyArrowForceNode);

        dragArrowForceNode = new ArrowNode(getDensityObject().getDragForceArrowModel(), 2, 0xFF0000);
        addArrowNode(dragArrowForceNode);

        contactArrowForceNode = new ArrowNode(getDensityObject().getContactForceArrowModel(), 2, 0xFF8800);
        addArrowNode(contactArrowForceNode);
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

    public function getDensityObject():DensityObject {
        return densityObject;
    }

    public function setGravityForceVisible(selected:Boolean):void {
        gravityArrowNode.visible = selected;
    }

    public function setBuoyancyForceVisible(selected:Boolean):void {
        buoyancyArrowForceNode.visible = selected;
    }

    public function setContactForceVisible(selected:Boolean):void {
        contactArrowForceNode.visible = selected;
    }

    public function setFluidDragForceVisible(selected:Boolean):void {
        dragArrowForceNode.visible = selected;
    }
}
}