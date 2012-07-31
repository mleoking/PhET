/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 7/1/12
 * Time: 6:12 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.model {
import edu.colorado.phet.projectilemotionflex.view.MainView;
import edu.colorado.phet.projectilemotionflex.view.MainView;
import edu.colorado.phet.projectilemotionflex.view.ProjectileView;

import flash.display.Sprite;

public class Projectile {

    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var _t: Number;                    //time in seconds, projectile is fired at t = 0
    private var _name: String;                 //name of projectile, in English: "tankshell", "golfball", etc
    private var _mass: Number;
    private var _diameter: Number;
    private var _dragCoefficient: Number;
    //private var inFlightGraphic: Sprite;       //graphic of projectile while in flight
    //private var onGroundGraphic: Sprite;       //graphic of projectile after it has hit the ground


    public function Projectile( mainView:MainView, trajectoryModel:TrajectoryModel, name:String, mass:Number,  diameter:Number,  dragCoefficient:Number ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel
        _name = name;
        _mass = mass;
        _diameter = diameter;
        _dragCoefficient = dragCoefficient;
        //this.inFlightGraphic = inFlightGraphic;
        //this.onGroundGraphic = onGroundGraphic;
    }//end constructor

    public function get name():String{
        return _name;
    }

    public function get mass():Number {
        return _mass;
    }

    public function set mass(value:Number):void {
        _mass = value;
    }

    public function get diameter():Number {
        return _diameter;
    }

    public function set diameter(value:Number):void {
        _diameter = value;
    }

    public function get dragCoefficient():Number {
        return _dragCoefficient;
    }

    public function set dragCoefficient(value:Number):void {
        _dragCoefficient = value;
    }

    public function get t(): Number {
        return _t;
    }

    public function set t( value: Number ): void {
        _t = value;
    }
}//end lclass
}//end package
