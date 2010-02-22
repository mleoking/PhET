package edu.colorado.phet.densityflex.view {
import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityflex.*;

import Box2D.Dynamics.b2Body;

import away3d.primitives.*;

import edu.colorado.phet.densityflex.model.Cuboid;
import edu.colorado.phet.densityflex.model.DensityModel;
import edu.colorado.phet.densityflex.model.Listener;

public class CuboidNode extends ObjectContainer3D implements Pickable, Listener{

    private var cuboid:Cuboid;

    private var cube : PickableCube;

    public function CuboidNode( cuboid:Cuboid ) : void {
        this.cuboid = cuboid;
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
        this.useHandCursor = true;
        cuboid.addListener(this);
        addNodes();
    }

    public function addNodes() : void {
        trace( "super addNodes()");
        cube = new PickableCube(this);
        cube.width = cuboid.getWidth() * DensityModel.DISPLAY_SCALE;
        cube.height = cuboid.getHeight() * DensityModel.DISPLAY_SCALE;
        cube.depth = cuboid.getDepth() * DensityModel.DISPLAY_SCALE;
        cube.segmentsH = 2;
        cube.segmentsW = 2;
        addChild(cube);
        cube.useHandCursor = true;
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

    public function getCube():PickableCube {
        return cube;
    }

    public function remove():void {
    }
}
}