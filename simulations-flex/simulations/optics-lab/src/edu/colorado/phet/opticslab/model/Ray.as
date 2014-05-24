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
    private var _x0: Number;    //x-component of initial position of ray
    private var _y0: Number;    //y-component of initial position of ray
    private var _angle: Number; //angle in radians = direction of ray, measured CCW from +x direction
    private var _length: Number;//length of ray from source to point of interception with first obstacle (a component or border of stage)


    public function Ray( x0: Number,  y0: Number,  angle: Number ) {
        _x0 = x0;
        _y0 = y0;
        _angle = angle;

    } //end of constructor

    public function setLength( length ):void{
       _length = length;
    }
} //end of class
} //end of package
