package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.components.*;

public class BooleanProperty extends Observable {
    private var _value:Boolean;
    private var _initialValue:Boolean;

    public function BooleanProperty(value:Boolean) {
        this._value = value;
        this._initialValue = _value;
    }

    public function get value():Boolean {
        return _value;
    }

    public function set value(value:Boolean):void {
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