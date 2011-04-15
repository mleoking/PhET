//  Copyright 2002-2011, University of Colorado

//REVIEW generalize and move to common?
/**
 * Model for a 2d vector that signifies changes to listeners.
 */
package edu.colorado.phet.densityandbuoyancy.model {
public class Vector2D {
    private var _x: Number;
    private var _y: Number;
    private const listeners: Array = new Array();

    //REVIEW yes, use observable pattern
    // TODO: use observable pattern or property pattern here?

    public function Vector2D( x: Number, y: Number ) {
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