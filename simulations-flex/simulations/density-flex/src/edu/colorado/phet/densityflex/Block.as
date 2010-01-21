package edu.colorado.phet.densityflex {

import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.b2Body;
import Box2D.Dynamics.b2BodyDef;

import flash.geom.ColorTransform;

/**
 * This class represents the model object for a block.
 */
public class Block {
    private var mass : Number;
    private var density : Number;
    private var width : Number;
    private var height: Number;
    private var depth : Number;
    private var x:Number;
    private var y:Number;
    private var z:Number;
    private var color:ColorTransform;
    private var listeners: Array;
    private var model : DensityModel;

    private var body : b2Body;

    public function Block( density : Number, size : Number, x:Number, y:Number, color : ColorTransform, model : DensityModel ) : void {
        this.width = size;
        this.height = size;
        this.depth = size;
        this.x = x;
        this.y = y;
        this.z = size / 2 + 1.01;
        this.density = density;
        this.mass = size * size * size * density;
        this.color = color;
        this.model = model;
        this.listeners = new Array();

        initEngineModel();
    }

    private function initEngineModel():void {
        var bodyDef:b2BodyDef = new b2BodyDef();
        bodyDef.position.Set(this.x, this.y);
        bodyDef.fixedRotation = true;
        body = model.getWorld().CreateBody(bodyDef);

        var shapeDef:b2PolygonDef = new b2PolygonDef();
        shapeDef.SetAsBox(width / 2, height / 2);
        shapeDef.density = density;
        shapeDef.friction = 0.3;
        shapeDef.restitution = 0;
        body.CreateShape(shapeDef);
        body.SetMassFromShapes();
    }

    public function addListener( listener:Listener ):void {
        listeners.push(listener);
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

    public function getX():Number {
        return x;
    }

    public function getY():Number {
        return y;
    }

    public function getZ():Number {
        return z;
    }

    public function getTopY() : Number {
        return y + height / 2;
    }

    public function getBottomY() : Number {
        return y - height / 2;
    }

    public function getVolume() : Number {
        return width * height * depth;
    }

    public function getColor():ColorTransform {
        return color;
    }

    public function getMass():Number {
        return mass;
    }

    public function update():void {
        setPosition(body.GetPosition().x, body.GetPosition().y);
    }

    public function setPosition( x:Number, y:Number ): void {
        this.x = x;
        this.y = y;

        if ( body.GetPosition().x != x || body.GetPosition().y != y ) {
            body.SetXForm(new b2Vec2(x, y), 0);
        }

        //todo: notify listeners
        for each ( var listener:Listener in listeners ) {
            listener.update();
        }
    }

    public function getBody() : b2Body {
        return body;
    }
}
}