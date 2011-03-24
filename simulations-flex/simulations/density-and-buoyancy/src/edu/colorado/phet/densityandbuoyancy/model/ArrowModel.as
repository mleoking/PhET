//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
public class ArrowModel {
    private var _x: Number;
    private var _y: Number;
    private const listeners: Array = new Array();

    // TODO: use observable pattern here?

    public function ArrowModel( x: Number, y: Number ) {
        this._x = x;
        this._y = y;
    }

    public function addListener( listener: Function ): void {
        listeners.push( listener );
    }

    public function getMagnitude(): Number {
        return Math.sqrt( _x * _x + _y * _y );
    }

    public function getAngle(): Number {
        return Math.atan2( _x, _y );
    }

    public function setValue( x: Number, y: Number ): void {
        this._x = x;
        this._y = y;
        for each ( var listener: Function in listeners ) {
            listener();
        }
    }

    public function get x(): Number {
        return _x;
    }

    public function get y(): Number {
        return _y;
    }
}
}