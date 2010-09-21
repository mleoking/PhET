package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.components.*;

public class NumericProperty extends Observable {
    private var _name:String;
    private var _units:String;
    private var _value:Number;
    private var _initialValue:Number;

    public function NumericProperty(name:String, units:String, value:Number) {
        this._name = name;
        this._units = units;
        this._value = value;
        this._initialValue = _value;
    }

    public function get name():String {
        return _name;
    }

    public function get value():Number {
        return _value;
    }

    public function get units():String {
        return _units;
    }

    public function set value(value:Number):void {
        if (isNaN(value)) {
            throw new Error("value was nan");
        }
        if (_value != value && !isNaN(value)) {
            _value = value;
            super.notifyObservers();
        }
    }

    public function reset():void {
        this.value = _initialValue;
    }
}
}