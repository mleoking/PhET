package edu.colorado.phet.densityandbuoyancy.view {
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.model.Cuboid;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Listener;
import edu.colorado.phet.densityandbuoyancy.model.ShapeChangeListener;

public class CuboidNode extends DensityObjectNode implements Pickable, ShapeChangeListener {

    private var cuboid:Cuboid;

    public function CuboidNode(cuboid:Cuboid,view:AbstractDensityModule):void {
        super(cuboid,view);
        this.cuboid = cuboid;
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
        this.useHandCursor = true;
        cuboid.addShapeChangeListener(this);
    }

    override public function getArrowOriginZ():Number {
        return cuboid.getDepth() / 2.0;
    }

    public override function setPosition(x:Number, y:Number):void {
        cuboid.setPosition(x / DensityModel.DISPLAY_SCALE, y / DensityModel.DISPLAY_SCALE);
    }

    public function getCuboid():Cuboid {
        return cuboid;
    }

    public override function getBody():b2Body {
        return cuboid.getBody();
    }

    public function shapeChanged():void {
        updateGeometry();
    }

    public override function updateGeometry():void {
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
    }
}
}