package edu.colorado.phet.densityflex.view {
import away3d.materials.ColorMaterial;

import edu.colorado.phet.densityflex.model.ArrowModel;

public class ArrowNode extends MyMesh {
    private var arrowModel:ArrowModel;
    private const ARROW_HEIGHT:Number = 200;
    private var scaleFromModelToView:Number;

    public function ArrowNode(arrowModel:ArrowModel, scaleFromModelToView:Number, color:*, init:Object = null) {
        super(combine({material:new ColorMaterial(color, {alpha: 0.75})}, init));
        //        super(combine({material:new WireColorMaterial(color, {alpha: 0.75})}, init));//useful for debugging objects that may have the same z-coordinate
        this.arrowModel = arrowModel;
        this.scaleFromModelToView = scaleFromModelToView;
        this.mouseEnabled = false; // don't want to click on arrows, but instead the objects behind them
        arrowModel.addListener(doUpdate);
        doUpdate();
        //        super( combine( {outline:"black|2", material:new ColorMaterial( 0xFF0000 )}, init ) );
        //        super( combine( {outine:"black|2"}, init ) );
        //        super( init );
    }

    public function doUpdate():void {
        this.scaleY = arrowModel.getMagnitude() / ARROW_HEIGHT * scaleFromModelToView;
        this.rotationZ = -arrowModel.getAngle() * 180.0 / Math.PI;
    }

    override protected function build():void {
        super.build();
        const width:Number = 100;
        const height:Number = ARROW_HEIGHT;
        const fractionUsedByArrowhead:Number = 0.25;
        const arrowHeadWidth:Number = 150;
        const arrowHeadHeight:Number = height * fractionUsedByArrowhead;
        const bodyHeight:Number = height - arrowHeadHeight;

        v(-width / 2, 0, 0);
        v(width / 2, 0, 0);
        v(width / 2, bodyHeight, 0);
        v(-width / 2, bodyHeight, 0);

        v(-arrowHeadWidth / 2, bodyHeight, 0);
        v(arrowHeadWidth / 2, bodyHeight, 0);
        v(0, height, 0);

        uv(0, 0);
        uv(1, 1 - fractionUsedByArrowhead);
        uv(( 1 + arrowHeadWidth / width) / 2, 0);
        uv(( 1 - arrowHeadWidth / width) / 2, 1 - fractionUsedByArrowhead);
        uv(0, 1 - fractionUsedByArrowhead);
        uv(1, 1 - fractionUsedByArrowhead);
        uv(0.5, 1);

        f(0, 1, 2, 0, 1, 2);
        f(0, 2, 3, 0, 2, 3);
        f(3, 2, 6, 3, 2, 6);
        f(2, 5, 6, 2, 5, 6);
        f(4, 3, 6, 4, 3, 6);

        //        addSegment(new Segment(getVertexArray()[0], getVertexArray()[1], new WireframeMaterial(0x000000)));
        //        addSegment(new Segment(getVertexArray()[4], getVertexArray()[3], new WireframeMaterial(0x000000)));
        //        addSegment(new Segment(getVertexArray()[2], getVertexArray()[5], new WireframeMaterial(0x000000)));
        //        addSegment(new Segment(getVertexArray()[5], getVertexArray()[6], new WireframeMaterial(0xFF0000,{width:5})));
        //        addSegment(new Segment(getVertexArray()[6], getVertexArray()[4], new WireframeMaterial(0x000000)));

        type = "ArrowNode";
        url = "density";
    }
}
}