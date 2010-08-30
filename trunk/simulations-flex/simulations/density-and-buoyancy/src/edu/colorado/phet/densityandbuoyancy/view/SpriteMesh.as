package edu.colorado.phet.densityandbuoyancy.view {
import away3d.materials.MovieMaterial;

import flash.display.Sprite;

public class SpriteMesh extends MyMesh {
    private var sprite:Sprite;

    public function SpriteMesh(sprite:Sprite) {
        super();
        this.sprite = sprite;
        var BOTTOM_LEFT:Number = v(0, 0, 0);
        var BOTTOM_RIGHT:Number = v(sprite.width, 0, 0);
        var TOP_LEFT:Number = v(0, sprite.height, 0);
        var TOP_RIGHT:Number = v(sprite.width, sprite.height, 0);
        uv(0, 0);
        uv(1, 0);
        uv(0, 1);
        uv(1, 1);
        const movieMaterial:MovieMaterial = new MovieMaterial(sprite);
        movieMaterial.smooth = true;
        plane(BOTTOM_LEFT, BOTTOM_RIGHT, TOP_RIGHT, TOP_LEFT, movieMaterial);
    }

    public function get width():Number {
        return sprite.width;
    }

    public function get height():Number {
        return sprite.height;
    }
}
}