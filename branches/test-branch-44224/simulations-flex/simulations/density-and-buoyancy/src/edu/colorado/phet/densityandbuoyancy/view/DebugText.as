package edu.colorado.phet.densityandbuoyancy.view {
import mx.controls.Text;

public class DebugText extends Text {
    public function DebugText() {
        super();

        instance = this;
    }

    private static var instance:DebugText;

    public static function debug(str:String):void {
        if (instance) {
            instance.text += str + "\n";
        }
    }

    public static function clear():void {
        if (instance) {
            instance.text = "";
        }
    }
}
}