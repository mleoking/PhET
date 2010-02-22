package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.*;

import away3d.materials.*;

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

        this.cubeMaterials.left = this.cubeMaterials.right = this.cubeMaterials.top = this.cubeMaterials.bottom = this.cubeMaterials.front = redWallMaterial;

        this.cubeMaterials.back = frontMaterial;
    }

    public function updateText() : void {
        textField.text = String(scaler.getScaleReadout());
        textField.width = WALL_RES * scaler.getWidth();
        textField.height = WALL_RES * scaler.getHeight();
        var format : TextFormat = new TextFormat();
        format.size = int(45 * (200 / this.width));
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