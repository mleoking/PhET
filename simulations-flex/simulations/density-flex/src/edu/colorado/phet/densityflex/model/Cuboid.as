package edu.colorado.phet.densityflex.model {
import edu.colorado.phet.densityflex.*;

import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Dynamics.b2BodyDef;

public class Cuboid extends MovableModel{

    private var density : Number;
    private var width : Number;
    private var height: Number;
    private var depth : Number;

    /**
     * Arbitrary cuboid
     * @param density
     * @param width
     * @param height
     * @param depth
     * @param x
     * @param y
     * @param model
     */
    public function Cuboid( density : Number, width:Number, height:Number, depth:Number, x:Number, y:Number, model : DensityModel ) {
        super(x, y, depth / 2 + 1.01, model);

        this.density = density;
        this.width = width;
        this.height = height;
        this.depth = depth;

        initEngineModel();
    }

    private function initEngineModel():void {
        var bodyDef:b2BodyDef = new b2BodyDef();
        bodyDef.position.Set(getX(), getY());
        bodyDef.fixedRotation = true;
        bodyDef.massData.mass = getMass();
        bodyDef.massData.center.SetZero();
        bodyDef.massData.I = 1.0; // rotational inertia shouldn't matter
        setBody(getModel().getWorld().CreateBody(bodyDef));

        var shapeDef:b2PolygonDef = new b2PolygonDef();
        shapeDef.SetAsBox(width / 2, height / 2);
        shapeDef.density = getDensity();
        shapeDef.friction = 0.3;
        shapeDef.restitution = 0;
        getBody().CreateShape(shapeDef);
        getBody().SetUserData(this);
    }

    public function getWidth():Number {
        return width;
    }

    public function getHeight():Number {
        return height;
    }

    public function getDepth():Number {
        return depth;
    }

    public function getDensity():Number {
        return density;
    }

    public function getTopY() : Number {
        return getY() + height / 2;
    }

    public function getBottomY() : Number {
        return getY() - height / 2;
    }

    public function getVolume() : Number {
        return width * height * depth;
    }

    public function getMass() : Number {
        return getVolume() * getDensity();
    }


}
}