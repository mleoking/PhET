package edu.colorado.phet.densityandbuoyancy.view.away3d {
import edu.colorado.phet.densityandbuoyancy.view.*;

import away3d.materials.*;
import away3d.primitives.Cube;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Scale;

import flash.display.Bitmap;
import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFormat;

import mx.core.BitmapAsset;

public class ScaleNode extends CuboidNode {

    private var _scale:Scale;

    private var base:Cube;
    private var top:Cube;
    private var stand:Cube;

    public function ScaleNode(scale:Scale, view:AbstractDensityModule):void {
        super(scale, view);
        this._scale = scale;

        var totalWidth:Number = getCuboid().getWidth() * DensityModel.DISPLAY_SCALE;
        var totalHeight:Number = getCuboid().getHeight() * DensityModel.DISPLAY_SCALE;
        var totalDepth:Number = getCuboid().getDepth() * DensityModel.DISPLAY_SCALE;

        base = new Cube();
        base.width = totalWidth;
        base.height = totalHeight / 2;
        base.depth = totalDepth;
        base.segmentsH = 2;
        base.segmentsW = 2;
        base.y = -totalHeight / 4;
        addChild(base);

        top = new Cube();
        top.width = totalWidth;
        top.height = totalHeight / 8;
        top.depth = totalDepth;
        top.segmentsH = 2;
        top.segmentsW = 2;
        top.y = 7 * totalHeight / 16;
        addChild(top);

        stand = new Cube();
        stand.width = totalWidth / 5;
        stand.height = totalHeight - base.height - top.height;
        stand.depth = totalDepth / 5;
        stand.y = base.y + base.height / 2 + stand.height / 2;
        addChild(stand);

        updateText();
        updateGeometry();

        var brightMaterial:ColorMaterial = new ColorMaterial(0xFFFFFF);
        var sideMaterial:ColorMaterial = new ColorMaterial(0xCCCCCC);
        var hidMaterial:ColorMaterial = new ColorMaterial(0x999999);

        base.cubeMaterials.left = base.cubeMaterials.right = base.cubeMaterials.top = base.cubeMaterials.bottom = base.cubeMaterials.front = sideMaterial;
        base.cubeMaterials.top = hidMaterial;

        base.cubeMaterials.back = sideMaterial;

        top.material = sideMaterial;
        top.cubeMaterials.back = brightMaterial;

        stand.material = sideMaterial;

        scale.addScaleReadoutListener(updateText);
    }

    public function updateText():void {
        setReadoutText( _scale.getScaleReadout());
    }

    override public function updateGeometry():void {
        super.updateGeometry();
        textReadout.x = base.x - base.width / 2;
        textReadout.y = base.y - base.height / 2;
        textReadout.z = base.z - base.depth / 2 - DensityConstants.FUDGE_FACTOR_DZ;
        updateText();
    }

    override protected function getFontReadoutSize():Number {
        return 36;
    }
}
}