package edu.colorado.phet.densityflex.model {
import edu.colorado.phet.densityflex.components.*;

public class StringProperty extends Observable {
    private var _value:String;
    private var _initialValue:String;

    public function StringProperty(value:String) {
        this._value = value;
        this._initialValue = _value;
    }

    public function get value():String {
        return _value;
    }

    public function set value(value:String):void {
        if (_value != value) {
            _value = value;
            super.notifyObservers();
        }
    }

    public function reset():void {
        this.value = _initialValue;
    }
}
}