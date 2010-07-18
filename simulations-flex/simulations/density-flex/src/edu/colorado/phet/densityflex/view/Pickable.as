package edu.colorado.phet.densityflex.view {
import Box2D.Dynamics.b2Body;

/**
 * An object that is movable by the mouse in the scene
 *
 * If it is part of a "parent" object, it should forward the functions to the main object
 */
public interface Pickable {
    function setPosition(x:Number, y:Number):void;

    function getBody():b2Body;

    function update():void;
}
}