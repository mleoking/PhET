package edu.colorado.phet.densityandbuoyancy.view {
import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class TextFieldMesh extends Sprite3D {
    private var _textField:TextField;
    private var format:TextFormat;

    public function TextFieldMesh(text:String, format:TextFormat = null) {
        super(createSprite(text, format));
        this.format = format;
    }

    public function set text(t:String):void {
        setTextNoResize(t);
        resize();
    }

    private function setTextNoResize(t:String):void {
        _textField.text = t;
        if (format != null) {
            _textField.setTextFormat(format);
        }
    }

    private function createSprite(text:String, format:TextFormat):Sprite {
        const sprite:Sprite = new Sprite();
        _textField = new TextField();
        _textField.autoSize = TextFieldAutoSize.LEFT;
        _textField.background = true;
        _textField.backgroundColor = 0xffffff;
        setTextNoResize(text);
        sprite.addChild(_textField);
        return sprite;
    }
}
}