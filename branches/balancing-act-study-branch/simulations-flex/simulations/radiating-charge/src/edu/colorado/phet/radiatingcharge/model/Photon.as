/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/11/12
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.model {

//individual photon on a ray
//a ray is a collection of photons
public class Photon {
    private var _xP:Number;     //x-coordinate of photon
    private var _yP:Number;     //y-coordinate of photon
    private var _cos:Number;    //direction cosine of photon's velocity vector
    private var _sin:Number;    //direction sine of photon's velocity vector
    private var _emitted:Boolean; //true if photon has been emitted since start of radiation
    private var _tEmitted:Number; //time at which photon was emitted
    private var _x0:Number;     //x-coordinate of position when emitted
    private var _y0:Number;     //y-coordinate of position when emitted

    public function Photon( xP:Number = 0,  yP:Number = 0,  cos:Number = 0,  sin:Number = 0, emitted:Boolean = false , tEmitted:Number = 0 ) {
        this._xP = xP;
        this._yP = yP;
        this._cos = cos;
        this._sin = sin;
        this._emitted = emitted;
        this._tEmitted = tEmitted;
    }

    public function resetPhoton():void{
        _cos = 0;
        _sin = 0;
    }

    public function set xP( xPos:Number ):void{
        this._xP = xPos;
    }

    public function get xP():Number{
        return this._xP;
    }

    public function set yP( yPos:Number ):void{
        this._yP = yPos;
    }

    public function get yP():Number {
       return this._yP;
    }

    //*******
    public function set x0( xPos:Number ):void{
        this._x0 = xPos;
    }

    public function get x0():Number{
        return this._x0;
    }

    public function set y0( yPos:Number ):void{
        this._y0 = yPos;
    }

    public function get y0():Number {
        return this._y0;
    }
    //*******

    public function set cos( cosine:Number ):void{
        this._cos = cosine;
    }

    public function get cos():Number {
        return this._cos;
    }

    public function set sin( sine:Number ):void{
        this._sin = sine;
    }

    public function get sin():Number {
        return this._sin;
    }

    public function set emitted( tOrF:Boolean ):void{
        this._emitted = tOrF;
    }

    public function get emitted():Boolean {
        return this._emitted;
    }

    public function set tEmitted( time:Number ):void{
        this._tEmitted = time;
    }

    public function get tEmitted():Number {
        return this._tEmitted;
    }

}//end of class
}//end of package
