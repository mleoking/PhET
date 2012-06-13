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
    public function Photon( xP:Number = 0,  yP:Number = 0,  cos:Number = 0,  sin:Number = 0, emitted:Boolean = false ) {
        this._xP = xP;
        this._yP = yP;
        this._cos = cos;
        this._sin = sin;
        this._emitted = emitted;
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

}//end of class
}//end of package
