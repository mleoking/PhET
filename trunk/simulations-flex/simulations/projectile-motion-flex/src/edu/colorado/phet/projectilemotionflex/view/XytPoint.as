/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/19/12
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {

//a single (x, y, t) point on a trajectory
public class XytPoint {
    private var _x: Number;     //x-coord
    private var _y: Number;     //y-coord
    private var _t: Number;     //t-coord
    //private var _i: int;        //index, first point is i = 0

    public function XytPoint( x: Number,  y: Number,  t: Number, i: int ) {
        this._x = x;
        this._y = y;
        this._t = t;
        //this._i = i;
    }

    public function get x():Number {
        return _x;
    }


    public function get y():Number {
        return _y;
    }


    public function get t():Number {
        return _t;
    }
}//end of class
}//end of package
