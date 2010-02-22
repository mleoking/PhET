package edu.colorado.phet.densityflex.view {
import Box2D.Dynamics.b2Body;

import away3d.primitives.Cube;

public class PickableCube extends Cube implements Pickable {

    var picker : Pickable;

    public function PickableCube( picker : Pickable ) {
        super();
        this.useHandCursor = true;
        this.picker = picker;
    }

    public function setPosition( x:Number, y:Number ):void {
        picker.setPosition(x, y);
    }

    public function getBody():b2Body {
        return picker.getBody();
    }

    public function update():void {
        picker.update();
    }
}
}