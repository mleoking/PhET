/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 8/2/12
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.model {
/**
 * Model of a single trajectory of a projectile.  Multiple trajectories can be running simultaneously.
 */
public class Trajectory {
    private var stageH: Number;
    private var stageW: Number;
    private var g: Number;          //acceleration of gravity, all units are SI
    private var _xP: Number;        //current x- and y_coords of position of projectile in meters
    private var _yP: Number;
    private var _xP0: Number;       //x- and y- coordinates of current position of cannon,
    private var _yP0: Number;       //relative to origin, which is at ground level (y = 0)
    private var xP00: Number;       //x- and y-coordinates of initial position of projectile when projectile was fired
    private var yP00: Number
    private var _vX: Number;        //x- and y-coords of velocity of projectile
    private var _vY: Number;
    private var v: Number;          //current speed of projectile
    private var aX: Number;         //x- and y-components of acceleration
    private var aY: Number;
    private var _vX0: Number;       //x- and y-components of initial velocity
    private var _vY0: Number;
    private var _v0: Number;        //initial speed of projectile
    private var _angleInDeg: Number;    //angle of cannon barrel in degrees, measured CCW from horizontal

    private var _ticMarkTime: Number;   //time of tic Mark, which are at 1 second intervals
    private var _drawTicMarkNow: Boolean;      //flag to indicate that 1-sec tic mark should be drawn on trajectory
    private var _updateReadoutsNow: Boolean;   //flag to indicate that readouts should be updated, 1 update per second

    private var _airResistance: Boolean;  //true if air resistance is on
    private var B: Number;                //parameter that measures drag:  acceleration due to drag = -B*v*v

    private var projectileIndex: int;           //index of projectile of this trajectory: projectiles[pIndex] = current projectile
    private var mass0: Number;          //mass, diameter, and drag coefficient of user-controlled projectile
    private var diameter0: Number;
    private var dragCoefficient0: Number;

    public function Trajectory() {
    }
}
}
