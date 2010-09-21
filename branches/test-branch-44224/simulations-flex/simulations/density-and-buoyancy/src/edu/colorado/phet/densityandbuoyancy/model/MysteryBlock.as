package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;
import edu.colorado.phet.densityandbuoyancy.view.away3d.BlockNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;

import flash.geom.ColorTransform;

public class MysteryBlock extends Block {
    private var label:String;

    public function MysteryBlock(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, label:String) {
        super(density, size, x, y, color, model, Material.CUSTOM);
        this.label = label;
    }

    override public function createNode(view:AbstractDensityModule):DensityObjectNode {
        return new BlockNode(this, view, new StringProperty(label), 2);
    }

}
}