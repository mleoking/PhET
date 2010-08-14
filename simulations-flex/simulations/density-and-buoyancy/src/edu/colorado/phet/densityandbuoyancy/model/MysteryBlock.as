package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.view.BlockNode;
import edu.colorado.phet.densityandbuoyancy.view.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;

import flash.geom.ColorTransform;

public class MysteryBlock extends Block {
    private var label:String;

    public function MysteryBlock(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, label:String) {
        super(density, size, x, y, color, model, Substance.CUSTOM);
        this.label = label;
    }

    override public function createNode(view:AbstractDensityModule):DensityObjectNode {
        return new BlockNode(this, view, new StringProperty(label));
    }

}
}