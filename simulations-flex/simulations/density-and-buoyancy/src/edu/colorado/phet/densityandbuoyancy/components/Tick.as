package edu.colorado.phet.densityandbuoyancy.components {
public class Tick {
    public var value:Number;
    public var color:uint;
    public var label:String;

    public function Tick(value:Number, color:uint, label:String) {
        this.value = value;
        this.color = color;
        this.label = label;
    }
}
}