package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import away3d.primitives.Cube;

import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;

/**
 * A cube that is possibly movable
 */
public class PickableCube extends Cube implements Pickable {

    private var _densityObjectNode: DensityObjectNode;

    public function PickableCube( densityObjectNode: DensityObjectNode ) {
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

    public function get densityObjectNode(): DensityObjectNode {
        return _densityObjectNode;
    }
}
}