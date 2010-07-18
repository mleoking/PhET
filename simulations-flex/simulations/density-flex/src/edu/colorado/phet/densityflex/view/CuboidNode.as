package edu.colorado.phet.densityflex.view {
import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityflex.*;

import Box2D.Dynamics.b2Body;

import away3d.primitives.*;

import edu.colorado.phet.densityflex.model.Cuboid;
import edu.colorado.phet.densityflex.model.DensityModel;
import edu.colorado.phet.densityflex.model.Listener;
import edu.colorado.phet.densityflex.model.ShapeChangeListener;

public class CuboidNode extends ObjectContainer3D implements Pickable, Listener, ShapeChangeListener{

    private var cuboid:Cuboid;

    private var cube : PickableCube;

    public function CuboidNode( cuboid:Cuboid ) : void {
        this.cuboid = cuboid;
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
        this.useHandCursor = true;
        cuboid.addListener(this);
        cuboid.addShapeChangeListener(this);
        addNodes();
    }

    public function addNodes() : void {
        trace( "super addNodes()");
        cube = createCube();
        addChild(cube);
    }

    protected function createCube():PickableCube {
        cube = new PickableCube(this);
        updateShape();
        cube.segmentsH = 2;
        cube.segmentsW = 2;
        cube.useHandCursor = true;
        return cube;
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

    public function shapeChanged():void {
        updateShape();
    }

    private function updateShape():void {
        cube.width = cuboid.getWidth() * DensityModel.DISPLAY_SCALE;
        cube.height = cuboid.getHeight() * DensityModel.DISPLAY_SCALE;
        cube.depth = cuboid.getDepth() * DensityModel.DISPLAY_SCALE;
    }
}
}