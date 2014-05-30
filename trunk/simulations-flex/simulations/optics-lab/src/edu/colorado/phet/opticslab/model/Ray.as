/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 5/23/14
 * Time: 12:54 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.model {
/*
 * A ray of light from a source of light, represented by a single line in the RayView. A ray moves forward in space until it
 * intercepts an optical component (lens, mask, etc) or reaches the perimeter of the layout stage. The component then either
 * absorbs or re-directs (reflects or refracts) the ray. In any case, the original ray is terminated at the component, and,
 * if necessary, a new ray is generated representing the refracted or reflected ray.
 * A Ray belongs to a Source, which is a collection of Rays.
 * */
public class Ray {
    private var _source: LightSource;  //every ray belongs to a light source
    //private var _x0: Number;    //x-component of initial position of ray
    //private var _y0: Number;    //y-component of initial position of ray
    private var _angle: Number; //angle in radians = direction of ray, measured CCW from +x direction
    private var _cosA: Number;   //cosine of angle, stored to avoid re-computing
    private var _sinA: Number;   //sine of angle, stored to avoid re-computing
    private var _length: Number;//length of ray from source to point of interception with first obstacle (a component or border of stage)
    private static var _MAXLENGTH: Number;
    private var _changed: Boolean; //true if ray length is changed in last update

    public function Ray( source: LightSource,  angle: Number ) {
        _source = source;
        _angle = angle;
        _cosA = Math.cos( _angle );
        _sinA = Math.sin( _angle );
        _MAXLENGTH = 2;  //2 meters
        _length = _MAXLENGTH;    //max length of ray is default

    } //end of constructor

    public function get length():Number {
        return _length;
    }

    public function set length(value:Number):void {
        _length = value;
        _changed = true;
    }

    public function get angle():Number {
        return _angle;
    }

    public function get cosA():Number {
        return _cosA;
    }

    public function get sinA():Number {
        return _sinA;
    }

    public function get MAXLENGTH():Number {
        return _MAXLENGTH;
    }
} //end of class
} //end of package
