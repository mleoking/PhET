//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.flexcommon.model {

/**
 * An observable boolean property. Used when we want a mutable observable boolean value.
 */
public class BooleanProperty extends Observable {
    private var _value: Boolean;
    private var _initialValue: Boolean;

    public function BooleanProperty( value: Boolean ) {
        this._value = value;
        this._initialValue = _value;
    }

    public function get value(): Boolean {
        return _value;
    }

    public function set value( value: Boolean ): void {
        if ( _value != value ) {
            _value = value;
            super.notifyObservers();
        }
    }

    /**
     * Same as setting the value, except that when reset it will revert to this value.
     */
    public function set initialValue( value: Boolean ): void {
        _initialValue = value;
        this.value = value;
    }

    public function reset(): void {
        this.value = _initialValue;
    }
}
}