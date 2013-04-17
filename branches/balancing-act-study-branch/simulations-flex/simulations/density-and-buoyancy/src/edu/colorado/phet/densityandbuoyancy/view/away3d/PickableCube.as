//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import away3d.primitives.Cube;

import edu.colorado.phet.flexcommon.model.BooleanProperty;

/**
 * A cube that is possibly movable
 */
public class PickableCube extends Cube implements Pickable {

    private var _densityObjectNode: DensityAndBuoyancyObject3D;

    public function PickableCube( densityObjectNode: DensityAndBuoyancyObject3D ) {
        super();
        this._densityObjectNode = densityObjectNode;
        isPickableProperty().addListener( updateHandCursor );
        updateHandCursor();
    }

    private function updateHandCursor(): void {
        this.useHandCursor = isPickableProperty().value;
    }

    public function setPosition( x: Number, y: Number ): void {
        _densityObjectNode.setPosition( x, y );
    }

    public function getBody(): b2Body {
        return _densityObjectNode.getBody();
    }

    public function updateGeometry(): void {
        _densityObjectNode.updateGeometry();
    }

    public function isPickableProperty(): BooleanProperty {
        return _densityObjectNode.isPickableProperty();
    }

    public function get densityObjectNode(): DensityAndBuoyancyObject3D {
        return _densityObjectNode;
    }
}
}