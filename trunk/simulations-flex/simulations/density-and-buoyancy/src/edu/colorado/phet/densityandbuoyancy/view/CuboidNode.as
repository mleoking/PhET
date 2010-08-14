package edu.colorado.phet.densityandbuoyancy.view {
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.model.Cuboid;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Listener;
import edu.colorado.phet.densityandbuoyancy.model.ShapeChangeListener;

public class CuboidNode extends DensityObjectNode implements Pickable, Listener, ShapeChangeListener {

    private var cuboid:Cuboid;

    private var cube:PickableCube;

    public function CuboidNode(cuboid:Cuboid):void {
        super(cuboid);
        this.cuboid = cuboid;
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
        this.useHandCursor = true;
        cuboid.addListener(this);
        cuboid.addShapeChangeListener(this);
        addNodes();
    }

    public function addNodes():void {
        trace("super addNodes()");
        cube = createCube();
        addChild(cube);
    }

    override public function getArrowOriginZ():Number {
        return cuboid.getDepth() / 2.0;
    }

    protected function createCube():PickableCube {
        cube = new PickableCube(this);
        updateShape();
        cube.segmentsH = 2;
        cube.segmentsW = 2;
        cube.useHandCursor = true;
        return cube;
    }

    public function setPosition(x:Number, y:Number):void {
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

    protected function updateShape():void {
        cube.width = cuboid.getWidth() * DensityModel.DISPLAY_SCALE;
        cube.height = cuboid.getHeight() * DensityModel.DISPLAY_SCALE;
        cube.depth = cuboid.getDepth() * DensityModel.DISPLAY_SCALE;
    }
}
}