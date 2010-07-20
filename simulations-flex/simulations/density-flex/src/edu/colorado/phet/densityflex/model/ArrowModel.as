package edu.colorado.phet.densityflex.model {
public class ArrowModel {
    private var x:Number;
    private var y:Number;
    private const listeners:Array = new Array();

    public function ArrowModel(x:Number, y:Number) {
        this.x = x;
        this.y = y;
    }

    public function addListener(listener:Function):void {
        listeners.push(listener);
    }

    public function getMagnitude():Number {
        return Math.sqrt(x * x + y * y);
    }

    public function getAngle():Number {
        return Math.atan2(x, y);
    }

    public function setValue(x:Number, y:Number):void {
        this.x = x;
        this.y = y;
        for each (var listener:Function in listeners) {
            listener();
        }
    }
}
}