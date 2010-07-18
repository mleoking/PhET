package edu.colorado.phet.densityflex.model {
public class ArrowModel {
    var x:Number;
    var y:Number;
    const listeners:Array = new Array();

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
}
}