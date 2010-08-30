package edu.colorado.phet.densityandbuoyancy.view {
import away3d.containers.ObjectContainer3D;

import flash.display.Sprite;

/**
 * Displays a sprite as a face of a 3d mesh.
 * Clients need to call resize() if modifications to the sprite change its size.
 */
public class Sprite3D extends ObjectContainer3D {
    private var _spriteFace:SpriteFace;
    private var sprite:Sprite;

    public function Sprite3D(sprite:Sprite) {
        this.sprite = sprite;
        _spriteFace = new SpriteFace(sprite);
        addChild(_spriteFace);
    }

    public function resize():void {
        removeChild(_spriteFace);
        _spriteFace = new SpriteFace(sprite);
        addChild(_spriteFace);
    }
}
}