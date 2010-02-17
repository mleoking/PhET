package edu.colorado.phet.densityflex {

import Box2D.Dynamics.b2Body;

import away3d.primitives.*;

public class CuboidNode extends Cube implements Pickable, Listener{

    private var cuboid:Cuboid;

    public function CuboidNode( cuboid:Cuboid ) : void {
        this.cuboid = cuboid;
        this.width = cuboid.getWidth() * DensityModel.DISPLAY_SCALE;
        this.height = cuboid.getHeight() * DensityModel.DISPLAY_SCALE;
        this.depth = cuboid.getDepth() * DensityModel.DISPLAY_SCALE;
        this.segmentsH = 2;
        this.segmentsW = 2;
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
        this.useHandCursor = true;
        cuboid.addListener(this);
    }

    public function setPosition( x:Number, y:Number ): void {
        cuboid.setPosition(x / DensityModel.DISPLAY_SCALE, y / DensityModel.DISPLAY_SCALE);
    }

    public function update():void {
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
    }

    public function getCuboid():Cuboid {
        return cuboid;
    }

    public function getBody():b2Body {
        return cuboid.getBody();
    }

    public function remove():void {
    }
}
}