/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 5/27/14
 * Time: 3:20 PM
 * Model of an aperture mask
 */
package edu.colorado.phet.opticslab.model {
import mx.skins.halo.ProgressMaskSkin;

public class Mask {
    private var myOpticsModel:OpticsModel;
    private var _index: uint;  //index labeling position of this mask in myOpticsModel.opticalComponents[index];
    private var _x:Number;     //x- and y-coordinates of bottom edge of mask, in meters
    private var _y:Number;
    private var _angle:Number; //angle of mask, in radians default angle = 0 for vertical mask.  Positive angle = CCW rotation
    private var _height:Number;//height of mask, in meters
    private static var _type: String; //type of optical component = MASK, so ray-tracer knows how to behave when ray intersects this component

    public function Mask( opticsModel: OpticsModel, idx: uint) {
        myOpticsModel = opticsModel;
        _index = idx;
        _type = "MASK";
        _x = _y = 0.5;
        _angle = 0;
        _height = 0.2;
    }//end constructor

    public function setLocation( xInMeters: Number, yInMeters: Number ):void{
        _x = xInMeters;
        _y = yInMeters;
        myOpticsModel.updateViews();
    }

    public function get x():Number {
        return _x;
    }

    public function get y():Number {
        return _y;
    }

    public function set height( value: Number ): void {
        _height = value;
    }

    public function get type(): String {
        return _type;
    }

    public function get height(): Number {
        return _height;
    }
}//end class
}//end package
