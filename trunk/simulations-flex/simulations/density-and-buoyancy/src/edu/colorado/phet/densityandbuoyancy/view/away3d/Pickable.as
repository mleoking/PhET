//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.flexcommon.model.BooleanProperty;

/**
 * An object that is movable by the mouse in the scene
 *
 * If it is part of a "parent" object, it should forward the functions to the main object
 */
public interface Pickable {
    function setPosition( x: Number, y: Number ): void;

    function getBody(): b2Body;

    function updateGeometry(): void;

    /**
     * @return A boolean property which holds true if the object can currently be picked and moved, or false if it cannot be.
     */
    function isPickableProperty(): BooleanProperty;

    function get densityObjectNode(): DensityAndBuoyancyObjectNode;
}
}