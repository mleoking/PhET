package edu.colorado.phet.densityflex {
import Box2D.Dynamics.b2Body;

/**
 * An object that is movable by the mouse in the scene
 */
public interface Pickable {
    function setPosition( x:Number, y:Number ): void;
    function getBody() : b2Body;
    function update() : void;
}
}