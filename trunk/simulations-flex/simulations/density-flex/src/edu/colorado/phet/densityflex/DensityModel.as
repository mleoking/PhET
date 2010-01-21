package edu.colorado.phet.densityflex {
import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Collision.b2AABB;
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.b2Body;
import Box2D.Dynamics.b2BodyDef;
import Box2D.Dynamics.b2World;

import flash.geom.ColorTransform;

import mx.controls.Alert;

public class DensityModel {
    private var blocks : Array;
    private var poolWidth : Number = 15;
    private var poolHeight : Number = 7.5;
    private var poolDepth : Number = 5;
    private var waterHeight : Number = 5.5;
    private static var BOUNDS : Number = 50;
    private var volume : Number = poolWidth * poolDepth * waterHeight;

    public static var STEPS_PER_FRAME : Number = 5;

    public static var DISPLAY_SCALE : Number = 100.0;

    private var world : b2World;

    public function DensityModel() {
        blocks = new Array();

        initWorld();
        createGround();

        blocks.push(new Block(5, 2, 4.5, 0, new ColorTransform(1, 0, 0), this));
        blocks.push(new Block(0.1, 1, 1.5, 0, new ColorTransform(0, 1, 0), this));
        blocks.push(new Block(1, 3, -1.5, 0, new ColorTransform(0, 0, 1), this));
        blocks.push(new Block(0.5, 2, -4.5, 0, new ColorTransform(1, 1, 1), this));
    }

    private function createGround():void {
        var groundBodyDef:b2BodyDef = new b2BodyDef();
        groundBodyDef.position.Set(0, 0);

        var groundBody:b2Body = world.CreateBody(groundBodyDef);

        var groundShapeDef:b2PolygonDef = new b2PolygonDef();
        groundShapeDef.SetAsOrientedBox(poolWidth / 2, 50, new b2Vec2(0, -50 - poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2, poolHeight, new b2Vec2(-(poolWidth / 2 + BOUNDS / 2), -poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2, poolHeight, new b2Vec2((poolWidth / 2 + BOUNDS / 2), -poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);
    }

    private function initWorld():void {
        var worldBox : b2AABB = new b2AABB();
        worldBox.lowerBound.Set(-BOUNDS, -BOUNDS);
        worldBox.upperBound.Set(BOUNDS, BOUNDS);
        var gravity : b2Vec2 = new b2Vec2(0, -9.8);
        var doSleep : Boolean = false;
        world = new b2World(worldBox, gravity, doSleep);
    }

    public function getBlocks() : Array {
        return blocks;
    }

    public function step() : void {
        for ( var i : Number = 0; i < STEPS_PER_FRAME; i++ ) {
            world.Step(1 / (30 * STEPS_PER_FRAME), 10);
            var block : Block;
            for each( block in blocks ) {
                block.update();
            }
            updateWater();
            var waterY : Number = -poolHeight + waterHeight;
            for each( block in blocks ) {
                if ( waterY < block.getBottomY() ) {
                    continue;
                }
                var submergedVolume : Number;
                if ( waterY > block.getTopY() ) {
                    submergedVolume = block.getVolume();
                }
                else {
                    submergedVolume = (waterY - block.getBottomY() ) * block.getWidth() * block.getDepth();
                }
                // TODO: add in liquid density
                var body : b2Body = block.getBody();
                body.ApplyForce(new b2Vec2(0, 9.8 * submergedVolume), body.GetPosition());

                var dragForce:b2Vec2 = body.GetLinearVelocity().Copy();
                dragForce.Multiply(-2 * submergedVolume);
                body.ApplyForce(dragForce, body.GetPosition());
            }
        }
    }

    public function updateWater() : void {
        var block : Block;
        var sortedHeights : Array = new Array();
        for ( var key : String in blocks ) {
            block = blocks[key];
            var top : Object = new Object();
            top.y = block.getTopY();
            top.pos = 1;
            top.block = block;
            var bottom : Object = new Object();
            bottom.y = block.getBottomY();
            bottom.pos = 0;
            bottom.block = block;
            sortedHeights.push(top);
            sortedHeights.push(bottom);
        }
        sortedHeights.sortOn(["y"], [Array.NUMERIC]);

        var curHeight : Number = 0;
        var volumeToGo : Number = volume;
        var crossSection : Number = poolWidth * poolDepth;

        for ( var i : String in sortedHeights ) {
            var ob : Object = sortedHeights[i];
            var pos : Number = ob.pos;
            var by : Number = ob.y + poolHeight;
            block = ob.block;
            var idealHeight : Number = volumeToGo / crossSection + curHeight;
            if ( idealHeight < by ) {
                curHeight = idealHeight;
                volumeToGo = 0;
                break;
            }
            var heightGain : Number = by - curHeight;
            volumeToGo -= crossSection * heightGain;
            curHeight = by;
            if ( pos == 0 ) {
                // bottom of block
                crossSection -= block.getWidth() * block.getDepth();
            }
            else {
                // top of block
                crossSection += block.getWidth() * block.getDepth();
            }
        }

        // fill it up the rest of the way
        curHeight += volumeToGo / crossSection;

        waterHeight = curHeight;
    }

    public function getPoolHeight() : Number {
        return poolHeight;
    }

    public function getWaterHeight() : Number {
        return waterHeight;
    }

    public function getPoolWidth() : Number {
        return poolWidth;
    }

    public function getPoolDepth() : Number {
        return poolDepth;
    }

    public function getWorld():b2World {
        return world;
    }
}
}