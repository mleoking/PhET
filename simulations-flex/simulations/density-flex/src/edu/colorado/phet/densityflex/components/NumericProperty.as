package edu.colorado.phet.densityflex.components {
public class NumericProperty extends Observable{
    private var _name:String;
    private var _units:String;
    private var _value:Number;
    
    public function NumericProperty(name:String, units:String, value:Number) {
        this._name=name;
        this._units=units;
        this._value=value;
    }

    function get name():String {
        return _name;
    }

    public function get value():Number{
        return _value;
    }

    function get units():String {
        return _units;
    }

    public function set value(value:Number):void {
        if (_value != value){
        _value = value;
        super.notifyObservers();
        }
    }

}
}