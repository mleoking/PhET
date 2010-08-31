package edu.colorado.phet.densityandbuoyancy.components {
import flash.text.TextField;
import flash.text.TextFormat;

public class Tick {
    public var value:Number;
    public var color:uint;
    public var label:String;
    public var textField:TextField;

    public function Tick(value:Number, color:uint, label:String) {
        this.value = value;
        this.color = color;
        this.label = label;
        textField = new TextField();
        textField.mouseEnabled = false;
        textField.text = label;
        var textFormat:TextFormat = new TextFormat();
        textFormat.color = color;
        textFormat.bold = true;
        textField.setTextFormat(textFormat);
    }
}
}