package edu.colorado.phet.densityflex.model {
import edu.colorado.phet.densityflex.view.BlockNode;
import edu.colorado.phet.densityflex.view.DensityObjectNode;
import edu.colorado.phet.densityflex.view.DensityView;

import flash.geom.ColorTransform;

public class MysteryBlock extends Block {
    private var label:String;

    public function MysteryBlock(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, label:String) {
        super(density, size, x, y, color, model);
        this.label = label;
    }

    override public function createNode(view:DensityView):DensityObjectNode {
        return new BlockNode(this, view, new StringProperty(label));
    }

}
}