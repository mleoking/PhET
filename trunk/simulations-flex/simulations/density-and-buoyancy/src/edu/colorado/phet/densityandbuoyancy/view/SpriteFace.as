package edu.colorado.phet.densityandbuoyancy.view {
import away3d.materials.MovieMaterial;

import flash.display.Sprite;

/**
 * This is used as a child in Sprite3D, they are created each time the sprite is resized.
 */
public class SpriteFace extends MyMesh {
    private var sprite:Sprite;
    private var bottomLeft:Number;
    private var bottomRight:Number;
    private var topLeft:Number;
    private var topRight:Number;

    public function SpriteFace(sprite:Sprite) {
        super();
        this.sprite = sprite;
        bottomLeft = v(0, 0, 0);
        bottomRight = v(sprite.width, 0, 0);
        topLeft = v(0, sprite.height, 0);
        topRight = v(sprite.width, sprite.height, 0);
        uv(0, 0);
        uv(1, 0);
        uv(0, 1);
        uv(1, 1);
        const movieMaterial:MovieMaterial = new MovieMaterial(sprite);
        movieMaterial.smooth = true;
        plane(bottomLeft, bottomRight, topRight, topLeft, movieMaterial);
    }

    public function get width():Number {
        return sprite.width;
    }

    public function get height():Number {
        return sprite.height;
    }
}
}