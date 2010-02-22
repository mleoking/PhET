package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.*;

import away3d.materials.*;

import edu.colorado.phet.densityflex.model.DensityModel;
import edu.colorado.phet.densityflex.model.Listener;

import edu.colorado.phet.densityflex.model.Scale;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFormat;

public class ScaleNode extends CuboidNode implements Pickable, Listener{

    private var frontSprite : Sprite;
    private var scaler:Scale;
    private var textField : TextField;
    private var view : DensityView3D;

    private var base : PickableCube;

    private static var WALL_RES : Number = 100;

    public function ScaleNode( scale:Scale, view : DensityView3D ) : void {
        super(scale);
        this.scaler = scale;
        this.view = view;

        frontSprite = new Sprite();

        frontSprite.graphics.beginFill(0xFFFFFF);
        frontSprite.graphics.drawRect(0, 0, WALL_RES * scaler.getWidth(), WALL_RES * scaler.getHeight());
        frontSprite.graphics.endFill();

        textField = new TextField();
        frontSprite.addChild(textField);
        updateText();

        var frontMaterial : MovieMaterial = new MovieMaterial(frontSprite);
        frontMaterial.smooth = true; //makes the font smooth instead of jagged, see http://www.mail-archive.com/away3d-dev@googlegroups.com/msg06699.html
        var redWallMaterial : ColorMaterial = new ColorMaterial(0xCCCCCC);

        base.cubeMaterials.left = base.cubeMaterials.right = base.cubeMaterials.top = base.cubeMaterials.bottom = base.cubeMaterials.front = redWallMaterial;

        base.cubeMaterials.back = frontMaterial;
    }


    override public function addNodes():void {
        trace( "scale addNodes()");
        base = new PickableCube(this);
        base.width = getCuboid().getWidth() * DensityModel.DISPLAY_SCALE;
        base.height = getCuboid().getHeight() * DensityModel.DISPLAY_SCALE;
        base.depth = getCuboid().getDepth() * DensityModel.DISPLAY_SCALE;
        base.segmentsH = 2;
        base.segmentsW = 2;
        addChild(base);
        base.useHandCursor = true;
    }

    public function updateText() : void {
        textField.text = String(scaler.getScaleReadout());
        textField.width = WALL_RES * scaler.getWidth();
        textField.height = WALL_RES * scaler.getHeight();
        var format : TextFormat = new TextFormat();
        format.size = int(45 * (200 / base.width));
        format.bold = true;
        format.font = "Arial";
        textField.multiline = true;
        textField.setTextFormat(format);
    }

    override public function update():void {
        super.update();
        updateText();
    }

    override public function remove():void {
        view.removeObject( this );
    }
}
}