package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import away3d.primitives.Cube;

import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;

/**
 * A cube that is possibly movable
 */
public class PickableCube extends Cube implements Pickable {

    private var picker: Pickable;

    public function PickableCube( picker: Pickable ) {
        super();
        this.picker = picker;
        isPickableProperty().addListener( updateHandCursor );
        updateHandCursor();
    }

    private function updateHandCursor(): void {
        this.useHandCursor = isPickableProperty().value;
    }

    public function setPosition( x: Number, y: Number ): void {
        picker.setPosition( x, y );
    }

    public function getBody(): b2Body {
        return picker.getBody();
    }

    public function updateGeometry(): void {
        picker.updateGeometry();
    }

    public function isPickableProperty(): BooleanProperty {
        return picker.isPickableProperty();
    }
}
}